DELETE
FROM friendships;
DELETE
FROM users;

INSERT INTO users (id, login, email, name, birthday)
VALUES (1, 'user1', 'user1@example.com', 'User One', '1990-01-01'),
       (2, 'user2', 'user2@example.com', 'User Two', '1995-05-15'),
       (3, 'user3', 'user3@example.com', 'User Three', '2000-10-20');

INSERT INTO friendships (user_id, friend_id)
VALUES (1, 2),
       (1, 3),
       (2, 3);

ALTER TABLE users
    ALTER COLUMN id RESTART WITH 4;