package com.github.mbto.cutimage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.ForkJoinPool.commonPool;

public class ImagePreparer implements Runnable {
    private final Args args;
    private final Path imagePath;
    private final String filename;
    private final String extension;
    private final Stats stats;

    public ImagePreparer(Args args, Path imagePath, String filename, String extension, Stats stats) {
        this.args = args;
        this.imagePath = imagePath;
        this.filename = filename;
        this.extension = extension;
        this.stats = stats;
    }

    @Override
    public void run() {
        BufferedImage sourceImage;
        try {
            sourceImage = ImageIO.read(imagePath.toFile());
        } catch (Throwable e) {
            stats.getErrors().incrementAndGet();

            System.err.println("Exception at '" + imagePath.toAbsolutePath() + "', message: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        int axisX = args.getAxisX();
        int axisY = args.getAxisY();
        Path outputDirPath = args.getResolvedOutputDirPath();

        int width = sourceImage.getWidth(null); // X
        int height = sourceImage.getHeight(null); // Y

        int newWidth = (int) ((float) width / axisX);
        int newHeight = (int) ((float) height / axisY);
        if (newWidth < 1 || newHeight < 1) {
            stats.getErrors().incrementAndGet();

            System.err.println(imagePath.toAbsolutePath() + " New width (" + newWidth + ") "
                    + "and height (" + newHeight + ") can't be < 1. Size: " + width + "x" + height);

            return;
        }

        String cropInfo = newWidth + "x" + newHeight + " (" + (axisX + "x" + axisY) + ")";

        Path imagesOutputDirectory = outputDirPath.resolve(filename
                + " " + cropInfo
                + " " + new UUID(ThreadLocalRandom.current().nextLong(),
                            ThreadLocalRandom.current().nextLong()
                        ).toString().substring(0, 6)
        );
        try {
            Files.createDirectory(imagesOutputDirectory);
        } catch (Throwable e) {
            stats.getErrors().incrementAndGet();

            e.printStackTrace();
            return;
        }

        int total = axisX * axisY;
        System.out.println(/*Thread.currentThread().getName() + " " +*/
                "'" + args.getSourceDirPath().relativize(imagePath) + "'"
                + " " + width + "x" + height + " -> "
                + cropInfo
                + " '" + imagesOutputDirectory.getFileName().toString() + "'"
                + " " + total + " pictures");

        for (int picNum = 0, y = 0; y < axisY; y++) {
            for (int x = 0; x < axisX; x++) {
                ++picNum;
                commonPool().submit(new Cutter(stats, imagesOutputDirectory, sourceImage, filename,
                        extension, x, y, newWidth, newHeight, picNum));
            }
        }
    }
}