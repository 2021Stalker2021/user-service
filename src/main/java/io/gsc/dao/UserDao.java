package io.gsc.dao;

import io.gsc.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для управления объектами {@link UserEntity} в базе данных.
 * Предоставляет базовые CRUD операции (Create, Read, Update, Delete).
 */
public interface UserDao {

    /**
     * Сохраняет нового пользователя в базе данных.
     *
     * @param userEntity объект пользователя для сохранения
     */
    void save(UserEntity userEntity);

    /**
     * Выполняет поиск пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return {@link Optional}, содержащий найденного пользователя,
     * или пустой {@link Optional}, если пользователь не найден
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Возвращает список всех пользователей, зарегистрированных в системе.
     *
     * @return список объектов {@link UserEntity}
     */
    List<UserEntity> findAll();

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userEntity объект пользователя с обновленными данными
     */
    void update(UserEntity userEntity);

    /**
     * Удаляет пользователя из базы данных по его идентификатору.
     *
     * @param id идентификатор пользователя, которого необходимо удалить
     */
    void delete(Long id);
}
