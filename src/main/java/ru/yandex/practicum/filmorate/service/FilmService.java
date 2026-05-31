package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;

    public FilmService(
            @Qualifier("inMemoryFilmStorage") FilmStorage filmStorage,
            @Qualifier("inMemoryUserStorage") UserStorage userStorage,
            FilmMapper filmMapper
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmMapper = filmMapper;
    }

    public FilmDto create(FilmDto filmDto) {
        Film createdFilm = filmStorage.createFilm(filmMapper.map(filmDto));
        log.info("Created film: {}", createdFilm);
        return filmMapper.mapToDto(createdFilm);
    }

    public FilmDto update(FilmDto filmDto) {
        Film film = filmMapper.map(filmDto);
        getFilmOrThrow(film.getId());
        Film updatedFilm = filmStorage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Film not found"));
        log.info("Updated film: {}", updatedFilm);
        return filmMapper.mapToDto(updatedFilm);
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.getAllFilms()
                .stream()
                .map(filmMapper::mapToDto)
                .toList();
    }

    public FilmDto findById(Long id) {
        Film film = getFilmOrThrow(id);
        log.info("Found film: {}", film);
        return filmMapper.mapToDto(film);
    }

    public void deleteById(Long id) {
        getFilmOrThrow(id);
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

    public Collection<FilmDto> getPopular(int count) {
        if (count < 0) {
            count = 10;
        }
        return filmStorage.getPopularFilms(count).stream()
                .map(filmMapper::mapToDto)
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
