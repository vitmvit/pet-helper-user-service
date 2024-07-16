package by.vitikova.discovery.mapper;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.model.User;
import org.mapstruct.Mapper;
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
     * Преобразование объекта UserCreateDto в объект PatchException
     *
     * @param source исходный DTO для создания комментария типа CommentCreateDto
     * @return преобразованный комментарий типа Comment
     */
    User convert(UserCreateDto source);
}