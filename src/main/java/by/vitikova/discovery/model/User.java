package by.vitikova.discovery.model;

import by.vitikova.discovery.constant.RoleName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.*;

/**
 * /* Модель пользователя
 */
@Document(collection = "user")
@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;

    private String login;

    private String password;

    private RoleName role;

    private LocalDateTime createDate;

    private LocalDateTime lastVisit;

    /**
     * Конструктор с параметрами.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @param role     роль пользователя
     */
    public User(String login, String password, RoleName role, LocalDateTime createDate, LocalDateTime lastVisit) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.createDate = createDate;
        this.lastVisit = lastVisit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == RoleName.ADMIN) {
            return List.of(new SimpleGrantedAuthority(ADMIN_ROLE), new SimpleGrantedAuthority(USER_ROLE));
        }
        if (this.role == RoleName.SUPPORT) {
            return List.of(new SimpleGrantedAuthority(SUPPORT_ROLE), new SimpleGrantedAuthority(USER_ROLE));
        }
        if (this.role == RoleName.VET) {
            return List.of(new SimpleGrantedAuthority(VET_ROLE), new SimpleGrantedAuthority(USER_ROLE));
        }
        if (this.role == RoleName.EDITOR) {
            return List.of(new SimpleGrantedAuthority(EDITOR_ROLE), new SimpleGrantedAuthority(USER_ROLE));
        }
        return List.of(new SimpleGrantedAuthority(USER_ROLE));
    }

    @Override
    public String getUsername() {
        return login;
    }

    /**
     * /* {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * /* {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * /* {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}