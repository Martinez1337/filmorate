package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest extends ValidationBaseTest {

    @Test
    void user_emailBlank_constraintViolation() {
        UserDto user = new UserDto();
        user.setEmail("   ");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Ожидается ошибка для email");
    }

    @Test
    void user_emailInvalid_constraintViolation() {
        UserDto user = new UserDto();
        user.setEmail("address-without-at-sign");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Ожидается ошибка для email");
    }

    @Test
    void user_loginBlank_constraintViolation() {
        UserDto user = new UserDto();
        user.setLogin("   ");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")), "Ожидается ошибка для login");
    }

    @Test
    void user_loginWithSpaces_constraintViolation() {
        UserDto user = new UserDto();
        user.setLogin("with space");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")), "Ожидается ошибка для login");
    }

    @Test
    void user_birthdayFuture_constraintViolation() {
        UserDto user = new UserDto();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")), "Ожидается ошибка для birthday");
    }

    @Test
    void user_birthToday_isValid() {
        UserDto user = new UserDto();
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("birthday")), "birthday на границе (today) должен проходить валидацию");
    }
}
