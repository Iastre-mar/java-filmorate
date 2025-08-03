package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @LogMethodResult
    public Director getDirectorByIdOrThrow(Long id) {
        return directorStorage.get(id)
                              .orElseThrow(() -> new DirectorNotFoundException(
                                      "Director not found with id: " + id));
    }

    @LogMethodResult
    public Collection<Director> getAllDirectors() {
        return directorStorage.getAll();
    }

    @LogMethodResult
    protected List<Director> getDirectorOrThrow(List<Director> directors) {
        List<Director> resDirectors = getEmptyArrayIfNull(directors);
        resDirectors = resDirectors.stream()
                                   .map(director -> getDirectorByIdOrThrow(
                                           director.getId()))
                                   .toList();
        return resDirectors;
    }

    private List<Director> getEmptyArrayIfNull(List<Director> directors) {
        return directors == null ? new ArrayList<>() : directors;
    }

    @LogMethodResult
    public Director add(Director director) {
        Optional<Director> existingDirector = directorStorage.getByName(
                director.getName());
        return existingDirector.orElseGet(() -> directorStorage.add(director));
    }

    @LogMethodResult
    public Director update(Director director) {
        getDirectorByIdOrThrow(director.getId());
        return directorStorage.update(director);
    }

    @LogMethodResult
    public Long delete(Long id) {
        getDirectorByIdOrThrow(id);
        return directorStorage.delete(id);
    }

}
