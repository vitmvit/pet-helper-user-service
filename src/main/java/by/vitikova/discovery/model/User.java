package by.vitikova.discovery.model;

import by.vitikova.discovery.constant.RoleName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static by.vitikova.discovery.constant.Constant.ADMIN_ROLE;
import static by.vitikova.discovery.constant.Constant.EDITOR_ROLE;
import static by.vitikova.discovery.constant.Constant.SUPPORT_ROLE;
import static by.vitikova.discovery.constant.Constant.USER_ROLE;

@Document(collection = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private String id;

    private String login;

    private String password;

    private RoleName role;

    private LocalDateTime createDate;

    private LocalDateTime lastVisit;
}