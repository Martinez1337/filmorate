package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MpaDto {
    Long id;
    String name;
}
