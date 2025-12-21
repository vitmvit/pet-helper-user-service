package by.vitikova.discovery.repository;

import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<Boolean> existsByLogin(String login);

    Mono<User> findByLogin(String login);

    Mono<User> findByLoginAndRole(String login, RoleName role);

    Flux<User> findUsersByLastVisitBefore(LocalDateTime lastVisit);

    Flux<User> findAllBy(Pageable pageable);

    Mono<Void> deleteUserByLogin(String login);

    Mono<Void> deleteByLoginIn(List<String> logins);
}