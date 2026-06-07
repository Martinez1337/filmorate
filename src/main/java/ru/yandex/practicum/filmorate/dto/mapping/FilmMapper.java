package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(
        componentModel = "spring",
        uses = {MpaMapper.class, GenreMapper.class}
)
public interface FilmMapper {
    FilmResponseDto mapToRsDto(Film film);

    @Mapping(target = "likes", ignore = true)
    Film map(FilmRequestDto filmRqDto);
}
