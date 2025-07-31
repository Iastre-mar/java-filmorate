package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Optional<Director> get(Long id);

    Optional<Director> getByName(String name);

    Collection<Director> getAll();

    Director add(Director director);

    Director update(Director director);

    Long delete(Long id);
}
