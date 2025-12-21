package by.vitikova.discovery.service;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.update.PasswordUpdateDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    Mono<Boolean> existsByLogin(String login);

    Mono<UserDto> findByLogin(String login);

    Mono<UserDto> findByLoginAndRole(String login, RoleName role);

    Flux<UserDto> findUsersByLastVisit(LocalDateTime lastVisit);

    Mono<Page<UserDto>> findAll(Integer offset, Integer limit);

    Mono<UserDto> create(UserCreateDto userDto);

    Mono<UserDto> updatePassword(PasswordUpdateDto passwordUpdateDto);

    Mono<UserDto> updateLastVisit(String login);

    Mono<Void> delete(String login, String token);

    Mono<Void> deleteAll(List<String> list);
}