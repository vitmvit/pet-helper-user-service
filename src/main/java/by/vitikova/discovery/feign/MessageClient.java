package by.vitikova.discovery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "messageClient", value = "${feign.message-service.value}", url = "${feign.message-service.url}")
public interface MessageClient {

    @DeleteMapping("/users/{login}")
    ResponseEntity<Void> deleteChatsByUserName(@PathVariable("login") String login);
}
