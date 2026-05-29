package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    FilmDto mapToDto(Film film);

    @Mapping(target = "likes", ignore = true)
    Film map(FilmDto filmRqDto);
}
