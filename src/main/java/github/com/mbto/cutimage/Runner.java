package github.com.mbto.cutimage;

import com.beust.jcommander.JCommander;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static github.com.mbto.cutimage.Constants.supportedExtensions;
import static java.util.concurrent.ForkJoinPool.commonPool;

public class Runner {
    public static void main(String[] arg) throws Exception {
        System.out.println("CutImage v2.0 https://github.com/mbto/CutImage");
        System.out.println("Supported extensions: " + supportedExtensions);
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("ForkJoinPool parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        Settings settings = new Settings();
        JCommander.newBuilder().addObject(settings).build();

        Stats stats = new Stats();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                ForkJoinPool forkJoinPool = commonPool();
                forkJoinPool.shutdown();
                forkJoinPool.awaitTermination(5, TimeUnit.SECONDS);

                printStats(stats);
            }
        });

        Path outputDirPath = settings.getOutputDirPath();

        if(Objects.isNull(outputDirPath)) {
            outputDirPath = getDefaultOutputDirPath();
        }

        outputDirPath = outputDirPath.toRealPath();

        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }

        settings.setOperationDateTime(LocalDateTime.now().format(Constants.DDMMYYYY_HHMMSS_PATTERN));

        RecursiveWalker recursiveWalker = new RecursiveWalker(settings, outputDirPath, stats);

        long start = System.currentTimeMillis();

        commonPool().invoke(recursiveWalker);
        commonPool().awaitQuiescence(settings.getTimeoutMins(), TimeUnit.MINUTES);

        long diff = (System.currentTimeMillis() - start) / 1000;

        System.out.println("Complete: " + (diff / 60) + "m " + (diff % 60) + "s");
        printStats(stats);
    }

    private static void printStats(Stats stats) {
        System.out.println("Stats: " + stats);
    }

    private static Path getDefaultOutputDirPath() {
        String classPath = System.getProperty("java.class.path");

        int semicolon = classPath.lastIndexOf(';');
        if (semicolon > -1)
            classPath = classPath.substring(semicolon + 1);

        return Paths.get(classPath).toAbsolutePath().getParent();
    }
}