package integration;

import io.gsc.dao.UserDao;
import io.gsc.dao.UserDaoImpl;
import io.gsc.entity.UserEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void setup() {
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        sessionFactory = configuration.buildSessionFactory();
    }

    @BeforeEach
    void init() {
        userDao = new UserDaoImpl(sessionFactory);
    }

    @Test
    void save_ShouldGenerateId_WhenUserIsSaved() {
        UserEntity user = UserEntity.builder().name("Tester").email("save@test.com").build();

        userDao.save(user);

        assertNotNull(user.getId());
    }

    @Test
    void findById_ShouldReturnUser_WhenIdExists() {
        UserEntity user = UserEntity.builder().name("FindMe").email("find@test.com").build();
        userDao.save(user);
        Long id = user.getId();

        Optional<UserEntity> found = userDao.findById(id);

        assertTrue(found.isPresent());
        assertEquals("FindMe", found.get().getName());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        Optional<UserEntity> found = userDao.findById(-1L);

        assertTrue(found.isEmpty());
    }
}
