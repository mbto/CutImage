package github.com.mbto.cutimage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.concurrent.ForkJoinPool.commonPool;

public class ImagePreparer implements Runnable {
    private final Settings settings;
    private final Path imagePath;
    private final String filename;
    private final String extension;
    private final Stats stats;

    public ImagePreparer(Settings settings, Path imagePath, String filename, String extension, Stats stats) {
        this.settings = settings;
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

            e.printStackTrace();
            return;
        }

        int axisX = settings.getAxisX();
        int axisY = settings.getAxisY();
        Path outputDirPath = settings.getOutputDirPath();

        int width = sourceImage.getWidth(null); // X
        int height = sourceImage.getHeight(null); // Y

        int newWidth = (int) ((float) width / axisX);
        int newHeight = (int) ((float) height / axisY);
        if (newWidth < 1 || newHeight < 1) {
            System.out.println(imagePath.toAbsolutePath() + " New width (" + newWidth + ") "
                    + "and height (" + newHeight + ") can't be < 1. Size: " + width + "x" + height);
            return;
        }

        String cropInfo = newWidth + "x" + newHeight + " (" + (axisX + "x" + axisY) + ")";

        Path imagesOutputDirectory = outputDirPath.resolve(filename + " " + cropInfo + " " + settings.getOperationDateTime());
        try {
            Files.createDirectory(imagesOutputDirectory);
        } catch (Throwable e) {
            stats.getErrors().incrementAndGet();

            e.printStackTrace();
            return;
        }

        int total = axisX * axisY;
        System.out.println(imagePath.toAbsolutePath() + " " + width + "x" + height + " -> "
                + cropInfo + " '" + imagesOutputDirectory + "/' " + total + " pictures");

        for (int picNum = 0, y = 0; y < axisY; y++) {
            for (int x = 0; x < axisX; x++) {
                ++picNum;
                commonPool().submit(new Cutter(stats, imagesOutputDirectory, sourceImage, filename,
                        extension, x, y, newWidth, newHeight, picNum));
            }
        }
    }

}