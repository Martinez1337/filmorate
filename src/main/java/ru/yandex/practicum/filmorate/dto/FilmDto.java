package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.dto.validation.annotation.MinDate;
import ru.yandex.practicum.filmorate.dto.validation.groups.OnUpdate;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class FilmDto {
    @NotNull(groups = OnUpdate.class)
    @Positive(groups = OnUpdate.class)
    private Long id;

    @NotBlank(message = "{film.name.notblank}")
    private String name;

    @Size(max = 200, message = "{film.description.size}")
    private String description;

    @NotNull(message = "{film.releaseDate.notnull}")
    @MinDate(value = "1895-12-28", message = "{film.releaseDate.mindate}")
    @PastOrPresent(message = "{film.releaseDate.pastorpresent}")
    private LocalDate releaseDate;

    @NotNull(message = "{film.duration.notnull}")
    @Positive(message = "{film.duration.positive}")
    private Integer duration;
}
