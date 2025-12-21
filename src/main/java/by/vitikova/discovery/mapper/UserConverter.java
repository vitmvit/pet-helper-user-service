package by.vitikova.discovery.mapper;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Component
public class UserConverter {

    public Mono<UserDto> convert(User source) {
        return Mono.fromCallable(() -> {
            if (source == null) {
                return null;
            }

            UserDto dto = new UserDto();
            dto.setId(source.getId());
            dto.setLogin(source.getLogin());
            dto.setPassword(source.getPassword());
            dto.setRole(source.getRole());
            dto.setCreateDate(source.getCreateDate());
            dto.setLastVisit(source.getLastVisit());

            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> convert(UserCreateDto source) {
        return Mono.fromCallable(() -> {
            if (source == null) {
                return null;
            }

            User user = new User();
            user.setLogin(source.getLogin());
            user.setPassword(source.getPassword());
            user.setRole(source.getRole());
            user.setCreateDate(LocalDateTime.now());
            user.setLastVisit(LocalDateTime.now());

            return user;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}