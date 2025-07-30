package ru.yandex.practicum.filmorate.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getValueAsString() == null) {
            return null;
        }
        try {
            // Ожидаем число минут (например, "94")
            long minutes = Long.parseLong(p.getValueAsString());
            return Duration.ofMinutes(minutes);
        } catch (NumberFormatException e) {
            // Если пришло значение в формате PT..., используем стандартный парсинг
            return Duration.parse(p.getValueAsString());
        }
    }
}