package ru.litu.main_service.role.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.main_service.TestLoggerExtension;
import ru.litu.main_service.user.model.Role;
import ru.litu.main_service.user.repository.RoleRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(TestLoggerExtension.class)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("getByName: должен вернуть роль по имени")
    void testGetByName_Found() {
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);

        Optional<Role> result = roleRepository.getByName("ROLE_USER");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("getByName: должен вернуть пусто, если роли нет")
    void testGetByName_NotFound() {
        Optional<Role> result = roleRepository.getByName("NON_EXISTENT");

        assertThat(result).isEmpty();
    }
}
