package by.vitikova.discovery.controller.impl;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.controller.UserController;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.LIMIT_DEFAULT;
import static by.vitikova.discovery.constant.Constant.OFFSET_DEFAULT;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/{login}")
    public Mono<ResponseEntity<UserDto>> findByLogin(@PathVariable("login") String login) {
        return userService.findByLogin(login)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{login}/{role}")
    public Mono<ResponseEntity<UserDto>> findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role) {
        return userService.findByLoginAndRole(login, role)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/lastVisit")
    public Mono<ResponseEntity<List<UserDto>>> findUsersByLastVisit(@RequestParam LocalDateTime lastVisit) {
        return userService.findUsersByLastVisit(lastVisit)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Collections.emptyList()));
    }

    @GetMapping("/exists/{login}")
    public Mono<ResponseEntity<Boolean>> existsByLogin(@PathVariable("login") String login) {
        return userService.existsByLogin(login)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<Page<UserDto>>> findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset,
                                                       @RequestParam(value = "limit", defaultValue = LIMIT_DEFAULT) Integer limit) {
        return userService.findAll(offset, limit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Page.empty()));
    }

    @PostMapping
    public Mono<ResponseEntity<UserDto>> create(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.create(userCreateDto)
                .map(userDto -> ResponseEntity.status(HttpStatus.CREATED).body(userDto));
    }

    @PutMapping("/password")
    public Mono<ResponseEntity<UserDto>> updatePassword(@RequestBody @Valid PasswordUpdateDto dto) {
        return userService.updatePassword(dto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{login}")
    public Mono<ResponseEntity<UserDto>> updateLastVisit(@PathVariable("login") String login) {
        return userService.updateLastVisit(login)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{login}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("login") String login,
                                             @RequestHeader("Authorization") String auth) {
        return userService.delete(login, auth)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteAll(@RequestBody List<String> list) {
        return userService.deleteAll(list)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}