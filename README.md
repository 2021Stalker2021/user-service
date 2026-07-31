# 👤 User Service

Микросервис управления пользователями, профилями и аутентификацией. Построен на базе распределенной архитектуры **Spring Cloud** и реализует событийно-ориентированный подход (EDA) с помощью брокера сообщений **Apache Kafka**.

---

## 🚀 Основной функционал

* 🔑 **REST API & HATEOAS:** Предоставляет зрелое REST API, обогащенное гипермедиа-ссылками на связанные действия (`self`, `update`, `delete`) согласно принципам HATEOAS.
* 🛡️ **Валидация:** Строгая проверка входящих запросов на уровне контроллера с помощью аннотаций `@Validated` и `@Valid`.
* 🔄 **MapStruct:** Высокопроизводительный автоматический маппинг данных между Entity, DTO (`UserDTO`) и объектами запросов (`NewUserRequest`, `UpdateUserRequest`).
* 📨 **Apache Kafka:** Асинхронная публикация событий в шину данных для мгновенного уведомления других компонентов системы.
* 📝 **Trace-логирование:** Глубокое логирование вызова методов контроллера (`log.trace`) для прозрачной отладки в распределенной среде.

---

## 🛠️ Стек технологий

* **Core & Web:** Java 17+ / Spring Boot 3.2.2 / Spring Web / Spring HATEOAS
* **Cloud Infrastructure:** Spring Cloud Config Client / Netflix Eureka Client
* **Data Layer:** Spring Data JPA / PostgreSQL Driver / MapStruct 1.5.5
* **Messaging:** Spring Kafka (Producer)
* **API Documentation:** Springdoc OpenAPI UI 2.3.0 (Swagger Annotations)
* **Testing:** JUnit 5 / Testcontainers (PostgreSQL) / Spring Kafka Test

---

## 📡 REST API Спецификация

Внутренний порт сервиса определяется динамически через Config Server, но все внешние клиентские запросы проходят через общий шлюз `api-gateway` (порт `8080`) по базовому маршруту `/users/*`.

### 📖 Интерактивный Swagger (OpenAPI)
Полная интерактивная спецификация API с описанием схем данных и возможностью отправки тестовых запросов доступна после локального запуска приложения по адресу:
* `http://localhost:<порт_сервиса>/swagger-ui/index.html`

### Конечные точки (Эндпоинты)

| Метод | Конечная точка | Описание | Доступ |
| :--- | :--- | :--- | :--- |
| `GET` | `/users/{id}` | Получить пользователя по ID (с HATEOAS-ссылками) | Авторизованный |
| `POST` | `/users/create` | Создать нового пользователя | Публичный |
| `PUT` | `/users/update/{id}` | Обновить данные существующего пользователя | Авторизованный |
| `DELETE` | `/users/delete/{id}` | Удалить пользователя из системы | Администратор |

### Примеры работы с API

#### 1. Создание пользователя (`POST /users/create`)
**Тело запроса (`NewUserRequest`):**
```json
{
  "username": "stalker2021",
  "email": "stalker@example.com",
  "password": "strong_password123"
}
```

#### 2. Получение пользователя (`GET /users/1`)
**Формат ответа (`UserDTO` с гипермедиа-ссылками):**
```json
{
  "id": 1,
  "username": "stalker2021",
  "email": "stalker@example.com",
  "_links": {
    "self": {
      "href": "http://localhost:8080/users/1"
    },
    "update": {
      "href": "http://localhost:8080/users/update/1"
    },
    "delete": {
      "href": "http://localhost:8080/users/delete/1"
    }
  }
}
```

---

## ⚙️ Конфигурация и запуск

Сервис является клиентом инфраструктуры Spring Cloud. Его локальный файл настроек `application.yml` содержит минимальные свойства для подключения к центральному серверу конфигураций:

```yaml
spring:
  application:
    name: user-service
  config:
    import: "optional:configserver:http://localhost:8888/"
```

### Порядок локального запуска:
1. Убедитесь, что общая инфраструктура (`discovery-server`, `config-server` и `kafka`) уже запущена (например, через глобальный `docker-compose.yml`).
2. Запустите класс `UserApplication.java` из вашей IDE или выполните команду в терминале:
```bash
./gradlew bootRun
```

---

## 🧪 Автоматическое тестирование

В репозитории настроено полноценное окружение для интеграционного тестирования:
* **Слой данных:** Тестируется на реальной БД PostgreSQL, поднимаемой «на лету» в Docker через **Testcontainers**.
* **Шина сообщений:** Интеграция с очередями эмулируется компонентом **Spring Kafka Test**.

Запуск тестов:
```bash
./gradlew test
```
