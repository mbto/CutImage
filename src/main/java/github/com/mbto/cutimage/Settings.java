package github.com.mbto.cutimage;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static github.com.mbto.cutimage.Constants.supportedExtensions;

@Getter
public class Settings {
    @Parameter(names = "-s", description = "Source directory path (with images)",
            validateValueWith = NotEmptyDirectoryPathValidator.class,
            required = true, echoInput = true)
    private Path sourceDirPath;

    @Parameter(names = "-o", description = "Output directory path")
    private Path outputDirPath;

    @Parameter(names = "-rs", description = "Enable/disable recursive walk in source directory")
    private boolean recursiveSourceDirEnabled;

    @Parameter(names = "-x", description = "Count of pictures on axis X",
            required = true, validateWith = PositiveInteger.class)
    private int axisX;

    @Parameter(names = "-y", description = "Count of pictures on axis Y",
            required = true, validateWith = PositiveInteger.class)
    private int axisY;

    @Parameter(names = "-t", description = "Timeout of operations. Default: 10 minutes",
            validateWith = PositiveInteger.class)
    private int timeoutMins = 10;

    @Parameter(names = "-e",
            description = "List filtered extensions in source directory. Example: jpg,jpeg,png,bmp,gif",
            validateValueWith = ExtensionValidator.class)
    private List<String> filteredExtensions = new ArrayList<>(supportedExtensions);

    @Setter
    private String outputDirectoryPostfix;
}