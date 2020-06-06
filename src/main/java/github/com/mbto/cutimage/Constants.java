package github.com.mbto.cutimage;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public interface Constants {
    Set<String> supportedExtensions = new LinkedHashSet<>(
            asList("jpg", "jpeg", "png", "bmp", "gif"));

    DateTimeFormatter DDMMYYYY_HHMMSS_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy HH;mm;ss");
}
