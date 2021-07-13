package com.github.mbto.cutimage;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.github.mbto.cutimage.Constants.supportedExtensions;

@Getter
@Setter
public class Args {
    @Parameter(names = "-s", description = "Source directory path images",
            validateValueWith = NotEmptyDirectoryPathValidator.class,
            required = true, order = 1)
    private Path sourceDirPath;

    @Parameter(names = "-o", description = "Output directory path", order = 2)
    private Path outputDirPath = Paths.get("").toAbsolutePath();

    private Path resolvedOutputDirPath;

    @Parameter(names = "-rs", arity = 1, description = "Enable/disable recursive walk in source directory", order = 3)
    private boolean recursiveSourceDirEnabled;

    @Parameter(names = "-x", description = "Count of pictures on axis X",
            required = true, validateWith = PositiveInteger.class, order = 4)
    private int axisX = 6;

    @Parameter(names = "-y", description = "Count of pictures on axis Y",
            required = true, validateWith = PositiveInteger.class, order = 5)
    private int axisY = 3;

    @Parameter(names = "-t", description = "Timeout of operations in minutes",
            validateWith = PositiveInteger.class, order = 6)
    private int timeoutMins = 10;

    @Parameter(names = "-e",
            description = "List of filtered extensions in source directory. Example: jpg,jpeg,png,bmp,gif",
            validateValueWith = ExtensionValidator.class, order = 7)
    private List<String> filteredExtensions = new ArrayList<>(supportedExtensions);

    @Override
    public String toString() {
        return "Source dir path: '" + sourceDirPath + "'\n" +
               "Output dir path: '" + outputDirPath + "'\n" +
               "Resolved output dir path: '" + resolvedOutputDirPath + "'\n" +
               "Recursive source dir enabled: " + recursiveSourceDirEnabled + '\n' +
               "Axis X: " + axisX + '\n' +
               "Axis Y: " + axisY + '\n' +
               "Timeout mins: " + timeoutMins + '\n' +
               "Filtered extensions: " + filteredExtensions;
    }
}