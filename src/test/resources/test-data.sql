DELETE
FROM film_likes;
DELETE
FROM film_genres;
DELETE
FROM films;
DELETE
FROM users;
DELETE
FROM ref_rating;
DELETE
FROM ref_genre;

INSERT INTO ref_rating (id, code)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO ref_genre (id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Фантастика'),
       (5, 'Боевик');


INSERT INTO films (id, name, description, release_date, duration, rating_id)
VALUES (1, 'Film 1', 'Description 1', '2020-01-01', 120, 3),
       (2, 'Film 2', 'Description 2', '2021-01-01', 90, 2);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1),
       (1, 2),
       (2, 2);

INSERT INTO users (id, login, email, name, birthday)
VALUES (101, 'user101', 'user101@example.com', 'User 101', '1990-01-01'),
       (102, 'user102', 'user102@example.com', 'User 102', '1995-05-15'),
       (103, 'user103', 'user103@example.com', 'User 103', '2000-10-20'),
       (104, 'user104', 'user104@example.com', 'User 104', '1985-03-25'),
       (105, 'user105', 'user105@example.com', 'User 105', '1999-12-31');

INSERT INTO film_likes (film_id, user_id)
VALUES (1, 101),
       (1, 102),
       (2, 101);

ALTER TABLE films
    ALTER COLUMN id RESTART WITH 3;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 106;