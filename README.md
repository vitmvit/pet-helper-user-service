# pet-helper-user-service

Данный микросервис предоставляет функционал для работы с пользователем.

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
- удаление пользователмя по логину
- удаление списка пользователей

### GET-запросы:

#### findByLogin(@PathVariable("login") String login)

Request:

```http request
http://localhost:8081/api/v1/users/support1@mail.com
```

Response:

```json
{
  "id": "660daccb4853be52971c0daf",
  "login": "support1@mail.com",
  "password": "$2a$10$aOTx.qSzodaiJ6ABXJQqM.XvYcFlQR2ucGud0TYMKVoALUq640cD6",
  "role": "SUPPORT",
  "createDate": "2024-04-03T22:23:55.703",
  "lastVisit": "2024-04-05T01:29:28.875"
}
```

Error:

```json
{
  "errorMessage": "Entity not found!",
  "errorCode": 404
}
```

#### findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role)

Request:

```http request
http://localhost:8081/api/v1/users/support1@mail.com/SUPPORT
```

Response:

```json
{
  "id": "660daccb4853be52971c0daf",
  "login": "support1@mail.com",
  "password": "$2a$10$aOTx.qSzodaiJ6ABXJQqM.XvYcFlQR2ucGud0TYMKVoALUq640cD6",
  "role": "SUPPORT",
  "createDate": "2024-04-03T22:23:55.703",
  "lastVisit": "2024-04-05T01:29:28.875"
}
```

Error:

```json
{
  "errorMessage": "Entity not found!",
  "errorCode": 404
}
```

#### existsByLogin(@PathVariable("login") String login)

Request:

```http request
http://localhost:8081/api/v1/users/exists/support1@mail.com
```

Response:

```text
true
```

Request:

```http request
http://localhost:8081/api/v1/users/exists/support1@mail.com2
```

Response:

```text
false
```

#### findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset, @RequestParam(value = "limit", defaultValue = LIMIT_DEFAULT) Integer limit

Request:

```http request
http://localhost:8081/api/v1/users
```

Response:

```json
{
  "content": [
    {
      "id": "6604580e2446c079fac07b70",
      "login": "ADMIN1",
      "password": "$2a$10$F/6Mcn0lilCfjiDy5Up5iudAVRZhowgfcXx5QEJC39DFYeb5A6OHu",
      "role": "ADMIN",
      "createDate": "2024-03-27T20:31:58.255",
      "lastVisit": "2024-03-31T13:09:50.841"
    },
    {
      "id": "66045c892446c079fac07b72",
      "login": "SUPPORT1",
      "password": "SUPPORT1",
      "role": "SUPPORT",
      "createDate": "2024-03-27T20:51:05.528",
      "lastVisit": "2024-04-01T22:25:55.984"
    },
    {
      "id": "66080f497c94f65697377704",
      "login": "USER1",
      "password": "$2a$10$GCBo7hmz47iC6yR0NJ73yOzS.VRczGcjNhKJ7mJbeJyjg482v2k4q",
      "role": "USER",
      "createDate": "2024-03-30T16:10:33.577",
      "lastVisit": "2024-03-30T18:10:43.381"
    },
    {
      "id": "660810427c94f65697377705",
      "login": "USER4",
      "password": "$2a$10$UyJQASaKB/ksZ8DKXawJ9uhieeYSxBlfPXvnR.cywvAnOXBC.UFbu",
      "role": "USER",
      "createDate": "2024-03-30T16:14:42.937",
      "lastVisit": "2024-03-30T16:14:42.937"
    },
    {
      "id": "660abf34295e95149bf61815",
      "login": "SUPPORT2",
      "password": "$2a$10$UO6bHdOzszuB8cPJ6veOXOY66W.tH/F0IxA22K9JwZWg3ZylFxFdC",
      "role": "SUPPORT",
      "createDate": "2024-04-01T17:05:40.946",
      "lastVisit": "2024-04-03T22:22:23.099"
    },
    {
      "id": "660daccb4853be52971c0daf",
      "login": "support1@mail.com",
      "password": "$2a$10$aOTx.qSzodaiJ6ABXJQqM.XvYcFlQR2ucGud0TYMKVoALUq640cD6",
      "role": "SUPPORT",
      "createDate": "2024-04-03T22:23:55.703",
      "lastVisit": "2024-04-05T01:29:28.875"
    },
    {
      "id": "660dacd54853be52971c0db0",
      "login": "support2@mail.com",
      "password": "$2a$10$9K36ii.tx8yQEvbTV7O45.zEyg4VUfmzxFX03UfKDQODr1hYeBbKy",
      "role": "SUPPORT",
      "createDate": "2024-04-03T22:24:05.459",
      "lastVisit": "2024-04-03T22:24:05.459"
    },
    {
      "id": "660dacda4853be52971c0db1",
      "login": "support3@mail.com",
      "password": "$2a$10$htsb9n5TpZIG/4Nf2Qkdee/aU8qvfcL9dKJJfod74.sQ1WPzsLpja",
      "role": "SUPPORT",
      "createDate": "2024-04-03T22:24:10.378",
      "lastVisit": "2024-04-03T22:24:10.378"
    },
    {
      "id": "660dacdf4853be52971c0db2",
      "login": "support4@mail.com",
      "password": "$2a$10$Kiy3OQ2A8ddCYiphCgSU9.iMUHAn4auTaS.zj.B9BmWaYJD962ZR6",
      "role": "SUPPORT",
      "createDate": "2024-04-03T22:24:15.383",
      "lastVisit": "2024-04-03T22:24:15.383"
    },
    {
      "id": "660dace44853be52971c0db3",
      "login": "support5@mail.com",
      "password": "$2a$10$/tzKcihg4Us/4VtnrOIfC.cS9.9jCU9bksleCxTr8daSCTocovjbe",
      "role": "SUPPORT",
      "createDate": "2024-04-03T22:24:20.872",
      "lastVisit": "2024-04-03T22:24:20.872"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": [],
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 4,
  "totalElements": 35,
  "last": false,
  "size": 10,
  "number": 0,
  "sort": [],
  "numberOfElements": 10,
  "first": true,
  "empty": false
}
```

### POST-запросы:

#### create(@RequestBody UserCreateDto userCreateDto)

Request:

```http request
http://localhost:8081/api/v1/users
```

Body:

```json
{
  "login": "admin10@mail.com",
  "password": "admin10@mail.com",
  "passwordConfirm": "admin10@mail.com",
  "role": "ADMIN"
}
```

Response:

```json
{
  "id": "6610066f38bc1b1c4d213fd5",
  "login": "admin10@mail.com",
  "password": "admin10@mail.com",
  "role": "ADMIN",
  "createDate": "2024-04-05T17:10:54.998591",
  "lastVisit": "2024-04-05T17:10:54.999489"
}
```

### PUT-запросы:

#### updatePassword(@RequestBody PasswordUpdateDto dto)

Request:

```http request
http://localhost:8081/api/v1/users/password
```

Body:

```json
{
  "login": "support1@mail.com",
  "oldPassword": "support1@mail.com",
  "newPassword": "support1@mail.com1",
  "confirmPassword": "support1@mail.com1"
}
```

Response:

```json
{
  "id": "660daccb4853be52971c0daf",
  "login": "support1@mail.com",
  "password": "$2a$10$F1t3OC/.LyZ9WquyDYFcHeZSXs/gy1Snmpz5RqKQPM8741XuqR4mu",
  "role": "SUPPORT",
  "createDate": "2024-04-03T22:23:55.703",
  "lastVisit": "2024-04-05T01:29:28.875"
}
```

Error:

```json
{
  "errorMessage": "Incorrect old password",
  "errorCode": 500
}
```

#### updateLastVisit(@PathVariable("login") String login)

Request:

```http request
http://localhost:8081/api/v1/users/support1@mail.com
```

Response:

```json
{
  "id": "660daccb4853be52971c0daf",
  "login": "support1@mail.com",
  "password": "$2a$10$m6Co2AL9e./9RJcpKt0AYO9giHC245L/sgoenPLWgm9GjK3oNhnXG",
  "role": "SUPPORT",
  "createDate": "2024-04-03T22:23:55.703",
  "lastVisit": "2024-04-05T20:46:45.591914"
}
```

### DELETE-запросы:

Не возвращают ничего:

- delete(@PathVariable("login") String login, @RequestHeader("Authorization") String auth)
- deleteAll(@RequestBody List<UserDto> list)