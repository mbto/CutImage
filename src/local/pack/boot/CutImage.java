package local.pack.boot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class CutImage {

    private static Set<String> supportFormats = Stream.of("jpg", "jpeg", "png", "bmp", "gif").collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    private static BufferedImage sourceImage;
    private static String filename;
    private static String extension;
    private static Path outDir;

    public static void main(String[] args) {
        System.out.println("CutImage v1.0 multi-threaded @noga $2016 | (Supported extensions: " + supportFormats.toString() + ")");

        Path path;// = Paths.get("C:\\temp\\Москва.jpg");
        int axisX, axisY;

        try {
            path = Paths.get(args[0]);
            axisX = Integer.parseInt(args[1]);
            axisY = Integer.parseInt(args[2]);
        } catch (Exception ex) {
            exitWithIllegalArguments();
            return;
        }

        if (axisX < 1 || axisY < 1) {
            exitWithIllegalArguments();
        }

        filename = path.getFileName().toString();
        int dot = filename.lastIndexOf(".");
        extension = filename.substring(dot + 1).toLowerCase();

        if (dot == -1 || !supportFormats.contains(extension)) {
            System.out.println("Unknown picture extension");
            return;
        }
        System.out.println("Picture: '" + path.toAbsolutePath() + "'");

        try {
            sourceImage = ImageIO.read(path.toFile());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int width = sourceImage.getWidth(null); // X
        int height = sourceImage.getHeight(null); // Y
        System.out.println("Size: " + width + "x" + height);

        int newWidth = (int) ((float) width / (float) axisX);
        int newHeight = (int) ((float) height / (float) axisY);
        if (newWidth < 1 || newHeight < 1) {
            exitWithIllegalArguments();
        }

        String crop = newWidth + "x" + newHeight + " (" + (axisX + "x" + axisY) + ")";
        System.out.println("Crop: " + crop);

        try {
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH;mm;ss");
            outDir = Files.createDirectory(getWorkPath().resolve(filename + " " + crop + " " + ZonedDateTime.now().format(pattern)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Output dir: '" + outDir + "'");

//        int parallelism = Math.max((axisX * axisY) / 2, 1);
        int parallelism = Runtime.getRuntime().availableProcessors() * 4;
        ExecutorService service = Executors.newFixedThreadPool(parallelism);
//        ExecutorService service = Executors.newWorkStealingPool(parallelism);
//        ForkJoinPool service = new ForkJoinPool(parallelism);

        int total = axisX * axisY;
        List<Future> tasks = new ArrayList<>(total);

        System.out.println("Making " + total + " pictures with " + parallelism + " threads");
        long start = ZonedDateTime.now().toEpochSecond();
        for (int picNum = 0, y = 0; y < axisY; y++) {
            for (int x = 0; x < axisX; x++) {
                ++picNum;
                tasks.add(service.submit(getJob(x, y, newWidth, newHeight, picNum)));
            }
        }

        tasks.forEach(j -> {
            try {
                j.get();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });

        long diff = ZonedDateTime.now().toEpochSecond() - start;
        System.out.println("Complete: " + (diff / 60) + "m " + (diff % 60) + "s");

        service.shutdown();
    }

    private static Runnable getJob(int x, int y, int newWidth, int newHeight, int picNum) {
        return () -> {
            BufferedImage newImage = sourceImage.getSubimage(x * newWidth, y * newHeight, newWidth, newHeight);

            Formatter format = new Formatter().format("%03d;%03d;%03d", picNum, x + 1, y + 1);
            Path outFile = outDir.resolve(filename + ";" + format + "." + extension);

            try {
                ImageIO.write(newImage, extension, outFile.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private static Path getWorkPath() {
        String property = System.getProperty("java.class.path"); // jackson-xc-1.9.2.jar;C:\IntelliJ IDEA 2016.2.5\lib\idea_rt.jar
        int i = property.lastIndexOf(';');
        if (i > -1) property = property.substring(i + 1);
//        System.out.println("property java.class.path='" + property + "'");
        return Paths.get(property).toAbsolutePath().getParent();
    }

    private static void exitWithIllegalArguments() {
        System.out.println("Incorrect arguments. Use: path_to_picture axisX axisY");
        System.out.println("Example: C:\\temp\\Москва.jpg 4 4");
        System.exit(1);
    }
}
