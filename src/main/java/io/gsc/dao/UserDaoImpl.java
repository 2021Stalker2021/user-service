package io.gsc.dao;

import io.gsc.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    @Override
    public void save(UserEntity userEntity) {
        executeInTransaction(session -> session.persist(userEntity));
        log.info("Пользователь успешно сохранен: {}", userEntity.getEmail());
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return execute(session -> Optional.ofNullable(session.get(UserEntity.class, id)));
    }

    @Override
    public List<UserEntity> findAll() {
        return execute(session -> session.createQuery("from UserEntity", UserEntity.class).list());
    }

    @Override
    public void update(UserEntity userEntity) {
        executeInTransaction(session -> session.merge(userEntity));
        log.info("Пользователь с ID {} обновлен", userEntity.getId());
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(session -> {
            UserEntity userEntity = session.get(UserEntity.class, id);
            if (userEntity != null) {
                session.remove(userEntity);
                log.info("Пользователь с ID {} удален", id);
            } else {
                log.warn("Удаление невозможно: пользователь с ID {} не найден", id);
            }
        });
    }

    private <T> T execute(Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            return action.apply(session);
        } catch (Exception e) {
            log.error("Ошибка при выполнении запроса к БД", e);
            throw e;
        }
    }

    private void executeInTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Ошибка транзакции. Откат изменений.", e);
            throw e;
        }
    }
}
