package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    Optional<Genre> getGenreById(long id);

    Collection<Genre> getAllGenres();

    Map<Integer, Set<Genre>> getGenresForFilms(Collection<Long> id);

    void setGenresForFilm(long filmId, Collection<Genre> genres);

    void deleteGenresFromFilm(long filmId);
}
