package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    Optional<Film> getFilmById(long id);

    Collection<Film> getAllFilms();

    Collection<Film> getPopularFilms(int count);

    Optional<Film> updateFilm(Film film);

    void deleteFilmById(long id);

    default void addLike(long filmId, long userId) {
        getFilmById(filmId).ifPresent(film -> film.getLikes().add(userId));
    }

    default void removeLike(long filmId, long userId) {
        getFilmById(filmId).ifPresent(film -> film.getLikes().remove(userId));
    }
}
