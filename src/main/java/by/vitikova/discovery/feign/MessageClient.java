package by.vitikova.discovery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign-клиент для взаимодействия с микросервисом обмена сообщениями
 */
@FeignClient(contextId = "messageClient", value = "${feign.message-service.value}", url = "${feign.message-service.url}")
public interface MessageClient {

    /**
     * Удаление чатов по логину пользователя.
     *
     * @param login логин пользователя
     * @return объект ResponseEntity со статусом ответа
     */
    @DeleteMapping("/users/{login}")
    ResponseEntity<Void> deleteChatsByUserName(@PathVariable("login") String login);
}