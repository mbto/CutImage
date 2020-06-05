package github.com.mbto.cutimage;

import com.beust.jcommander.JCommander;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CutImageTest {
    private TestSettings buildTestSettings() {
        /* RAMDisk */
        Path sourceDirPath = Paths.get("R:\\source");
        Path targetDirPath = Paths.get("R:\\output");
//      Path sourceDirPath = Paths.get("build", "resources", "test", "images").toAbsolutePath();

        return new TestSettings(sourceDirPath.toString(), targetDirPath.toString());
    }

    @Test
    public void recursitveTest() throws Exception {
        TestSettings testSettings = buildTestSettings();

        String[] args = {
                "-s", testSettings.getSourceDirPathString(),
                "-o", testSettings.getTargetDirPathString(),
                "-rs",
                "-x", "6",
                "-y", "3",
                "-e", "jpg,jpeg,png,bmp,gif",
        };

        Stats stats = runApplication(args);
        runAsserts(stats, 41, 13, 738, 0);
    }

    @Test
    public void recursitveJpgTest() throws Exception {
        TestSettings testSettings = buildTestSettings();

        String[] args = {
                "-s", testSettings.getSourceDirPathString(),
                "-o", testSettings.getTargetDirPathString(),
                "-rs",
                "-x", "6",
                "-y", "3",
                "-e", "jpg",
        };

        Stats stats = runApplication(args);
        runAsserts(stats, 39, 13, 702, 0);
    }

    @Test
    public void nonRecursitveTest() throws Exception {
        TestSettings testSettings = buildTestSettings();

        String[] args = {
                "-s", testSettings.getSourceDirPathString(),
                "-o", testSettings.getTargetDirPathString(),
                "-x", "6",
                "-y", "3",
                "-e", "jpg,jpeg,png,bmp,gif",
        };

        Stats stats = runApplication(args);
        runAsserts(stats, 6, 1, 108, 0);
    }

    private Stats runApplication(String[] args) throws Exception {
        Settings settings = new Settings();
        JCommander.newBuilder().addObject(settings).build().parse(args);

        return new Runner().runApplication(settings);
    }

    private void runAsserts(Stats stats, int totalImages, int directories, int newImages, int errors) {
        assertEquals(totalImages, stats.calcImagesCount());
        assertEquals(directories, stats.getDirectories().get());
        assertEquals(newImages, stats.getNewImages().get());
        assertEquals(errors, stats.getErrors().get());
    }
}