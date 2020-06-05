package github.com.mbto.cutimage;

import java.nio.file.Path;
import java.util.Optional;

public class TestSettings {
    private final Path sourceDirPathString;
    private final Path targetDirPathString;

    public TestSettings(Path sourceDirPathString, Path targetDirPathString) {
        this.sourceDirPathString = sourceDirPathString;
        this.targetDirPathString = targetDirPathString;

        System.out.println("sourceDirPathString=" + sourceDirPathString);
        System.out.println("targetDirPathString=" + targetDirPathString);
    }

    public String getSourceDirPathString() {
        return Optional.ofNullable(sourceDirPathString).map(Path::toString).orElse("");
    }

    public String getTargetDirPathString() {
        return Optional.ofNullable(targetDirPathString).map(Path::toString).orElse("");
    }
}