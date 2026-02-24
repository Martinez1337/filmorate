package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.annotation.MinDate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @NotNull(message = "{id.notnull}", groups = {OnUpdate.class})
    private Integer id;

    @NotBlank(message = "{film.name.notblank}")
    private String name;

    @Size(max = 200, message = "{film.description.size}")
    private String description;

    @NotNull(message = "{film.releaseDate.notnull}")
    @MinDate(value = "1895-12-28", message = "{film.releaseDate.mindate}")
    private LocalDate releaseDate;

    @NotNull(message = "{film.duration.notnull}")
    @Positive(message = "{film.duration.positive}")
    private Integer duration;
}
