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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.DELETE_EXCEPTION;
import static by.vitikova.discovery.constant.Constant.USERNAME_IS_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    @Override
    public Mono<Boolean> existsByLogin(String login) {
        log.info("UserService: exist by login: " + login);
        return userRepository.existsByLogin(login);
    }

    @Override
    public Mono<UserDto> findByLogin(String login) {
        log.info("UserService: find user by login: " + login);
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(() ->
                        {
                            log.warn("User not found with login: {}", login);
                            return new EntityNotFoundException("User not found with login: " + login);
                        }
                ))
                .flatMap(userConverter::convert)
                .doOnSuccess(userDto -> log.debug("Successfully found user by login: {}", login));
    }

    @Override
    public Mono<UserDto> findByLoginAndRole(String login, RoleName role) {
        log.info("UserService: find user by login: " + login + ", end role: " + role);
        return userRepository.findByLoginAndRole(login, role)
                .switchIfEmpty(Mono.error(() ->
                        {
                            log.warn("User not found with login and role: {}, {}", login, role);
                            return new EntityNotFoundException("User not found with login and role: " + login + ", " + role);
                        }
                ))
                .flatMap(userConverter::convert)
                .doOnSuccess(userDto -> log.debug("Successfully found user by login and role: {}, {}", login, role));
    }

    @Override
    public Flux<UserDto> findUsersByLastVisit(LocalDateTime lastVisit) {
        log.info("UserService: find users by last visit: " + lastVisit);
        return userRepository.findUsersByLastVisitBefore(lastVisit)
                .flatMap(userConverter::convert)
                .doOnComplete(() ->
                        log.debug("Search completed for last visit before: {}", lastVisit));
    }

    @Override
    public Mono<Page<UserDto>> findAll(Integer offset, Integer limit) {
        log.info("UserService: find all users");

        Pageable pageable = PageRequest.of(offset, limit);

        return userRepository.findAllBy(pageable)
                .flatMap(userConverter::convert)
                .collectList()
                .flatMap(userDtos -> {
                    if (userDtos.isEmpty()) {
                        return Mono.error(new EmptyListException("Empty list"));
                    }
                    return userRepository.count().map(total -> new PageImpl<>(userDtos, pageable, total));
                });
    }

    @Override
    public Mono<UserDto> create(UserCreateDto dto) {
        log.info("UserService: create user: {}", dto.getLogin());

        if (dto.getPassword() != null && dto.getPasswordConfirm() != null
                && !dto.getPassword().equals(dto.getPasswordConfirm())) {
            log.error("UserService: passwords do not match for user: {}", dto.getLogin());
            return Mono.error(new ValidationException("Passwords do not match"));
        }

        return userRepository.existsByLogin(dto.getLogin())
                .flatMap(exists -> {
                    if (exists) {
                        log.error("UserService: username already exists");
                        return Mono.error(new EntityIsExistsException(USERNAME_IS_EXIST));
                    }

                    return userConverter.convert(dto)
                            .flatMap(user -> Mono.fromCallable(() -> passwordEncoder.encode(user.getPassword())
                                    ).subscribeOn(Schedulers.boundedElastic())
                                    .doOnNext(user::setPassword)
                                    .thenReturn(user))
                            .flatMap(userRepository::save)
                            .flatMap(userConverter::convert)
                            .doOnSuccess(savedUser -> log.info("User created successfully: {}", dto.getLogin()));
                });
    }

    @Override
    @Transactional
    public Mono<UserDto> updatePassword(PasswordUpdateDto passwordUpdateDto) {
        return userRepository.findByLogin(passwordUpdateDto.getLogin())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("User not found: {}", passwordUpdateDto.getLogin());
                    return Mono.error(new EntityNotFoundException("User with lastname not found: " + passwordUpdateDto.getLogin()));
                }))
                .flatMap(user -> validateOldPassword(user, passwordUpdateDto))
                .flatMap(user -> validateNewPasswordsMatch(user, passwordUpdateDto))
                .flatMap(user -> validatePasswordConfirmation(passwordUpdateDto)
                        .then(Mono.just(user)))
                .flatMap(user -> encodeNewPassword(user, passwordUpdateDto))
                .flatMap(this::saveUserWithNewPassword)
                .flatMap(userConverter::convert)
                .doOnSuccess(dto -> log.info("Password updated successfully for user: {}", dto.getLogin()))
                .doOnError(throwable -> log.error("Failed to update password for user: {}",
                        passwordUpdateDto.getLogin(), throwable));
    }

    @Override
    @Transactional
    public Mono<UserDto> updateLastVisit(String login) {
        log.info("UserService: update last visit by login: {}", login);
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with login: " + login)))
                .flatMap(user -> {
                    user.setLastVisit(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .flatMap(userConverter::convert)
                .doOnSuccess(userDto ->
                        log.debug("Successfully updated last visit for user: {}", login));
    }

    @Override
    @Transactional
    public Mono<Void> delete(String login, String token) {
        return Mono.fromCallable(() -> {
                    String tokenLogin = tokenUtil.getLogin(token);
                    if (!tokenLogin.equals(login)) {
                        log.info("UserService: delete user by login: " + login);
                        return login;
                    } else {
                        log.error("Delete exception: user cannot delete themselves");
                        throw new DeleteException(DELETE_EXCEPTION);
                    }
                })
                .flatMap(validLogin ->
                        deleteUserDependencies(validLogin)
                                .then(userRepository.deleteUserByLogin(validLogin))
                )
                .doOnSuccess(unused -> log.info("Successfully deleted user: {}", login))
                .doOnError(DeleteException.class, error -> log.warn("Delete operation rejected: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll(List<String> list) {
        log.info("UserService: delete all users: " + list);
        return Flux.fromIterable(list)
                .flatMap(this::deleteUserDependencies)
                .then()
                .then(Mono.defer(() -> userRepository.deleteByLoginIn(list)))
                .doOnSuccess(v -> log.info("Successfully deleted {} users with dependencies", list.size()));
    }

    private Mono<Void> deleteUserDependencies(String login) {
        return Mono.when(
                //todo привести в порядок как будет сага
                // Пример реактивных вызовов (замените на реальные реактивные клиенты)
//                petHelperClient.deleteNotificationsByUserLogin(login),
//                petHelperClient.deleteRecordsByUserLogin(login),
//                messageClient.deleteChatsByUserName(login)
        ).doOnSubscribe(subscription ->
                log.debug("Deleting dependencies for user: {}", login));
    }

    private Mono<User> validateOldPassword(User user, PasswordUpdateDto dto) {
        if (dto.getOldPassword() == null || dto.getOldPassword().trim().isEmpty()) {
            return Mono.error(new PasswordUpdateException("Old password cannot be empty"));
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Mono.error(new PasswordUpdateException("User password not found"));
        }

        return Mono.fromCallable(() -> passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.error("Incorrect old password for user: {}", dto.getLogin());
                        return Mono.error(new PasswordUpdateException("Incorrect old password"));
                    }
                    log.info("Old password validated successfully for user: {}", dto.getLogin());
                    return Mono.just(user);
                });
    }

    private Mono<User> validateNewPasswordsMatch(User user, PasswordUpdateDto dto) {
        return Mono.fromCallable(() -> passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(isSameAsOld -> {
                    if (isSameAsOld) {
                        log.error("New password is identical to old password for user: {}", dto.getLogin());
                        return Mono.error(new PasswordUpdateException("New password must be different from the old one"));
                    }
                    log.info("New password is different from old password for user: {}", dto.getLogin());
                    return Mono.just(user);
                });
    }

    private Mono<Void> validatePasswordConfirmation(PasswordUpdateDto dto) {
        if (dto.getNewPassword() == null || dto.getConfirmPassword() == null) {
            return Mono.error(new ValidationException("Both password fields are required"));
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            log.warn("Password mismatch for user {}. New: {}, Confirm: {}",
                    dto.getLogin(),
                    dto.getNewPassword(),
                    dto.getConfirmPassword());
            return Mono.error(new ValidationException("Password and confirmation do not match"));
        }

        return Mono.empty();
    }

    private Mono<User> encodeNewPassword(User user, PasswordUpdateDto dto) {
        return Mono.fromCallable(() -> passwordEncoder.encode(dto.getNewPassword())
        ).doOnNext(encodedPassword -> {
            user.setPassword(encodedPassword);
            log.debug("Password encoded for user: {}", dto.getLogin());
        }).thenReturn(user);
    }

    private Mono<User> saveUserWithNewPassword(User user) {
        return userRepository.save(user)
                .doOnNext(savedUser -> log.debug("User saved with new password: {}", savedUser.getLogin()));
    }
}