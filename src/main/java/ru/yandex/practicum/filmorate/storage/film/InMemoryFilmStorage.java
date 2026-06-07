package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new HashMap<>();
    private long currentId = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(filmMap.get(id));
    }

    @Override
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return filmMap.values().stream()
                .sorted(
                        Comparator.comparingInt((Film film) -> film.getLikes().size())
                                .reversed()
                                .thenComparing(Film::getId)
                )
                .limit(count)
                .toList();
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (filmMap.containsKey(film.getId())) {
            filmMap.put(film.getId(), film);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public void deleteFilmById(long id) {
        filmMap.remove(id);
    }

    private long getNextId() {
        return ++currentId;
    }
}
