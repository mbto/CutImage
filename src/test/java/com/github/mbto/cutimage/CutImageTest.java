package com.github.mbto.cutimage;

import com.beust.jcommander.JCommander;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.mbto.cutimage.Constants.SOFTWARE_NAME;
import static org.junit.Assert.assertEquals;

public class CutImageTest {
    private static final Path integrationTestsPath = Paths.get("R:\\IntegrationTests");

    private TestSettings buildTestSettings() {
        if(Files.isDirectory(integrationTestsPath)) {
            return new TestSettings(integrationTestsPath.resolve(SOFTWARE_NAME).resolve("source"),
                    integrationTestsPath.resolve(SOFTWARE_NAME).resolve("output"));
        }

        return new TestSettings(Paths.get("build", "resources", "test", "images").toAbsolutePath(),
                Paths.get("build", "fromTest").toAbsolutePath());
    }

    @Test
    public void recursitveTest() throws Exception {
        TestSettings testSettings = buildTestSettings();

        String[] args = {
                "-s", testSettings.getSourceDirPathString(),
                "-o", testSettings.getTargetDirPathString(),
                "-rs", "true",
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
                "-rs", "true",
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

    private Stats runApplication(String[] argsRaw) throws Exception {
        Args args = new Args();
        JCommander.newBuilder().addObject(args).build().parse(argsRaw);

        return new Application().runApplication(args);
    }

    private void runAsserts(Stats stats, int totalImages, int directories, int newImages, int errors) {
        assertEquals(totalImages, stats.calcImagesCount());
        assertEquals(directories, stats.getDirectories().get());
        assertEquals(newImages, stats.getNewImages().get());
        assertEquals(errors, stats.getErrors().get());
    }
}