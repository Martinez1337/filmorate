package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageIntegrationTest {
    private final MpaDbStorage mpaStorage;

    @Test
    void getById_existingId_returnsMpa() {
        assertThat(mpaStorage.getById(3))
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa.getId()).isEqualTo(3L);
                    assertThat(mpa.getName()).isEqualTo("PG-13");
                });
    }

    @Test
    void getById_unknownId_returnsEmptyOptional() {
        assertThat(mpaStorage.getById(999)).isEmpty();
    }

    @Test
    void getAll_returnsAllMpaRatingsOrderedById() {
        assertThat(mpaStorage.getAll())
                .extracting("id")
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }
}
