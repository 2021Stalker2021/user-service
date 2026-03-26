package io.gsc.dao;

import io.gsc.entity.User;
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
    public void save(User user) {
        executeInTransaction(session -> session.persist(user));
        log.info("Пользователь успешно сохранен: {}", user.getEmail());
    }

    @Override
    public Optional<User> findById(Long id) {
        return execute(session -> Optional.ofNullable(session.get(User.class, id)));
    }

    @Override
    public List<User> findAll() {
        return execute(session -> session.createQuery("from User", User.class).list());
    }

    @Override
    public void update(User user) {
        executeInTransaction(session -> session.merge(user));
        log.info("Пользователь с ID {} обновлен", user.getId());
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
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
