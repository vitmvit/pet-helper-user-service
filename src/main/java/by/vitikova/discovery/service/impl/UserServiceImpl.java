package by.vitikova.discovery.service.impl;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.exception.*;
import by.vitikova.discovery.mapper.UserConverter;
import by.vitikova.discovery.model.User;
import by.vitikova.discovery.repository.UserRepository;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import by.vitikova.discovery.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.DELETE_EXCEPTION;
import static by.vitikova.discovery.constant.Constant.USERNAME_IS_EXIST;

// todo почистить как будет нормальная сага
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    @Override
    public boolean existsByLogin(String login) {
        log.info("UserService: exist by login: " + login);
        return userRepository.existsByLogin(login);
    }

    //    @Cacheable(value = "user", key = "#login")
    @Override
    public UserDto findByLogin(String login) {
        log.info("UserService: find user by login: " + login);
        return userConverter.convert(userRepository.findByLogin(login).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public UserDto findByLoginAndRole(String login, RoleName role) {
        log.info("UserService: find user by login: " + login + ", end role: " + role);
        return userConverter.convert(userRepository.findByLoginAndRole(login, role).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public List<UserDto> findUsersByLastVisit(LocalDateTime lastVisit) {
        log.info("UserService: find users by last visit: " + lastVisit);
        return userRepository.findUsersByLastVisitBefore(lastVisit).stream().map(userConverter::convert).toList();
    }

    @Override
    public Page<UserDto> findAll(Integer offset, Integer limit) {
        log.info("UserService: find all users");
        Page<User> commentPage = userRepository.findAll(PageRequest.of(offset, limit));
        commentPage.stream().findAny().orElseThrow(EmptyListException::new);
        return commentPage.map(userConverter::convert);
    }

    //    @CacheEvict(value = "users", key = "#passwordUpdateDto.login")
    @Transactional
    @Override
    public UserDto updatePassword(PasswordUpdateDto passwordUpdateDto) {
        var user = userRepository.findByLogin(passwordUpdateDto.getLogin()).orElseThrow(EntityNotFoundException::new);
        if (passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            if (passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
                log.info("UserService: update user: " + passwordUpdateDto.getLogin());
                String encodePassword = passwordEncoder.encode(passwordUpdateDto.getNewPassword());
                user.setPassword(encodePassword);
                return userConverter.convert(userRepository.save(user));
            } else {
                log.error("UserService: Password update exception");
                throw new PasswordUpdateException("Password must be identical");
            }
        } else {
            log.error("UserService: Incorrect old password");
            throw new PasswordUpdateException("Incorrect old password");
        }
    }

    @Override
    public UserDto create(UserCreateDto dto) {
        if (Boolean.FALSE.equals(userRepository.existsByLogin(dto.getLogin()))) {
            log.info("UserService: create user: " + dto.getLogin());
            var user = userConverter.convert(dto);
            user.setCreateDate(LocalDateTime.now());
            user.setLastVisit(LocalDateTime.now());
            return userConverter.convert(userRepository.save(user));
        }
        log.error("UserService: username is exist");
        throw new InvalidJwtException(USERNAME_IS_EXIST);
    }

    @Transactional
    @Override
    public UserDto updateLastVisit(String login) {
        log.info("UserService: update last visit by login: " + login);
        var user = userRepository.findByLogin(login).orElseThrow(EntityNotFoundException::new);
        user.setLastVisit(LocalDateTime.now());
        return userConverter.convert(userRepository.save(user));
    }

    //    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    @Override
    public void delete(String login, String token) {
        if (!tokenUtil.getLogin(token).equals(login)) {
            log.info("UserService: delete user by login: " + login);
//            petHelperClient.deleteNotificationsByUserLogin(login);
//            petHelperClient.deleteRecordsByUserLogin(login);
//            messageClient.deleteChatsByUserName(login);
            userRepository.deleteUserByLogin(login);
        } else {
            log.error("UserService: Delete exception");
            throw new DeleteException(DELETE_EXCEPTION);
        }
    }

    @Transactional
    @Override
    public void deleteAll(List<UserDto> list) {
        log.info("UserService: delete all users: " + list);
        for (UserDto userDto : list) {
            userRepository.deleteUserByLogin(userDto.getLogin());
        }
    }
}