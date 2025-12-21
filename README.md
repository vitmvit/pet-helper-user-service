# pet-helper-user-service

[Точка входа в приложение](https://github.com/vitmvit/pet-helper-api-gateway-service)

Микросервис для управления пользователями, построенный на Java Spring Boot с использованием реактивного подхода (
WebFlux + Reactive MongoDB). Сервис предоставляет REST API для операций CRUD над пользователями, управления паролями и
отслеживания активности.

## Технический стек

- Java 17+
- Spring Boot 3.2.1
- Spring WebFlux (реактивный REST API)
- Spring Data MongoDB Reactive
- Spring Security
- Spring Cloud Netflix Eureka Client
- Project Reactor (Mono/Flux)
- Lombok
- SpringDoc OpenAPI

## Доступ по ролям

- доступен всем

## Swagger

http://localhost:8081/api/doc/swagger-ui/index.html#/

## Порт

```text
8081
```

## Валидация DTO моделей на UserController

### Таблица валидации UserCreateDto

| Поле            | Тип      | Обязательно | Правила валидации           | Сообщение об ошибке                                                     |
|-----------------|----------|-------------|-----------------------------|-------------------------------------------------------------------------|
| login           | String   | Да          | Валидный email формат       | "Email is required"<br>"Email should be valid"                          |
| password        | String   | Да          | Минимум 6 символов          | "Password is required"<br>"Password must be at least 6 characters long" |
| passwordConfirm | String   | Да          | Должно совпадать с password | "Password confirmation is required"                                     |
| role            | RoleName | Да          | Не может быть null          | "Role is required"                                                      |

### Таблица валидации PasswordUpdateDto

| Поле            | Тип    | Обязательно | Правила валидации              | Сообщение об ошибки                                                             |
|-----------------|--------|-------------|--------------------------------|---------------------------------------------------------------------------------|
| login           | String | Да          | Валидный email формат          | "Email is required"<br>"Email should be valid"                                  |
| oldPassword     | String | Да          | Любая непустая строка          | "Current password is required"                                                  |
| newPassword     | String | Да          | Минимум 6 символов             | "New password is required"<br>"New password must be at least 6 characters long" |
| confirmPassword | String | Да          | Должно совпадать с newPassword | "Password confirmation is required"                                             |

## UserController (8081/api/v1/users)

Контроллер поддерживает следующие операции:

- поиск пользователя по логину
- поиск пользователя по логину и роли
- поиск пользователей по последнему визиту (для шедулеров удаления)
- проверка существования по логину
- вывод всех пользователей
- создание пользователя
- обновление пароля
- обновление даты последнего визита
- удаление пользователя по логину
- удаление списка пользователей по списку логинов

### GET-запросы:

#### findByLogin(@PathVariable("login") String login)

##### Успешный поиск

Request:

```http request
http://localhost:8081/api/v1/users/admin1@mail.com
```

Response:

```json
{
  "id": "6945530dd6e294613a55af49",
  "login": "admin1@mail.com",
  "password": "$2a$10$Iz2/ME9tA9S8bmllxdiKYeecx2MT1.9Tw8RBC4Jpf.tzzB6FwBQ3O",
  "role": "ADMIN",
  "createDate": "2025-12-19T16:28:45.268",
  "lastVisit": "2025-12-19T16:28:45.268"
}
```

Error:

```json
{
  "errorMessage": "Entity not found!",
  "errorCode": 404
}
```

##### Пользователь не найден

Request:

```http request
http://localhost:8081/api/v1/users/admin12@mail.com
```

Response:

```json
{
  "errorMessage": "User not found with login: admin12@mail.com",
  "errorCode": 404
}
```

#### findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role)

##### Успешный поиск

Request:

```http request
http://localhost:8081/api/v1/users/admin1@mail.com/ADMIN
```

Response:

```json
{
  "id": "6945530dd6e294613a55af49",
  "login": "admin1@mail.com",
  "password": "$2a$10$Iz2/ME9tA9S8bmllxdiKYeecx2MT1.9Tw8RBC4Jpf.tzzB6FwBQ3O",
  "role": "ADMIN",
  "createDate": "2025-12-19T16:28:45.268",
  "lastVisit": "2025-12-19T16:28:45.268"
}
```

##### Не верный логин

Request:

```http request
http://localhost:8081/api/v1/users/admin12@mail.com/ADMIN
```

Response:

```json
{
  "errorMessage": "User not found with login and role: admin12@mail.com, ADMIN",
  "errorCode": 404
}
```

##### Не верный логин

Request:

```http request
http://localhost:8081/api/v1/users/support12@mail.com/ADMIN
```

Response:

```json
{
  "errorMessage": "User not found with login and role: support12@mail.com, ADMIN",
  "errorCode": 404
}
```

#### findUsersByLastVisit(@RequestParam LocalDateTime lastVisit)

##### Успешный поиск

Request:

```http request
http://localhost:8081/api/v1/users/lastVisit?lastVisit=2025-12-19T20:11:16.908732
```

Response:

```json
[
  {
    "id": "6945530dd6e294613a55af49",
    "login": "admin1@mail.com",
    "password": "$2a$10$Iz2/ME9tA9S8bmllxdiKYeecx2MT1.9Tw8RBC4Jpf.tzzB6FwBQ3O",
    "role": "ADMIN",
    "createDate": "2025-12-19T16:28:45.268",
    "lastVisit": "2025-12-19T16:28:45.268"
  },
  {
    "id": "6945547cd6e294613a55af4a",
    "login": "admin2@mail.com",
    "password": "$2a$10$IgZWQKJ0DH3u8Z1BDvTQEuRAtmeEMK/kGWWDCF8yOxm4mePvBonEy",
    "role": "ADMIN",
    "createDate": "2025-12-19T16:34:52.786",
    "lastVisit": "2025-12-19T16:34:52.786"
  },
  {
    "id": "6945548dd6e294613a55af4b",
    "login": "admin3@mail.com",
    "password": "$2a$10$s0zc9LFzfQVOyO4GVcziDeGtoZFKEvZ4dQ1Ztiozjn3xOB6.zvmXW",
    "role": "ADMIN",
    "createDate": "2025-12-19T16:35:09.184",
    "lastVisit": "2025-12-19T16:35:09.184"
  }
]
```

##### Пустой ответ

Request:

```http request
http://localhost:8081/api/v1/users/lastVisit?lastVisit=2025-11-19T20:11:16.908732
```

Response:

```json
[]
```

#### existsByLogin(@PathVariable("login") String login)

##### Пользователь существует

Request:

```http request
http://localhost:8081/api/v1/users/exists/admin1@mail.com
```

Response:

```text
true
```

##### Пользователь не существует

Request:

```http request
http://localhost:8081/api/v1/users/exists/admin12@mail.com
```

Response:

```text
false
```

#### findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset, @RequestParam(value = "limit", defaultValue = LIMIT_DEFAULT) Integer limit

##### Запрос с дефолтной пагинацией

Request:

```http request
http://localhost:8081/api/v1/users
```

Response:

```json
{
  "content": [
    {
      "id": "6945530dd6e294613a55af49",
      "login": "admin1@mail.com",
      "password": "$2a$10$Iz2/ME9tA9S8bmllxdiKYeecx2MT1.9Tw8RBC4Jpf.tzzB6FwBQ3O",
      "role": "ADMIN",
      "createDate": "2025-12-19T16:28:45.268",
      "lastVisit": "2025-12-19T16:28:45.268"
    },
    {
      "id": "6945547cd6e294613a55af4a",
      "login": "admin2@mail.com",
      "password": "$2a$10$IgZWQKJ0DH3u8Z1BDvTQEuRAtmeEMK/kGWWDCF8yOxm4mePvBonEy",
      "role": "ADMIN",
      "createDate": "2025-12-19T16:34:52.786",
      "lastVisit": "2025-12-19T16:34:52.786"
    },
    {
      "id": "6945548dd6e294613a55af4b",
      "login": "admin3@mail.com",
      "password": "$2a$10$s0zc9LFzfQVOyO4GVcziDeGtoZFKEvZ4dQ1Ztiozjn3xOB6.zvmXW",
      "role": "ADMIN",
      "createDate": "2025-12-19T16:35:09.184",
      "lastVisit": "2025-12-19T16:35:09.184"
    },
    {
      "id": "69458e338ec2ef43a439e1ab",
      "login": "support1@mail.com",
      "password": "$2a$10$56bO5MHmiQmShvlEbvLnO.R9c5yfxMKLH3LvVK9kFLzEgqjPtvL3W",
      "role": "SUPPORT",
      "createDate": "2025-12-19T20:41:07.343",
      "lastVisit": "2025-12-19T20:41:07.343"
    },
    {
      "id": "69459073a843694e7c2a7047",
      "login": "support2@mail.com",
      "password": "$2a$10$s3W6kQCjnDax3vH1uJSaJOjFOqcn.08mahuWz.Btv6HKpELrjEXFS",
      "role": "SUPPORT",
      "createDate": "2025-12-19T20:50:43.187",
      "lastVisit": "2025-12-19T20:50:43.187"
    },
    {
      "id": "69459079a843694e7c2a7048",
      "login": "support3@mail.com",
      "password": "$2a$10$X44BfXbB19R3Y2z7CFvBdO038XyUeJKxhoBMnzZd6aly5/0Q1fIsm",
      "role": "SUPPORT",
      "createDate": "2025-12-19T20:50:49.223",
      "lastVisit": "2025-12-19T20:50:49.223"
    },
    {
      "id": "6945907ea843694e7c2a7049",
      "login": "support4@mail.com",
      "password": "$2a$10$tHl55pl46JidL7bacjw88.Tecs9voNEKB9Zso2EK/YavciRKpp7xS",
      "role": "SUPPORT",
      "createDate": "2025-12-19T20:50:54.696",
      "lastVisit": "2025-12-19T20:50:54.696"
    },
    {
      "id": "69459087a843694e7c2a704a",
      "login": "user1@mail.com",
      "password": "$2a$10$LuPWlmXz38UcLK2PNlH7k.nB78PeoQSK6MjQ35n2nt97qfVyI9T8W",
      "role": "USER",
      "createDate": "2025-12-19T20:51:03.608",
      "lastVisit": "2025-12-19T20:51:03.608"
    },
    {
      "id": "6945908ea843694e7c2a704b",
      "login": "user2@mail.com",
      "password": "$2a$10$8cQHU7CmTo8ox5YViQhwxuynCZI4tUNv29nCqdX6gOjH7BT8gxaLG",
      "role": "USER",
      "createDate": "2025-12-19T20:51:10.235",
      "lastVisit": "2025-12-19T20:51:10.235"
    },
    {
      "id": "69459093a843694e7c2a704c",
      "login": "user3@mail.com",
      "password": "$2a$10$AibszCL699EOgKQqXXbA2uWExVpPawwbFObitD2iPhWl94fClTzRm",
      "role": "USER",
      "createDate": "2025-12-19T20:51:15.748",
      "lastVisit": "2025-12-19T20:51:15.748"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 10,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 10,
  "first": true,
  "empty": false
}
```

##### Запрос с пользовательской пагинацией

Request:

```http request
http://localhost:8081/api/v1/users?offset=2&limit=3
```

Response:

```json
{
  "content": [
    {
      "id": "6945907ea843694e7c2a7049",
      "login": "support4@mail.com",
      "password": "$2a$10$tHl55pl46JidL7bacjw88.Tecs9voNEKB9Zso2EK/YavciRKpp7xS",
      "role": "SUPPORT",
      "createDate": "2025-12-19T20:50:54.696",
      "lastVisit": "2025-12-19T20:50:54.696"
    },
    {
      "id": "69459087a843694e7c2a704a",
      "login": "user1@mail.com",
      "password": "$2a$10$LuPWlmXz38UcLK2PNlH7k.nB78PeoQSK6MjQ35n2nt97qfVyI9T8W",
      "role": "USER",
      "createDate": "2025-12-19T20:51:03.608",
      "lastVisit": "2025-12-19T20:51:03.608"
    },
    {
      "id": "6945908ea843694e7c2a704b",
      "login": "user2@mail.com",
      "password": "$2a$10$8cQHU7CmTo8ox5YViQhwxuynCZI4tUNv29nCqdX6gOjH7BT8gxaLG",
      "role": "USER",
      "createDate": "2025-12-19T20:51:10.235",
      "lastVisit": "2025-12-19T20:51:10.235"
    }
  ],
  "pageable": {
    "pageNumber": 2,
    "pageSize": 3,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 6,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 4,
  "totalElements": 10,
  "last": false,
  "size": 3,
  "number": 2,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 3,
  "first": false,
  "empty": false
}
```

### POST-запросы:

#### create(@RequestBody UserCreateDto userCreateDto)

##### Успешное создание

Request:

```http request
http://localhost:8081/api/v1/users
```

Body:

```json
{
  "login": "admin1@mail.com",
  "password": "admin1@mail.com",
  "passwordConfirm": "admin1@mail.com",
  "role": "ADMIN"
}
```

Response:

```json
{
  "id": "6945530dd6e294613a55af49",
  "login": "admin1@mail.com",
  "password": "$2a$10$Iz2/ME9tA9S8bmllxdiKYeecx2MT1.9Tw8RBC4Jpf.tzzB6FwBQ3O",
  "role": "ADMIN",
  "createDate": "2025-12-19T16:28:45.268237",
  "lastVisit": "2025-12-19T16:28:45.268281"
}
```

##### Повторное создание

Request:

```http request
http://localhost:8081/api/v1/users
```

Body:

```json
{
  "login": "admin1@mail.com",
  "password": "admin1@mail.com",
  "passwordConfirm": "admin1@mail.com",
  "role": "ADMIN"
}
```

Response:

```json
{
  "errorMessage": "Username is exists",
  "errorCode": 409
}
```

##### Не подтвержден пароль

Request:

```http request
http://localhost:8081/api/v1/users
```

Body:

```json
{
  "login": "admin3@mail.com",
  "password": "admin1@mail.com",
  "passwordConfirm": "admin3@mail.com",
  "role": "ADMIN"
}
```

Response:

```json
{
  "errorMessage": "Passwords do not match",
  "errorCode": 400
}
```

### PUT-запросы:

#### updatePassword(@RequestBody PasswordUpdateDto dto)

##### Успешное обновление

Request:

```http request
http://localhost:8081/api/v1/users/password
```

Body:

```json
{
  "login": "admin1@mail.com",
  "oldPassword": "admin1@mail.com",
  "newPassword": "adminNew@mail.com1",
  "confirmPassword": "adminNew@mail.com1"
}
```

Response:

```json
{
  "id": "6945530dd6e294613a55af49",
  "login": "admin1@mail.com",
  "password": "$2a$10$p8R.vLXupfMQDG9/ukN/7exIf.rCP0UDNgUGh1rhDeM1KUhWpyNMm",
  "role": "ADMIN",
  "createDate": "2025-12-19T16:28:45.268",
  "lastVisit": "2025-12-19T16:28:45.268"
}
```

##### Пользователь не найден

Request:

```http request
http://localhost:8081/api/v1/users/password
```

Body:

```json
{
  "login": "admin12@mail.com",
  "oldPassword": "admin1@mail.com",
  "newPassword": "adminNew@mail.com1",
  "confirmPassword": "adminNew@mail.com1"
}
```

Response:

```json
{
  "errorMessage": "User with lastname not found: admin12@mail.com",
  "errorCode": 404
}
```

##### Не верный текущий пароль

Request:

```http request
http://localhost:8081/api/v1/users/password
```

Body:

```json
{
  "login": "admin1@mail.com",
  "oldPassword": "admin12@mail.com",
  "newPassword": "adminNew@mail.com1",
  "confirmPassword": "adminNew@mail.com1"
}
```

Response:

```json
{
  "errorMessage": "Incorrect old password",
  "errorCode": 400
}
```

##### Одинаковые новый и старый пароли

Request:

```http request
http://localhost:8081/api/v1/users/password
```

Body:

```json
{
  "login": "admin1@mail.com",
  "oldPassword": "admin1@mail.com",
  "newPassword": "admin1@mail.com1",
  "confirmPassword": "admin1@mail.com1"
}
```

Response:

```json
{
  "errorMessage": "New password must be different from the old one",
  "errorCode": 400
}
```

##### Новый пароль и его подтверждение не совпали

Request:

```http request
http://localhost:8081/api/v1/users/password
```

Body:

```json
{
  "login": "admin10@mail.com",
  "oldPassword": "adminNew@mail.com",
  "newPassword": "admin10@mail.com",
  "confirmPassword": "adminNew@mail.com"
}
```

Response:

```json
{
  "errorMessage": "Password and confirmation do not match",
  "errorCode": 400
}
```

#### updateLastVisit(@PathVariable("login") String login)

##### Успешный запрос

Request:

```http request
http://localhost:8081/api/v1/users/admin1@mail.com
```

Response:

```json
{
  "id": "6945530dd6e294613a55af49",
  "login": "admin1@mail.com",
  "password": "$2a$10$p8R.vLXupfMQDG9/ukN/7exIf.rCP0UDNgUGh1rhDeM1KUhWpyNMm",
  "role": "ADMIN",
  "createDate": "2025-12-19T16:28:45.268",
  "lastVisit": "2025-12-19T22:50:29.639101"
}
```

##### Отсутствует пользователь

Request:

```http request
http://localhost:8081/api/v1/users/admin12@mail.com
```

Response:

```json
{
  "errorMessage": "User not found with login: admin12@mail.com",
  "errorCode": 404
}
```

### DELETE-запросы:

Не возвращают ничего:

- delete(@PathVariable("login") String login, @RequestHeader("Authorization") String auth)
- deleteAll(@RequestBody List<String> list)