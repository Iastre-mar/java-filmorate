package ru.yandex.practicum.filmorate.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    @LogMethodResult
    public void serialize(Duration duration,
                          JsonGenerator gen,
                          SerializerProvider provider
    ) throws IOException {
        if (duration == null) {
            gen.writeNull();
        } else {
            gen.writeString(duration.toString());
        }
    }
}
