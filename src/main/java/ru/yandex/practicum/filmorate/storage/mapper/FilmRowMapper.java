package ru.yandex.practicum.filmorate.storage.mapper;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date")
                              .toLocalDate());
        film.setDuration(Duration.ofMinutes(rs.getInt("duration")));
        Rating rating = new Rating();
        rating.setId(rs.getLong("rating_id"));
        film.setRating(rating);
        return film;
    }

}
