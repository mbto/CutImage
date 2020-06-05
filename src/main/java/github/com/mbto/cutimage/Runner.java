package github.com.mbto.cutimage;

import com.beust.jcommander.JCommander;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static github.com.mbto.cutimage.Constants.DDMMYYYY_HHMMSS_PATTERN;
import static github.com.mbto.cutimage.Constants.supportedExtensions;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ForkJoinPool.commonPool;

public class Runner {
    public static void main(String[] args) throws Exception {
        System.out.println("CutImage v2.0 https://github.com/mbto/CutImage");
        System.out.println("Supported extensions: " + supportedExtensions);
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors() + "\n");

        Settings settings = new Settings();
        JCommander jCommander = JCommander.newBuilder()
                .programName("CutImage")
                .addObject(settings).build();
        try {
            jCommander.parse(args);
        } catch (Exception e) {
            jCommander.usage();

            System.err.println("Exception: " + e.getMessage());
            System.exit(1);
        }

        new Runner().runApplication(settings);
    }

    public Stats runApplication(Settings settings) throws Exception {
        Stats stats = new Stats();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @SneakyThrows
//            @Override
//            public void run() {
//                ForkJoinPool forkJoinPool = commonPool();
//                forkJoinPool.shutdown();
//                forkJoinPool.awaitTermination(5, TimeUnit.SECONDS);
//            }
//        });

        String outputDirectoryPostfix = LocalDateTime.now().format(DDMMYYYY_HHMMSS_PATTERN);

        Path outputDirPath = settings.getOutputDirPath()
                .resolve("Cutted Images")
                .resolve(outputDirectoryPostfix + " " + randomUUID().toString().substring(0, 6))
                .toAbsolutePath();

        settings.setResolvedOutputDirPath(outputDirPath);

        System.out.println(settings);

        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }

        RecursiveWalker recursiveWalker = new RecursiveWalker(settings, settings.getSourceDirPath(), stats);

        long start = System.currentTimeMillis();

        commonPool().invoke(recursiveWalker);
        commonPool().awaitQuiescence(settings.getTimeoutMins(), TimeUnit.MINUTES);

        long diff = (System.currentTimeMillis() - start) / 1000;

        System.out.println("Complete: " + (diff / 60) + "m " + (diff % 60) + "s");
        System.out.println(stats);

        return stats;
    }
}