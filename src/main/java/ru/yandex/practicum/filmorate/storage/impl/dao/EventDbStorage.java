package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getEventsByUserId(Long userId) {
        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY event_id";
        return jdbcTemplate.query(sql, new EventRowMapper(), userId);
    }

    @Override
    public Event addEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(
                jdbcTemplate).withTableName("events")
                             .usingGeneratedKeyColumns("event_id");

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", event.getUserId());
        values.put("entity_id", event.getEntityId());
        values.put("timestamp", event.getTimestamp());
        values.put("event_type", event.getEventType()
                                      .name());
        values.put("operation", event.getOperation()
                                     .name());

        Long eventId = simpleJdbcInsert.executeAndReturnKey(values)
                                       .longValue();
        event.setEventId(eventId);

        return event;
    }

    @Override
    public List<Event> getAllEvents() {
        String sql = "SELECT * FROM events ORDER BY event_id";
        return jdbcTemplate.query(sql, new EventRowMapper());
    }

    private static class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = new Event();
            event.setEventId(rs.getLong("event_id"));
            event.setUserId(rs.getLong("user_id"));
            event.setEntityId(rs.getLong("entity_id"));
            event.setTimestamp(System.currentTimeMillis());
            event.setEventType(
                    Event.EventType.valueOf(rs.getString("event_type")));
            event.setOperation(
                    Event.Operation.valueOf(rs.getString("operation")));
            return event;
        }
    }
}