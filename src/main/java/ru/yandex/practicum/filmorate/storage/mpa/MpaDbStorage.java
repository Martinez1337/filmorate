package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mapping.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Component
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String GET_MPA_BY_ID_SQL = "SELECT id, name FROM mpa WHERE id = ?";
    private static final String GET_ALL_MPA_SQL = "SELECT id, name FROM mpa ORDER BY id";

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new MpaRowMapper());
    }

    @Override
    public Optional<Mpa> getById(long id) {
        return findOne(GET_MPA_BY_ID_SQL, id);
    }

    @Override
    public Collection<Mpa> getAll() {
        return findMany(GET_ALL_MPA_SQL);
    }
}
