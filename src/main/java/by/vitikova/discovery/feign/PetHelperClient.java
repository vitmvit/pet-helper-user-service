package by.vitikova.discovery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign-клиент для взаимодействия с микросервисом pet-helper
 */
@FeignClient(contextId = "petHelperClient", value = "${feign.pet-helper-service.value}", url = "${feign.pet-helper-service.url}")
public interface PetHelperClient {

    /**
     * Удаление уведомлений по логину пользователя.
     *
     * @param login логин пользователя
     * @return объект ResponseEntity со статусом ответа
     */
    @DeleteMapping("/notifications/delete/user/{login}")
    ResponseEntity<Void> deleteNotificationsByUserLogin(@PathVariable("login") String login);

    /**
     * Удаление записей по логину пользователя.
     *
     * @param login логин пользователя
     * @return объект ResponseEntity со статусом ответа
     */
    @DeleteMapping("/records/delete/user/{login}")
    ResponseEntity<Void> deleteRecordsByUserLogin(@PathVariable("login") String login);
}