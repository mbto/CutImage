package github.com.mbto.cutimage;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.util.List;

import static github.com.mbto.cutimage.Constants.supportedExtensions;

public class ExtensionValidator implements IValueValidator<List<String>> {
    @Override
    public void validate(String name, List<String> value) throws ParameterException {
        if (value.stream()
                .anyMatch(extension -> !supportedExtensions.contains(extension.toLowerCase()))) {
            throw new ParameterException("Supported only " + supportedExtensions + " extensions");
        }
    }
}
