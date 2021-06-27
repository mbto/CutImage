package com.github.mbto.cutimage;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public interface Constants {
    String SOFTWARE_INFO = "CutImage v2.0\nhttps://github.com/mbto/CutImage";

    String SOFTWARE_NAME = "CutImage";

    Set<String> supportedExtensions = new LinkedHashSet<>(
            asList("jpg", "jpeg", "png", "bmp", "gif"));

    DateTimeFormatter DDMMYYYY_HHMMSS_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy HH;mm;ss");

    static String calcHumanDiff(long start) {
        long diff = (System.currentTimeMillis() - start) / 1000;
        return (diff / 60) + "m " + (diff % 60) + "s";
    }
}
