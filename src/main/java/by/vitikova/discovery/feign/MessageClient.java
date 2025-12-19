package by.vitikova.discovery.feign;

//todo это нужно убрать когда будет нормальная сага
/**
 * Feign-клиент для взаимодействия с микросервисом обмена сообщениями
 */
//@FeignClient(contextId = "messageClient", value = "${feign.message-service.value}", url = "${feign.message-service.url}")
//public interface MessageClient {
//
//    /**
//     * Удаление чатов по логину пользователя.
//     *
//     * @param login логин пользователя
//     * @return объект ResponseEntity со статусом ответа
//     */
//    @DeleteMapping("/users/{login}")
//    ResponseEntity<Void> deleteChatsByUserName(@PathVariable("login") String login);
//}