package ru.yandex.practicum.filmorate.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    @LogMethodResult
    public Duration deserialize(JsonParser p,
                                DeserializationContext ctxt
    ) throws IOException {
        String text = p.getText();

        try {
            return Duration.ofMinutes(Long.valueOf(text));
        } catch (NumberFormatException e) {
            return Duration.parse(text);
        } catch (Exception e) {
            throw new IOException("Failed to parse Duration from: " + text, e);
        }
    }
}