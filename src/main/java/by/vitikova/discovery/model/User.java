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
public class User implements UserDetails {

    @Id
    private String id;

    private String login;

    private String password;

    private RoleName role;

    private LocalDateTime createDate;

    private LocalDateTime lastVisit;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRolesForUser()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private List<String> getRolesForUser() {
        return switch (this.role) {
            case ADMIN -> List.of(ADMIN_ROLE, USER_ROLE);
            case SUPPORT -> List.of(SUPPORT_ROLE, USER_ROLE);
            case EDITOR -> List.of(EDITOR_ROLE, USER_ROLE);
            default -> List.of(USER_ROLE);
        };
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}