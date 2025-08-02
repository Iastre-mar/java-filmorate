package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();

        event.setEventId(rs.getLong("event_id"));
        event.setUserId(rs.getLong("user_id"));
        event.setEntityId(rs.getLong("entity_id"));

        event.setTimestamp(rs.getLong("timestamp"));

        String eventTypeStr = rs.getString("event_type");
        if (eventTypeStr != null) {
            event.setEventType(Event.EventType.valueOf(eventTypeStr));
        }

        String operationStr = rs.getString("operation");
        if (operationStr != null) {
            event.setOperation(Event.Operation.valueOf(operationStr));
        }

        return event;
    }
}