package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int currentId = 0;

    public Film create(Film film) {
        film.setId(++currentId);
        filmMap.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        if (filmMap.containsKey(film.getId())) {
            filmMap.put(film.getId(), film);
            return film;
        }
        throw new NotFoundException("Film with id " + film.getId() + " not found");
    }

    public Collection<Film> findAll() {
        return filmMap.values();
    }
}
