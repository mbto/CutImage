package github.com.mbto.cutimage;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static github.com.mbto.cutimage.Constants.supportedExtensions;

@Getter
@ToString
public class Settings {
    @Parameter(names = "-s", description = "Source directory path (with images)",
            validateValueWith = NotEmptyDirectoryPathValidator.class,
            required = true, order = 0)
    private Path sourceDirPath;

    @Parameter(names = "-o", description = "Output directory path", order = 1)
    private Path outputDirPath = Paths.get("").toAbsolutePath();

    @Getter
    @Setter
    private Path resolvedOutputDirPath;

    @Parameter(names = "-rs", description = "Enable/disable recursive walk in source directory", order = 2)
    private boolean recursiveSourceDirEnabled;

    @Parameter(names = "-x", description = "Count of pictures on axis X",
            required = true, validateWith = PositiveInteger.class, order = 3)
    private int axisX = 6;

    @Parameter(names = "-y", description = "Count of pictures on axis Y",
            required = true, validateWith = PositiveInteger.class, order = 4)
    private int axisY = 3;

    @Parameter(names = "-t", description = "Timeout of operations in minutes",
            validateWith = PositiveInteger.class, order = 5)
    private int timeoutMins = 10;

    @Parameter(names = "-e",
            description = "List filtered extensions in source directory. Example: jpg,jpeg,png,bmp,gif",
            validateValueWith = ExtensionValidator.class, order = 6)
    private List<String> filteredExtensions = new ArrayList<>(supportedExtensions);
}