package io.gsc;

import io.gsc.dao.UserDao;
import io.gsc.dao.UserDaoImpl;
import io.gsc.entity.UserEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Scanner;

public class UserApplication {
    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));

        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()) {
            UserDao userDao = new UserDaoImpl(sessionFactory);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== USER SERVICE STARTED ===");

            while (true) {
                System.out.println("\nМеню: 1.Создать 2.Найти 3.Список 4.Изменить имя 5.Удалить 0.Выход");
                String command = scanner.nextLine();

                if ("0".equals(command)) break;

                try {
                    switch (command) {
                        case "1" -> {
                            System.out.print("Имя: "); String name = scanner.nextLine();
                            System.out.print("Email: "); String email = scanner.nextLine();
                            System.out.print("Возраст: "); int age = Integer.parseInt(scanner.nextLine());
                            userDao.save(UserEntity.builder().name(name).email(email).age(age).build());
                        }
                        case "2" -> {
                            System.out.print("ID: "); Long id = Long.parseLong(scanner.nextLine());
                            userDao.findById(id).ifPresentOrElse(System.out::println, () -> System.out.println("Не найден."));
                        }
                        case "3" -> userDao.findAll().forEach(System.out::println);
                        case "4" -> {
                            System.out.print("ID: "); Long id = Long.parseLong(scanner.nextLine());
                            userDao.findById(id).ifPresent(userEntity -> {
                                System.out.print("Новое имя: "); userEntity.setName(scanner.nextLine());
                                userDao.update(userEntity);
                            });
                        }
                        case "5" -> {
                            System.out.print("ID для удаления: ");
                            userDao.delete(Long.parseLong(scanner.nextLine()));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка выполнения: " + e.getMessage());
                }
            }
        }
    }
}
