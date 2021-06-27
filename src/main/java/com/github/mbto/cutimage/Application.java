package com.github.mbto.cutimage;

import com.beust.jcommander.JCommander;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.github.mbto.cutimage.Constants.*;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ForkJoinPool.commonPool;

public class Application {
    public static void main(String[] argsRaw) {
        System.out.println(SOFTWARE_INFO);
        System.out.println("Supported extensions: " + supportedExtensions);
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors() + "\n");

        Args args = new Args();
        JCommander jCommander = JCommander.newBuilder()
                .programName(SOFTWARE_NAME)
                .addObject(args).build();
        try {
            jCommander.parse(argsRaw);
        } catch (Exception e) {
            jCommander.usage();
            System.err.println("Failed parse arguments, " + e);
            return;
        }

        long startEpoch = System.currentTimeMillis();
        Stats stats;
        try {
            stats = new Application().runApplication(args);
        } catch (Throwable e) {
            System.err.println("Failed in " + calcHumanDiff(startEpoch));
            e.printStackTrace();
            return;
        }
        System.out.println("Complete in " + calcHumanDiff(startEpoch));
        System.out.println(stats);
    }

    public Stats runApplication(Args args) throws Exception {
        String outputDirectoryPostfix = LocalDateTime.now().format(DDMMYYYY_HHMMSS_PATTERN);

        Path outputDirPath = args.getOutputDirPath()
                .resolve("Cutted Images")
                .resolve(outputDirectoryPostfix + " " + randomUUID().toString().substring(0, 6))
                .toAbsolutePath();

        args.setResolvedOutputDirPath(outputDirPath);

        System.out.println(args);

        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }

        Stats stats = new Stats();
        RecursiveWalker recursiveWalker = new RecursiveWalker(args, args.getSourceDirPath(), stats);

        commonPool().invoke(recursiveWalker);
        commonPool().awaitQuiescence(args.getTimeoutMins(), TimeUnit.MINUTES);

        return stats;
    }
}