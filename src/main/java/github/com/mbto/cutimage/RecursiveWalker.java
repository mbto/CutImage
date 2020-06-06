package github.com.mbto.cutimage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

import static java.util.concurrent.ForkJoinPool.commonPool;

public class RecursiveWalker extends RecursiveAction {
    private final Settings settings;
    private final Path nextDirectoryPath;
    private final Stats stats;

    public RecursiveWalker(Settings settings, Path nextDirectoryPath, Stats stats) {
        this.settings = settings;
        this.nextDirectoryPath = nextDirectoryPath;
        this.stats = stats;
    }

    @Override
    protected void compute() {
        final List<RecursiveWalker> internalWalks = new ArrayList<>();

        try {
            Files.walkFileTree(nextDirectoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                    if(!Files.isReadable(filePath))
                        return FileVisitResult.CONTINUE;

                    String filename = filePath.getFileName().toString();
                    int dot = filename.lastIndexOf(".");

                    if(dot == -1)
                        return FileVisitResult.CONTINUE;

                    String extension = filename.substring(dot + 1).toLowerCase();

                    if (!settings.getFilteredExtensions().contains(extension)) {
                        return FileVisitResult.CONTINUE;
                    }

                    Map<String, Integer> images = stats.getImages();
                    synchronized (images) {
                        Integer extensionsCount = images.getOrDefault(extension, 0);
                        images.put(extension, ++extensionsCount);
                    }

                    commonPool().submit(new ImagePreparer(settings, filePath, filename, extension, stats));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attrs) throws IOException {
                    if (dirPath.equals(RecursiveWalker.this.nextDirectoryPath)) {
                        stats.getDirectories().incrementAndGet();

                        return FileVisitResult.CONTINUE;
                    }

                    if(settings.isRecursiveSourceDirEnabled()) {
                        RecursiveWalker recursiveWalker = new RecursiveWalker(settings, dirPath, stats);
                        recursiveWalker.fork();

                        internalWalks.add(recursiveWalker);
                    }

                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path filePath, IOException exc) throws IOException {
                    stats.getErrors().incrementAndGet();

                    System.err.println("Failed visit '" + filePath.toAbsolutePath() + "'");

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Throwable e) {
            stats.getErrors().incrementAndGet();

            e.printStackTrace();
        }

        if(!internalWalks.isEmpty()) {
            for (RecursiveWalker internalWalk : internalWalks) {
                internalWalk.join();
            }
        }
    }
}