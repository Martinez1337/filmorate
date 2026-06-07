package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mapping.GenreRowMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String GET_GENRE_BY_ID_SQL = "SELECT id, name FROM genres WHERE id = ?";
    private static final String GET_ALL_GENRES_SQL = "SELECT id, name FROM genres ORDER BY id";
    private static final String GET_GENRES_FOR_FILMS_SQL = """
            SELECT fg.film_id, g.id, g.name
            FROM film_genres AS fg
            JOIN genres AS g ON fg.genre_id = g.id
            WHERE fg.film_id IN (:ids)
            ORDER BY fg.film_id, g.id
            """;
    private static final String SET_GENRES_FOR_FILM_SQL = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_GENRES_FROM_FILM_SQL = "DELETE FROM film_genres WHERE film_id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new GenreRowMapper());
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        return findOne(GET_GENRE_BY_ID_SQL, id);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return findMany(GET_ALL_GENRES_SQL);
    }

    @Override
    public Map<Integer, Set<Genre>> getGenresForFilms(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String query = GET_GENRES_FOR_FILMS_SQL.replace(":ids", placeholders);
        List<Object> params = ids.stream()
                .map(id -> (Object) id)
                .toList();

        return jdbc.query(query, rs -> {
            Map<Integer, Set<Genre>> genresByFilmId = new HashMap<>();
            while (rs.next()) {
                genresByFilmId
                        .computeIfAbsent(rs.getInt("film_id"), filmId -> new LinkedHashSet<>())
                        .add(mapper.mapRow(rs, rs.getRow()));
            }
            return genresByFilmId;
        }, params.toArray());
    }

    @Override
    public void setGenresForFilm(long filmId, Collection<Genre> genres) {
        deleteGenresFromFilm(filmId);
        if (genres == null || genres.isEmpty()) {
            return;
        }

        for (Genre genre : genres) {
            update(SET_GENRES_FOR_FILM_SQL, filmId, genre.getId());
        }
    }

    @Override
    public void deleteGenresFromFilm(long filmId) {
        jdbc.update(DELETE_GENRES_FROM_FILM_SQL, filmId);
    }
}
