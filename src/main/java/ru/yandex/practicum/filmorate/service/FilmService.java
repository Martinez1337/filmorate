package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
        return filmMapper.mapToRsDto(createdFilm);
    }

    public FilmResponseDto update(FilmRequestDto filmRequestDto) {
        Film film = filmMapper.map(filmRequestDto);
        getFilmOrThrow(film.getId());
        validateFilmReferences(film);
        Film updatedFilm = filmStorage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Film not found"));
        log.info("Updated film: {}", updatedFilm);
        return filmMapper.mapToRsDto(updatedFilm);
    }

    public Collection<FilmResponseDto> findAll() {
        return mapFilmsToResponseDto(filmStorage.getAllFilms());
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
        return mapFilmsToResponseDto(filmStorage.getPopularFilms(count));
    }

    private Collection<FilmResponseDto> mapFilmsToResponseDto(Collection<Film> films) {
        Map<Long, List<Genre>> genresByFilmId = genreStorage.getGenresForFilms(
                films.stream()
                        .map(Film::getId)
                        .toList()
        );

        return films.stream()
                .map(film -> {
                    film.setGenres(new LinkedHashSet<>(genresByFilmId.getOrDefault(film.getId(), List.of())));
                    return filmMapper.mapToRsDto(film);
                })
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
                .map(genre -> genre.getName() == null ? getGenreOrThrow(genre.getId()) : genre)
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
        film.setGenres(new LinkedHashSet<>(genres));
        return filmMapper.mapToRsDto(film);
    }

    private void validateFilmReferences(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpa(getMpaOrThrow(film.getMpa().getId()));
        } else {
            throw new ValidationException("Film references must contain an MPA");
        }
        if (film.getGenres() != null) {
            List<Long> genresIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .toList();
            List<Genre> storedGenres = genreStorage.getGenresByIds(genresIds);

            if (genresIds.size() != storedGenres.size()) {
                throw new NotFoundException("Some genre ids were not found");
            }

            film.setGenres(new LinkedHashSet<>(storedGenres));
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
