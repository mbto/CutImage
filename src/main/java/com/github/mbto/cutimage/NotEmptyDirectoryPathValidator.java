package com.github.mbto.cutimage;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;
import lombok.SneakyThrows;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class NotEmptyDirectoryPathValidator implements IValueValidator<Path> {
    @SneakyThrows
    @Override
    public void validate(String name, Path value) throws ParameterException {
        if (!Files.exists(value)) {
            throw new ParameterException("Path '" + value + "' not exists");
        }

        try(DirectoryStream<Path> sourceEntries = Files.newDirectoryStream(value)) {
            if(!sourceEntries.iterator().hasNext()) {
                throw new ParameterException("Empty source directory '" + value + "'");
            }
        }
    }
}