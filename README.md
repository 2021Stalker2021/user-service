# 👤 User Service

Микросервис управления пользователями, построенный на базе **Spring Boot 3.2.2** и распределенной архитектуры **Spring Cloud (2023.0.0)**. Сервис отвечает за регистрацию, хранение профилей, редактирование данных аккаунтов и генерацию бизнес-событий в брокер сообщений.

---

## 🛠️ Технологический стек и зависимости

Сервис использует современные библиотеки и подходы корпоративной Java-разработки:

* **Core Framework:** Spring Boot 3.2.2 & Spring Web (REST API).
* **Data Layer:** Spring Data JPA + Hibernate. Драйвер подключения: `org.postgresql:postgresql`.
* **Data Mapping:** **MapStruct (1.5.5)** + **Lombok** со специальным биндингом процессоров аннотаций для безопасной и быстрой генерации конвертеров между Entity и DTO.
* **API Стандарты:** **Spring HATEOAS** (Hypermedia As The Engine Of Application State) для реализации гибкого REST-интерфейса уровня зрелости Ричардсона 3.
* **Документация:** **Springdoc OpenAPI v2 (Swagger UI)** с русскоязычным описанием операций для интерактивного тестирования контрактов API.
* **Асинхронный стриминг:** `spring-kafka` для отправки сообщений о транзакциях и действиях пользователей (например, регистрация -> отправка уведомления).
* **Тестирование:** JUnit 5 + библиотека **Testcontainers** (модули `postgresql` и `spring-kafka-test`), позволяющая прогонять тесты в изолированных Docker-контейнерах.

---

## ⚙️ Конфигурация и Среда (Cloud Config)

Микросервис работает в режиме централизованного управления свойствами. Локальный файл `application.yml` содержит только метаданные для инициализации связи с Config-сервером:

```yaml
spring:
  application:
    name: user-service
  config:
    import: "optional:configserver:http://localhost:8888/"
```
*Все остальные рабочие настройки (строка подключения к PostgreSQL, топики Kafka, уровни логирования) подтягиваются динамически из файла `user-service.yml` на удаленном сервере конфигураций.*

---

## 🛰️ Спецификация API (Endpoints & HATEOAS)

Все запросы к сервису проходят через префикс пути `/users`. Проект поддерживает стандарт **HATEOAS**, возвращая вместе с данными пользователя ссылки на доступные действия.

### Список доступных ресурсов:

| Метод | URL-путь | Описание контракта | Тело запроса (Payload) |
| :--- | :--- | :--- | :--- |
| **GET** | `/users/{id}` | Получить пользователя по ID | *Отсутствует* |
| **POST** | `/users/create` | Создать нового пользователя | `NewUserRequest` (Валидируемый) |
| **PUT** | `/users/update/{id}` | Обновить данные пользователя | `UpdateUserRequest` (Валидируемый) |
| **DELETE** | `/users/delete/{id}` | Удалить пользователя | *Отсутствует* |

### Пример структуры HATEOAS-ответа (`GET /users/1`):
```json
{
  "id": 1,
  "username": "ivan_developer",
  "email": "ivan@example.com",
  "_links": {
    "self": {
      "href": "http://localhost:8081/users/1"
    },
    "update": {
      "href": "http://localhost:8081/users/update/1"
    },
    "delete": {
      "href": "http://localhost:8081/users/delete/1"
    }
  }
}
```

---

## 📈 Асинхронное взаимодействие (Apache Kafka)

При выполнении ключевых операций над пользователем слой `producer` генерирует событие `UserEvent` и асинхронно отправляет его в кластер **Apache Kafka** для обработки другими микросервисами (например, `notification-service`).

* **Компонент отправки:** `KafkaUserProducer` (использует настроенный `KafkaTemplate`).
* **Управление топиками:** Имя целевого топика конфигурируется динамически через централизованное свойство `${spring.kafka.topic.name}`.

### Типы событий (`ActionType`):
* `CREATE` — отправляется сразу после успешной регистрации пользователя.
* `DELETE` — отправляется при удалении аккаунта из системы.

### Пример структуры JSON-сообщения в топике:
```json
{
  "email": "ivan@example.com",
  "actionType": "CREATE"
}
```

---

## 📂 Архитектурная структура модулей

```text
user-service/
├── gradle/wrapper/          # Настройки сборщика Gradle Wrapper
└── src/
    ├── test/                # Интеграционные тесты с использованием Testcontainers
    └── main/
        ├── resources/       # Базовые свойства инициализации приложения
        └── java/io/gsc/     
            ├── UserServiceApplication.java # Главный класс запуска микросервиса
            ├── advice/      # Глобальный перехват и валидация ошибок (@ControllerAdvice)
            ├── config/      # Настройка бинов интеграции (Kafka Producers, Swagger, Security)
            ├── controller/  # REST-контроллеры с поддержкой HATEOAS-ссылок и Swagger-валидацией
            ├── mapper/      # Интерфейсы маппинга MapStruct
            ├── repository/  # Spring Data JPA репозитории для PostgreSQL
            ├── producer/    # Отправка бизнес-событий в кластер Apache Kafka (UserEvent)
            ├── utils/       # Вспомогательные классы и утилиты логирования
            ├── service/     # Сервисный слой (Интерфейсы бизнес-логики и их реализации в /impl)
            └── model/       
                ├── entity/  # JPA-сущности для базы данных
                ├── dto/     # Выходные DTO-модели с поддержкой RepresentationModel (HATEOAS)
                ├── request/ # Модели входящих запросов с валидацией (`@Valid`, `@NotNull`)
                ├── event/   # Схема события UserEvent (email, actionType) для очередей Kafka
                └── constants/ # Перечисления (Enums) и логируемые сообщения (ApiLogMessage)
```

---

## 📡 Интерактивная документация (Swagger)

Полная интерактивная спецификация API доступна при запуске сервиса по следующим адресам:
* **Swagger UI:** `http://localhost:<порт-сервиса>/swagger-ui/index.html`
* **OpenAPI Specs (JSON):** `http://localhost:<порт-сервиса>/v3/api-docs`

---

## 🚀 Команды для сборки и локального тестирования

Проект собирается с помощью Gradle. Для локальной компиляции требуется установленный JDK версии **17 или 21**.

* **Запуск тестов (требуется запущенный Docker для Testcontainers):**
  ```bash
  ./gradlew test
  ```
* **Сборка проекта в переносимый JAR-файл (без тестов):**
  ```bash
  ./gradlew clean build -x test
  ```
* **Локальный запуск скомпилированного артефакта:**
  ```bash
  java -jar build/libs/user-service-1.0-SNAPSHOT.jar
  ```
