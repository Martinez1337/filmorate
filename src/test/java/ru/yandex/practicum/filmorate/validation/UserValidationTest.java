package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest extends ValidationBaseTest {

    @Test
    void user_emailBlank_constraintViolation() {
        User user = new User();
        user.setEmail("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Ожидается ошибка для email");
    }

    @Test
    void user_emailInvalid_constraintViolation() {
        User user = new User();
        user.setEmail("address-without-at-sign");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Ожидается ошибка для email");
    }

    @Test
    void user_loginBlank_constraintViolation() {
        User user = new User();
        user.setLogin("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")), "Ожидается ошибка для login");
    }

    @Test
    void user_loginWithSpaces_constraintViolation() {
        User user = new User();
        user.setLogin("with space");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")), "Ожидается ошибка для login");
    }

    @Test
    void user_birthdayFuture_constraintViolation() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")), "Ожидается ошибка для birthday");
    }

    @Test
    void user_birthToday_isValid() {
        User user = new User();
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("birthday")), "birthday на границе (today) должен проходить валидацию");
    }
}
