package com.github.mbto.cutimage;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.util.List;

import static com.github.mbto.cutimage.Constants.supportedExtensions;

public class ExtensionValidator implements IValueValidator<List<String>> {
    @Override
    public void validate(String name, List<String> values) throws ParameterException {
        for (String extension : values) {
            if(!supportedExtensions.contains(extension.toLowerCase())) {
                throw new ParameterException("Extension '" + extension + "' not supported, " +
                        "only " + supportedExtensions);
            }
        }
    }
}
