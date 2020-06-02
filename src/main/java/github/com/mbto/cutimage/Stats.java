package github.com.mbto.cutimage;

import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@ToString
public class Stats {
    private final AtomicInteger files = getDefaultCounter();
    private final AtomicInteger images = getDefaultCounter();
    private final AtomicInteger directories = getDefaultCounter();

    private final AtomicInteger newImages = getDefaultCounter();
    private final AtomicInteger errors = getDefaultCounter();

    private static AtomicInteger getDefaultCounter() {
        return new AtomicInteger(0);
    }
}