package by.vitikova.discovery.service;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.update.PasswordUpdateDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    boolean existsByLogin(String login);

    UserDto findByLogin(String login);

    UserDto findByLoginAndRole(String login, RoleName role);

    List<UserDto> findUsersByLastVisit(LocalDateTime lastVisit);

    Page<UserDto> findAll(Integer offset, Integer limit);

    UserDto create(UserCreateDto userDto);

    UserDto updatePassword(PasswordUpdateDto passwordUpdateDto);

    UserDto updateLastVisit(String login);

    void delete(String login, String token);

    void deleteAll(List<UserDto> list);
}