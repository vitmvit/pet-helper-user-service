package by.vitikova.discovery.repository;

import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User в MongoDB.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Найти пользователя по логину
     *
     * @param login логин пользователя
     * @return найденный пользователь или null, если пользователь не найден
     */
    Optional<User> findByLogin(String login);

    /**
     * Найти пользователя по логину и роли
     *
     * @param login логин пользователя
     * @param role роль пользователя
     * @return найденный пользователь или null, если пользователь не найден
     */
    Optional<User> findByLoginAndRole(String login, RoleName role);

    /**
     * Найти пользователей, у которых последнее посещение было до указанной даты.
     *
     * @param lastVisit дата последнего посещения
     * @return список найденных пользователей
     */
    List<User> findUsersByLastVisitBefore(LocalDateTime lastVisit);

    /**
     * Проверить, существует ли пользователь с указанным логином
     *
     * @param login логин пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsByLogin(String login);

    /**
     * Удалить пользователя по его логину.
     *
     * @param login логин пользователя, которого нужно удалить
     */
    void deleteUserByLogin(String login);
}
