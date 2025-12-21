package by.vitikova.discovery.controller;

import by.vitikova.discovery.ErrorDto;
import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.update.PasswordUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "User Management", description = "API для управления пользователями")
public interface UserController {

    @Operation(
            summary = "Получить пользователя по логину",
            description = "Возвращает информацию о пользователе по указанному логину"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @GetMapping("/{login}")
    Mono<ResponseEntity<UserDto>> findByLogin(String login);

    @Operation(
            summary = "Получить пользователя по логину и роли",
            description = "Возвращает пользователя по комбинации логина и роли"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @GetMapping("/{login}/{role}")
    Mono<ResponseEntity<UserDto>> findByLoginAndRole(String login, RoleName role);

    @Operation(
            summary = "Найти пользователей по дате последнего визита",
            description = "Возвращает список пользователей, чей последний визит был до указанной даты"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей (может быть пустым)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto[].class)
                    )
            )
    })
    @GetMapping("/lastVisit")
    Mono<ResponseEntity<List<UserDto>>> findUsersByLastVisit(LocalDateTime lastVisit);

    @Operation(
            summary = "Проверить существование пользователя",
            description = "Проверяет, существует ли пользователь с указанным логином"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат проверки",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "boolean")
                    )
            )
    })
    @GetMapping("/exists/{login}")
    Mono<ResponseEntity<Boolean>> existsByLogin(String login);

    @Operation(
            summary = "Получить всех пользователей с пагинацией",
            description = "Возвращает страницу пользователей с поддержкой пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница пользователей",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping
    Mono<ResponseEntity<Page<UserDto>>> findAll(Integer offset, Integer limit);

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя с указанными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя (пароли не совпадают или пустые поля)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким логином уже существует",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @PostMapping
    Mono<ResponseEntity<UserDto>> create(UserCreateDto userCreateDto);

    @Operation(
            summary = "Обновить пароль пользователя",
            description = "Обновляет пароль для существующего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пароль успешно обновлен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные: "
                            + "1. Старый пароль пустой"
                            + "2. Новый пароль идентичен старому"
                            + "3. Пароли не совпадают"
                            + "4. Ошибка валидации полей",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @PutMapping("/password")
    Mono<ResponseEntity<UserDto>> updatePassword(PasswordUpdateDto dto);

    @Operation(
            summary = "Обновить дату последнего визита",
            description = "Обновляет дату последнего визита пользователя на текущее время"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Дата обновлена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @PutMapping("/{login}")
    Mono<ResponseEntity<UserDto>> updateLastVisit(String login);

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по логину. Пользователь не может удалить сам себя."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно удален"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Пользователь пытается удалить самого себя",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Невалидный токен авторизации",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @DeleteMapping("/{login}")
    Mono<ResponseEntity<Void>> delete(String login, String auth);

    @Operation(
            summary = "Массовое удаление пользователей",
            description = "Удаляет несколько пользователей по их логинам"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователи успешно удалены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный список логинов",
                    content = @Content(
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @DeleteMapping
    Mono<ResponseEntity<Void>> deleteAll(List<String> list);
}