package by.vitikova.discovery.controller;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.LIMIT_DEFAULT;
import static by.vitikova.discovery.constant.Constant.OFFSET_DEFAULT;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{login}")
    public ResponseEntity<UserDto> findByLogin(@PathVariable("login") String login) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByLogin(login));
    }

    @GetMapping("/{login}/{role}")
    public ResponseEntity<UserDto> findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByLoginAndRole(login, role));
    }

    @GetMapping("/lastVisit")
    public ResponseEntity<List<UserDto>> findUsersByLastVisit(@RequestParam LocalDateTime lastVisit) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUsersByLastVisit(lastVisit));
    }

    @GetMapping("/exists/{login}")
    public ResponseEntity<Boolean> existsByLogin(@PathVariable("login") String login) {
        return ResponseEntity
                .status(HttpStatus.OK).body(userService.existsByLogin(login));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset,
                                                 @RequestParam(value = "limit", defaultValue = LIMIT_DEFAULT) Integer limit) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll(offset, limit));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserCreateDto userCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(userCreateDto));
    }

    @PutMapping("/password")
    public ResponseEntity<UserDto> updatePassword(@RequestBody PasswordUpdateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updatePassword(dto));
    }

    @PutMapping("/{login}")
    public ResponseEntity<UserDto> updateLastVisit(@PathVariable("login") String login) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateLastVisit(login));
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<Void> delete(@PathVariable("login") String login, @RequestHeader("Authorization") String auth) {
        userService.delete(login, auth);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestBody List<UserDto> list) {
        userService.deleteAll(list);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}