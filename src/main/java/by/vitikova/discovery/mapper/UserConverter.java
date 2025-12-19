package by.vitikova.discovery.mapper;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    UserDto convert(User source);

    User convert(UserCreateDto source);
}