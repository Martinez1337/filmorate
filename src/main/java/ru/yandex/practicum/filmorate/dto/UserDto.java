package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.dto.validation.groups.OnUpdate;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class UserDto {
    @NotNull(groups = OnUpdate.class)
    @Positive(groups = OnUpdate.class)
    private Long id;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotBlank(message = "{user.login.notblank}")
    @Pattern(regexp = "^\\S+$", message = "{user.login.pattern.nospaces}")
    private String login;

    private String name;

    @NotNull(message = "{user.birthday.notnull}")
    @PastOrPresent(message = "{user.birthday.pastorpresent}")
    private LocalDate birthday;
}
