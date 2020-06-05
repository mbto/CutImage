package github.com.mbto.cutimage;

import lombok.Getter;

@Getter
public class TestSettings {
    private final String sourceDirPathString;
    private final String targetDirPathString;

    public TestSettings(String sourceDirPathString, String targetDirPathString) {
        this.sourceDirPathString = sourceDirPathString;
        this.targetDirPathString = targetDirPathString;

        System.out.println("sourceDirPathString=" + sourceDirPathString);
        System.out.println("targetDirPathString=" + targetDirPathString);
    }
}