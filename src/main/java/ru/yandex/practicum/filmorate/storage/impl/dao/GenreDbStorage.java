package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public Optional<Genre> get(Long id) {
        String sql = "SELECT id, name FROM ref_genre WHERE id = ?";
        Genre genre = null;
        try {
            genre = jdbcTemplate.queryForObject(sql, genreRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(
                    "Genre with id %d doesn't exist".formatted(id));
        }
        return Optional.ofNullable(genre);
    }

    @Override
    public Collection<Genre> getAll() {
        String sql = "SELECT id, name FROM ref_genre ORDER BY id";
        return jdbcTemplate.query(sql, genreRowMapper);
    }
}