package github.com.mbto.cutimage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class Cutter implements Runnable {
    private final Stats stats;
    private final Path imagesOutputDirectory;
    private final BufferedImage sourceImage;
    private final String filename;
    private final String extension;
    private final int x;
    private final int y;
    private final int newWidth;
    private final int newHeight;
    private final int pictureNumber;

    public Cutter(Stats stats, Path imagesOutputDirectory,
                  BufferedImage sourceImage, String filename, String extension,
                  int x, int y, int newWidth, int newHeight, int pictureNumber) {
        this.stats = stats;
        this.imagesOutputDirectory = imagesOutputDirectory;
        this.sourceImage = sourceImage;
        this.filename = filename;
        this.extension = extension;
        this.x = x;
        this.y = y;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.pictureNumber = pictureNumber;
    }

    @Override
    public void run() {
        BufferedImage newImage = sourceImage.getSubimage(x * newWidth, y * newHeight, newWidth, newHeight);

        String pictureParams = String.format("%03d;%03d;%03d", pictureNumber, x + 1, y + 1);
        Path newImagePath = imagesOutputDirectory.resolve(filename + ";" + pictureParams + "." + extension);

        try {
            ImageIO.write(newImage, extension, newImagePath.toFile());

            stats.getNewImages().incrementAndGet();
        } catch (Throwable e) {
            stats.getErrors().incrementAndGet();

            e.printStackTrace();
        }
    }
}