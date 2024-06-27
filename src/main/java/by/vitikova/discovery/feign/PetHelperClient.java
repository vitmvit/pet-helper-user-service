package by.vitikova.discovery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "petHelperClient", value = "${feign.pet-helper-service.value}", url = "${feign.pet-helper-service.url}")
public interface PetHelperClient {

    @DeleteMapping("/notifications/delete/user/{login}")
    ResponseEntity<Void> deleteNotificationsByUserLogin(@PathVariable("login") String login);

    @DeleteMapping("/records/delete/user/{login}")
    ResponseEntity<Void> deleteRecordsByUserLogin(@PathVariable("login") String login);
}
