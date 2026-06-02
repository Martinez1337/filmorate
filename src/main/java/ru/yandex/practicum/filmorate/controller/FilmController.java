package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.validation.groups.OnUpdate;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmDto filmDto) {
        log.info("Received a request to create a movie: {}", filmDto);
        return filmService.create(filmDto);
    }

    @PutMapping
    public FilmDto updateFilm(@Validated({Default.class, OnUpdate.class}) @RequestBody FilmDto filmDto) {
        log.info("Received a request to update the movie with id: {}", filmDto.getId());
        return filmService.update(filmDto);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@Positive @PathVariable Long id) {
        log.info("Received a request to get the film with id: {}", id);
        return filmService.findById(id);
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.info("A request for a list of all films has been received");
        return filmService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@Positive @PathVariable Long id) {
        log.info("Received a request to delete the film with id: {}", id);
        filmService.deleteById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilmById(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long userId
    ) {
        log.info("Received a request to like the film with id: {}", id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilmById(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long userId
    ) {
        log.info("Received a request to unlike the film with id: {}", id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = "10") @Positive int count
    ) {
        log.info("Received a request to get the popular films has been received");
        return filmService.getPopular(count);
    }
}
