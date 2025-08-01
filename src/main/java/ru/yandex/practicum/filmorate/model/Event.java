package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;

@Data
public class Event {
    private Long eventId;
    private Long userId;
    private Long entityId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant timestamp;

    private EventType eventType;
    private Operation operation;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }
}
