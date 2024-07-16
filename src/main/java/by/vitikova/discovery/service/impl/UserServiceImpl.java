package by.vitikova.discovery.service.impl;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.exception.*;
import by.vitikova.discovery.feign.MessageClient;
import by.vitikova.discovery.feign.PetHelperClient;
import by.vitikova.discovery.mapper.UserConverter;
import by.vitikova.discovery.model.TokenPayload;
import by.vitikova.discovery.model.User;
import by.vitikova.discovery.repository.UserRepository;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.*;

/**
 * Реализация интерфейса UserService.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PetHelperClient petHelperClient;
    private final UserConverter userConverter;
    private final MessageClient messageClient;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Проверяет наличие пользователя с указанным логином.
     *
     * @param login логин пользователя
     * @return true, если пользователь существует, false в противном случае
     */
    @Override
    public boolean existsByLogin(String login) {
        logger.info("UserService: exist by login: " + login);
        return userRepository.existsByLogin(login);
    }

    /**
     * Поиск пользователя по логину.
     *
     * @param login логин пользователя
     * @return объект UserDto, если пользователь найден
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Cacheable(value = "user", key = "#login")
    @Override
    public UserDto findByLogin(String login) {
        logger.info("UserService: find user by login: " + login);
        return userConverter.convert(userRepository.findByLogin(login).orElseThrow(EntityNotFoundException::new));
    }

    /**
     * Находит пользователя по заданному логину и роли.
     *
     * @param login логин пользователя
     * @param role  роль пользователя
     * @return объект типа UserDto, содержащий информацию о найденном пользователе
     * @throws EntityNotFoundException Если пользователь с заданным логином и ролью не найден
     */
    @Override
    public UserDto findByLoginAndRole(String login, RoleName role) {
        logger.info("UserService: find user by login: " + login + ", end role: " + role);
        return userConverter.convert(userRepository.findByLoginAndRole(login, role).orElseThrow(EntityNotFoundException::new));
    }

    /**
     * Поиск пользователей, последний визит которых был до указанной даты.
     *
     * @param lastVisit дата последнего визита
     * @return список объектов UserDto, соответствующих условию поиска
     */
    @Override
    public List<UserDto> findUsersByLastVisit(LocalDateTime lastVisit) {
        logger.info("UserService: find users by last visit: " + lastVisit);
        return userRepository.findUsersByLastVisitBefore(lastVisit).stream().map(userConverter::convert).toList();
    }

    /**
     * Получение страницы пользователей.
     *
     * @param offset смещение страницы
     * @param limit  количество записей на странице
     * @return страница объектов UserDto
     * @throws EmptyListException если страница пустая
     */
    @Override
    public Page<UserDto> findAll(Integer offset, Integer limit) {
        logger.info("UserService: find all users");
        Page<User> commentPage = userRepository.findAll(PageRequest.of(offset, limit));
        commentPage.stream().findAny().orElseThrow(EmptyListException::new);
        return commentPage.map(userConverter::convert);
    }

    /**
     * Создание нового пользователя.
     *
     * @param dto данные пользователя для создания
     * @return созданный пользователь в виде объекта UserDto
     * @throws InvalidJwtException если пользователь с таким логином уже существует
     */
    @CacheEvict(value = "users", key = "#dto.login")
    @Transactional
    @Override
    public UserDto create(UserCreateDto dto) {
        if (Boolean.FALSE.equals(userRepository.existsByLogin(dto.getLogin()))) {
            logger.info("UserService: create user: " + dto.getLogin());
            var user = userConverter.convert(dto);
            user.setCreateDate(LocalDateTime.now());
            user.setLastVisit(LocalDateTime.now());
            return userConverter.convert(userRepository.save(user));
        }
        logger.error("UserService: username is exist");
        throw new InvalidJwtException(USERNAME_IS_EXIST);
    }

    /**
     * Обновляет пароль пользователя.
     *
     * @param passwordUpdateDto данные для обновления пароля
     * @return объект типа UserDto, содержащий информацию о пользователе с обновленным паролем
     * @throws EntityNotFoundException   если пользователь с заданным логином не найден
     * @throws PasswordUpdateException   если старый пароль неверен или новый пароль не совпадает с подтверждением
     */
    @CacheEvict(value = "users", key = "#passwordUpdateDto.login")
    @Transactional
    @Override
    public UserDto updatePassword(PasswordUpdateDto passwordUpdateDto) {
        var user = userRepository.findByLogin(passwordUpdateDto.getLogin()).orElseThrow(EntityNotFoundException::new);
        if (passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            if (passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
                logger.info("UserService: update user: " + passwordUpdateDto.getLogin());
                String encodePassword = passwordEncoder.encode(passwordUpdateDto.getNewPassword());
                user.setPassword(encodePassword);
                return userConverter.convert(userRepository.save(user));
            } else  {
                logger.error("UserService: Password update exception");
                throw new PasswordUpdateException("Password must be identical");
            }
        } else {
            logger.error("UserService: Incorrect old password");
            throw new PasswordUpdateException("Incorrect old password");
        }
    }

    /**
     * Обновление даты последнего визита пользователя.
     *
     * @param login логин пользователя
     * @return обновленный пользователь в виде объекта UserDto
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional
    @Override
    public UserDto updateLastVisit(String login) {
        logger.info("UserService: update last visit by login: " + login);
        var user = userRepository.findByLogin(login).orElseThrow(EntityNotFoundException::new);
        user.setLastVisit(LocalDateTime.now());
        return userConverter.convert(userRepository.save(user));
    }

    /**
     * Удаление пользователя.
     *
     * @param login логин пользователя
     * @param token токен для авторизации
     * @throws DeleteException         если возникла ошибка при удалении пользователя
     * @throws InvalidJwtException     если токен недействителен
     * @throws EntityNotFoundException если пользователь не найден
     */
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    @Override
    public void delete(String login, String token) {
        if (!getLogin(token).equals(login)) {
            logger.info("UserService: delete user by login: " + login);
            petHelperClient.deleteNotificationsByUserLogin(login);
            petHelperClient.deleteRecordsByUserLogin(login);
            messageClient.deleteChatsByUserName(login);
            userRepository.deleteUserByLogin(login);
        } else {
            logger.error("UserService: Delete exception");
            throw new DeleteException(DELETE_EXCEPTION);
        }
    }

    /**
     * Удаление списка пользователей.
     *
     * @param list список пользователей для удаления
     */
    @Transactional
    @Override
    public void deleteAll(List<UserDto> list) {
        logger.info("UserService: delete all users: " + list);
        for (UserDto userDto : list) {
            userRepository.deleteUserByLogin(userDto.getLogin());
        }
    }

    /**
     * Получение логина из токена авторизации.
     *
     * @param token токен авторизации
     * @return логин пользователя, указанный в токене
     * @throws RuntimeException если произошла ошибка при обработке токена
     */
    private String getLogin(String token) {
        try {
            String[] chinks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chinks[1]));
            TokenPayload tokenPayload = objectMapper.readValue(payload, TokenPayload.class);
            return tokenPayload.getUsername();
        } catch (JsonProcessingException e) {
            throw new InvalidJwtException(INVALID_TOKEN);
        }
    }
}