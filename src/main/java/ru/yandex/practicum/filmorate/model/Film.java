package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotation.MinDate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;

@Data
public class Film {
    @NotNull(message = "{id.notnull}", groups = {OnUpdate.class})
    private Integer id;

    @NotBlank(message = "{film.name.notblank}")
    private String name;

    @Size(max = 200, message = "{film.description.size}")
    private String description;

    @MinDate(value = "28.12.1895", message = "{film.releaseDate.mindate}")
    private LocalDate releaseDate;

    @Positive(message = "{film.duration.positive}")
    private Integer duration;
}
