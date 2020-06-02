package github.com.mbto.cutimage;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CutImageTest {
    @Test
    public void nonRecursitveTest() throws Exception {
        TestSettings testSettings = buildTestSettings();

        String[] args = {
                "-s", testSettings.getSourceDirPathString(),
                "-o", testSettings.getTargetDirPathString(),
                "-rs",
                "-x", "6",
                "-y", "3",
                "-e", "jpg,jpeg,png,bmp,gif",
        };

        Runner.main(args);
    }

    private TestSettings buildTestSettings() {
        Path sourceDirPath = Paths.get("build", "resources", "test", "images")
                .toAbsolutePath();

        Path targetDirPath = Optional
                .ofNullable(System.getProperty("java.io.tmpdir"))
                .map(dir -> Paths.get(dir).resolve(CutImageTest.class.getSimpleName()))
                .orElseThrow(() -> new RuntimeException("Empty 'java.io.tmpdir' environment variable"));

        String sourDirPathString = sourceDirPath.toString();
        String targetDirPathString = targetDirPath.toString();

        System.out.println("sourDirPathString=" + sourDirPathString);
        System.out.println("targetDirPathString=" + targetDirPathString);

        return new TestSettings(sourDirPathString, targetDirPathString);
    }
}