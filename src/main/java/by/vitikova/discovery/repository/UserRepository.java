package by.vitikova.discovery.repository;

import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndRole(String login, RoleName role);

    List<User> findUsersByLastVisitBefore(LocalDateTime lastVisit);

    boolean existsByLogin(String login);

    void deleteUserByLogin(String login);
}