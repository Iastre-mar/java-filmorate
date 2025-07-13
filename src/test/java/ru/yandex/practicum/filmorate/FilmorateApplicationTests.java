package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FilmorateApplicationTests {
    @Autowired private TestRestTemplate restTemplate;


    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Disabled("Deprecated")
    @Test
    void whenValidInputThenAddFilm() {

        Film film = new Film();
        film.setName("Sucess");
        film.setDuration(Duration.ofMinutes(5));
        film.setReleaseDate(LocalDate.now());
        film.setId(1);
        ResponseEntity<Film> response = restTemplate.postForEntity("/films",
                                                                   film,
                                                                   Film.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()
                           .getName()).isEqualTo("Sucess");
    }

    @Test
    void whenTooEarlyCreateDateOfFilmThenError() {

        Film film = new Film();
        film.setName("Too old");
        film.setDuration(Duration.ofMinutes(5));
        film.setReleaseDate(LocalDate.MIN);
        film.setId(1);
        assertThrows(RestClientException.class,
                     () -> restTemplate.postForEntity("/films", film,
                                                      Film.class));
    }


    @Test
    void whenDurationZeroOfFilmThenError() {

        Film film = new Film();
        film.setName("Too old");
        film.setDuration(Duration.ZERO);
        film.setReleaseDate(LocalDate.now());
        film.setId(1);
        assertThrows(RestClientException.class,
                     () -> restTemplate.postForEntity("/films", film,
                                                      Film.class));
    }

    @Test
    void whenValidInputThenCreateUser() {

        User user = new User();
        user.setName("Sucess");
        user.setBirthday(LocalDate.now());
        user.setEmail("aaa@a.example.com");
        user.setId(1);
        user.setLogin("a");
        ResponseEntity<User> response = restTemplate.postForEntity("/users",
                                                                   user,
                                                                   User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()
                           .getName()).isEqualTo("Sucess");
    }


}
