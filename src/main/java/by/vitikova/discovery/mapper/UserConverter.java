package by.vitikova.discovery.mapper;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.model.User;
import by.vitikova.discovery.update.UserUpdateDateDto;
import by.vitikova.discovery.update.UserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Конвертер для преобразования объектов, связанных с пользователями.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    /**
     * Преобразование объекта PatchException в объект UserDto
     *
     * @param source исходный комментарий типа Comment
     * @return преобразованный комментарий типа CommentDto
     */
    UserDto convert(User source);

    /**
     * Преобразование объекта UserDto в объект User.
     *
     * @param source исходный объект UserDto
     * @return преобразованный объект User
     */
    User convert(UserDto source);

    /**
     * Преобразование объекта UserCreateDto в объект PatchException
     *
     * @param source исходный DTO для создания комментария типа CommentCreateDto
     * @return преобразованный комментарий типа Comment
     */
    User convert(UserCreateDto source);

    /**
     * Преобразование объекта UserUpdateDto в объект PatchException
     *
     * @param source исходный DTO для обновления комментария типа CommentUpdateDto
     * @return преобразованная модель комментария типа Comment
     */
    User convert(UserUpdateDto source);

    /**
     * Преобразование объекта UserUpdateDateDto в объект User для обновления даты последнего посещения.
     *
     * @param source исходный объект UserUpdateDateDto
     * @return преобразованный объект User
     */
    User updateLastVisit(UserUpdateDateDto source);

    /**
     * Объединение значений из объекта UserUpdateDto в существующий комментарий
     *
     * @param user существующий комментарий типа PatchException, в который нужно объединить значения
     * @param dto  DTO для обновления комментария типа CommentUpdateDto
     * @return обновленный комментарий типа Comment
     */
    User merge(@MappingTarget User user, UserUpdateDto dto);

    /**
     * Обновление полей объекта User на основе данных из UserUpdateDateDto.
     *
     * @param user объект User, который нужно обновить
     * @param dto  объект UserUpdateDateDto с обновленными данными даты посещения
     * @return обновленный объект User
     */
    User mergeModel(@MappingTarget User user, UserUpdateDateDto dto);
}
