package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.mapping.MpaMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    public Collection<MpaDto> findAll() {
        return mpaStorage.getAll()
                .stream()
                .map(mpaMapper::mapToDto)
                .toList();
    }

    public MpaDto findById(long id) {
        Mpa mpa = getMpaOrThrow(id);
        log.info("Found mpa: {}", mpa);
        return mpaMapper.mapToDto(mpa);
    }

    private Mpa getMpaOrThrow(long id) {
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Mpa id " + id + " not found"));
    }
}
