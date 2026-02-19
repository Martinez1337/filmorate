package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(message = "{id.notnull}", groups = {OnUpdate.class})
    private Integer id;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotBlank(message = "{user.login.notblank}")
    @Pattern(regexp = "^\\S+$", message = "{user.login.pattern.nospaces}")
    private String login;

    private String name;

    @PastOrPresent(message = "{user.birthday.pastorpresent}")
    private LocalDate birthday;
}
