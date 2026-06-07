package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Accessors(chain = true)
public class FilmResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaDto mpa;
    private Collection<GenreDto> genres = new ArrayList<>();
}
