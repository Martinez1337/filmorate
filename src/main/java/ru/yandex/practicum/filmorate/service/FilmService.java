package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmMapper filmMapper;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage,
            FilmMapper filmMapper
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.filmMapper = filmMapper;
    }

    public FilmResponseDto create(FilmRequestDto filmRequestDto) {
        Film film = filmMapper.map(filmRequestDto);
        validateFilmReferences(film);
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Created film: {}", createdFilm);
        return mapToResponseDto(createdFilm);
    }

    public FilmResponseDto update(FilmRequestDto filmRequestDto) {
        Film film = filmMapper.map(filmRequestDto);
        getFilmOrThrow(film.getId());
        validateFilmReferences(film);
        Film updatedFilm = filmStorage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Film not found"));
        log.info("Updated film: {}", updatedFilm);
        return mapToResponseDto(updatedFilm);
    }

    public Collection<FilmResponseDto> findAll() {
        return filmStorage.getAllFilms()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public FilmResponseDto findById(long id) {
        Film film = getFilmOrThrow(id);
        log.info("Found film: {}", film);
        return mapToResponseDto(film);
    }

    public void deleteById(long id) {
        getFilmOrThrow(id);
        filmStorage.deleteFilmById(id);
        log.info("Deleted film: {}", id);
    }

    public void addLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<FilmResponseDto> getPopular(int count) {
        if (count < 0) {
            count = 10;
        }
        return filmStorage.getPopularFilms(count).stream()
                .map(this::mapToResponseDto)
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

    private FilmResponseDto mapToResponseDto(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpa(getMpaOrThrow(film.getMpa().getId()));
        }
        Collection<Genre> genres = film.getGenres().stream()
                .map(genre -> getGenreOrThrow(genre.getId()))
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
        film.setGenres(new java.util.LinkedHashSet<>(genres));
        return filmMapper.mapToRsDto(film);
    }

    private void validateFilmReferences(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            getMpaOrThrow(film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> getGenreOrThrow(genre.getId()));
        }
    }

    private Mpa getMpaOrThrow(long mpaId) {
        return mpaStorage.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa id " + mpaId + " not found"));
    }

    private Genre getGenreOrThrow(long genreId) {
        return genreStorage.getGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre id " + genreId + " not found"));
    }
}
