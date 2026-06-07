package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.IdDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Mapper(componentModel = "spring")
public interface MpaMapper {
    MpaDto mapToDto(Mpa mpa);

    Mpa map(MpaDto mpaDto);

    @Mapping(target = "name", ignore = true)
    Mpa map(IdDto idDto);
}
