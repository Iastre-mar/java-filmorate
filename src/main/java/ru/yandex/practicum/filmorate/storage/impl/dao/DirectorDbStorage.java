package ru.yandex.practicum.filmorate.storage.impl.dao;

import jakarta.websocket.OnError;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;

    @Override
    public Optional<Director> get(Long id) {
        String sql = "SELECT id, name FROM ref_director WHERE id = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sql, directorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(
                    "Director with id %d doesn't exist".formatted(id));
        }
        return Optional.ofNullable(director);
    }

    @Override
    public Optional<Director> getByName(String name) {
        String sql = "SELECT id, name FROM ref_director WHERE name = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sql, directorRowMapper,
                    name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(director);
    }

    @Override
    public Collection<Director> getAll() {
        String sql = "SELECT id, name FROM ref_director ORDER BY id";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Director add(Director director) {
        String sql = "INSERT INTO ref_director (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey())
                .longValue();
        director.setId(generatedId);
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE ref_director SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @OnError
    public Long delete(Long id) {
        String sql = "DELETE FROM ref_director WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }
}
