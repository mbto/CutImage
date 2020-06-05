package github.com.mbto.cutimage;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Stats {
    private final Map<String, Integer> images = new HashMap<>();
    private final AtomicInteger directories = buildDefaultCounter();

    private final AtomicInteger newImages = buildDefaultCounter();
    private final AtomicInteger errors = buildDefaultCounter();

    private static AtomicInteger buildDefaultCounter() {
        return new AtomicInteger(0);
    }

    @Override
    public String toString() {
        synchronized (images) {
            return "Founded " + calcImagesCount() + " images from "
                    + directories + " directories: " + images + "\n" +
                    "Created " + newImages + " images\n" +
                    errors + " errors throwed";
        }
    }

    public int calcImagesCount() {
        synchronized (images) {
            return images.values().stream()
                    .mapToInt(Integer::intValue).sum();
        }
    }
}