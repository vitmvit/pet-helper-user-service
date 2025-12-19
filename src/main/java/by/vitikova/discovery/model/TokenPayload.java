package by.vitikova.discovery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TokenPayload {

    private String username;
    private String role;
    private Long ext;
}