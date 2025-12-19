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

import static by.vitikova.discovery.constant.Constant.ADMIN_ROLE;
import static by.vitikova.discovery.constant.Constant.EDITOR_ROLE;
import static by.vitikova.discovery.constant.Constant.SUPPORT_ROLE;
import static by.vitikova.discovery.constant.Constant.USER_ROLE;
import static by.vitikova.discovery.constant.Constant.VET_ROLE;

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

    public User(String login, String password, RoleName role, LocalDateTime createDate, LocalDateTime lastVisit) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.createDate = createDate;
        this.lastVisit = lastVisit;
    }

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