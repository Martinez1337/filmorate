package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> getGenreById(long id);

    List<Genre> getGenresByIds(Collection<Long> ids);

    Collection<Genre> getAllGenres();

    Map<Long, List<Genre>> getGenresForFilms(Collection<Long> id);

    void setGenresForFilm(long filmId, Collection<Genre> genres);

    void deleteGenresFromFilm(long filmId);
}
