package by.vitikova.discovery.service.impl;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.exception.*;
import by.vitikova.discovery.mapper.UserConverter;
import by.vitikova.discovery.model.TokenPayload;
import by.vitikova.discovery.model.User;
import by.vitikova.discovery.repository.UserRepository;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import by.vitikova.discovery.update.UserUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final UserConverter userConverter;
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
        return userRepository.existsByLogin(login);
    }

    /**
     * Поиск пользователя по логину.
     *
     * @param login логин пользователя
     * @return объект UserDto, если пользователь найден
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Override
    public UserDto findByLogin(String login) {
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
    @Override
    public UserDto create(UserCreateDto dto) {
        if (Boolean.FALSE.equals(userRepository.existsByLogin(dto.getLogin()))) {
            var user = userConverter.convert(dto);
            user.setCreateDate(LocalDateTime.now());
            user.setLastVisit(LocalDateTime.now());
            return userConverter.convert(userRepository.save(user));
        }
        throw new InvalidJwtException(USERNAME_IS_EXIST);
    }

//    /**
//     * Обновление данных пользователя.
//     *
//     * @param dto данные пользователя для обновления
//     * @return обновленный пользователь в виде объекта UserDto
//     * @throws EntityNotFoundException если пользователь не найден
//     */
//    @Override
//    public UserDto update(UserUpdateDto dto) {
//        var user = userRepository.findByLogin(dto.getLogin()).orElseThrow(EntityNotFoundException::new);
//        userConverter.merge(user, dto);
//        return userConverter.convert(userRepository.save(user));
//    }

    /**
     * Обновляет пароль пользователя.
     *
     * @param passwordUpdateDto данные для обновления пароля
     * @return объект типа UserDto, содержащий информацию о пользователе с обновленным паролем
     * @throws EntityNotFoundException   если пользователь с заданным логином не найден
     * @throws PasswordUpdateException   если старый пароль неверен или новый пароль не совпадает с подтверждением
     */
    @Override
    public UserDto updatePassword(PasswordUpdateDto passwordUpdateDto) {
        var user = userRepository.findByLogin(passwordUpdateDto.getLogin()).orElseThrow(EntityNotFoundException::new);
        if (passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            if (passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
                String encodePassword = passwordEncoder.encode(passwordUpdateDto.getNewPassword());
                user.setPassword(encodePassword);
                return userConverter.convert(userRepository.save(user));
            } else  {
                throw new PasswordUpdateException("Password must be identical");
            }
        } else {
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
    @Override
    public UserDto updateLastVisit(String login) {
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
    @Override
    public void delete(String login, String token) {
        if (!getLogin(token).equals(login)) {
            userRepository.deleteUserByLogin(login);
        } else {
            throw new DeleteException(DELETE_EXCEPTION);
        }
    }

    /**
     * Удаление списка пользователей.
     *
     * @param list список пользователей для удаления
     */
    @Override
    public void deleteAll(List<UserDto> list) {
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