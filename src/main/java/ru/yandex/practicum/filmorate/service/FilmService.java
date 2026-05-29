package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(
            @Qualifier("inMemoryFilmStorage") FilmStorage filmStorage,
            @Qualifier("inMemoryUserStorage") UserStorage userStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Created film: {}", createdFilm);
        return createdFilm;
    }

    public Film update(Film film) {
        Film updatedFilm = filmStorage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Film not found"));
        log.info("Updated film: {}", updatedFilm);
        return updatedFilm;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    public Film findById(Long id) {
        Film film = getFilmOrThrow(id);
        log.info("Found film: {}", film);
        return film;
    }

    public void deleteById(Long id) {
        filmStorage.deleteFilmById(id);
        log.info("Deleted film: {}", id);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        film.getLikes().remove(userId);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(
                        Comparator.comparingInt((Film film) -> film.getLikes().size())
                                .reversed()
                                .thenComparing(Film::getId)
                )
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film id " + filmId + " not found"));
    }

    private User getUserOrThrow(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User id " + userId + " not found"));
    }
}
