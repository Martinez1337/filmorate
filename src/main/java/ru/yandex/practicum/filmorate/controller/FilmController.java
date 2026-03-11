package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Received a request to create a movie: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Validated(OnUpdate.class) @RequestBody Film film) {
        log.info("Received a request to update the movie with id: {}", film.getId());
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("A request for a list of all films has been received");
        return filmService.findAll();
    }
}
