package by.vitikova.discovery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий пакет данных, содержащих полезную информацию о токене.
 */
@Setter
@Getter
@AllArgsConstructor
public class TokenPayload {

    private String username;
    private String role;

    /**
     * Время валидности токена
     */
    private Long ext;
}
