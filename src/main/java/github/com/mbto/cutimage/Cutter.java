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
    private final int picNum;

    public Cutter(Stats stats, Path imagesOutputDirectory,
                  BufferedImage sourceImage, String filename, String extension,
                  int x, int y, int newWidth, int newHeight, int picNum) {
        this.stats = stats;
        this.imagesOutputDirectory = imagesOutputDirectory;
        this.sourceImage = sourceImage;
        this.filename = filename;
        this.extension = extension;
        this.x = x;
        this.y = y;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.picNum = picNum;
    }

    @Override
    public void run() {
        BufferedImage newImage = sourceImage.getSubimage(x * newWidth, y * newHeight, newWidth, newHeight);

        String picId = String.format("%03d;%03d;%03d", picNum, x + 1, y + 1);
        Path newImagePath = imagesOutputDirectory.resolve(filename + ";" + picId + "." + extension);

        try {
            ImageIO.write(newImage, extension, newImagePath.toFile());

            stats.getNewImages().incrementAndGet();
        } catch (Throwable e) {
            stats.getErrors().incrementAndGet();

            e.printStackTrace();
        }
    }
}