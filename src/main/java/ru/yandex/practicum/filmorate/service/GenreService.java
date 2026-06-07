package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.mapping.GenreMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    public Collection<GenreDto> findAll() {
        return genreStorage.getAllGenres()
                .stream()
                .map(genreMapper::mapToDto)
                .toList();
    }

    public GenreDto findById(long id) {
        Genre genre = getGenreOrThrow(id);
        log.info("Found genre: {}", genre);
        return genreMapper.mapToDto(genre);
    }

    private Genre getGenreOrThrow(long id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Genre id " + id + " not found"));
    }
}
