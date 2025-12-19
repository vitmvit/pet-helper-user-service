package by.vitikova.discovery.util;

import by.vitikova.discovery.exception.InvalidJwtException;
import by.vitikova.discovery.model.TokenPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static by.vitikova.discovery.constant.Constant.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
public class TokenUtil {

    private final ObjectMapper objectMapper;

    public String getLogin(String token) {
        try {
            String[] chinks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chinks[1]));
            TokenPayload tokenPayload = objectMapper.readValue(payload, TokenPayload.class);
            return tokenPayload.getUsername();
        } catch (JsonProcessingException e) {
            throw new InvalidJwtException(INVALID_TOKEN);
        }
    }
}
