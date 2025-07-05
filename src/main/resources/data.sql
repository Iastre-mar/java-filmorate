-- Справочники
MERGE INTO ref_rating (id, code)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

Merge INTO ref_genre (id, name)
VALUES (1, 'comedy'),
       (2, 'drama'),
       (3, 'cartoon'),
       (4, 'thriller'),
       (5, 'documentary'),
       (6, 'action')
       ;

MERGE INTO ref_friendship_status (id, status)
VALUES (1, 'PENDING'),
       (2, 'ACCEPTED');