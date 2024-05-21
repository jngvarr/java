package dao.entities.people;

import dao.entities.Authority;
import dao.entities.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class User extends SomeOne implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_name")
    private final String userName;
    @Setter
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private final String email;
    @OneToMany
    @JoinTable(name = "user_to_athorities",
            joinColumns = @JoinColumn(name = "athority_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<Authority> authorities;
    @OneToMany
    @JoinTable(name = "user_to_token",
            joinColumns = @JoinColumn(name = "token_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<RefreshToken> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public void setAuthorities(Authority authority) {
//        authorities = List<Authority> getAuthorities();
        authorities.add(authority);
    }

    @Override
    public String getUsername() {
        return null;
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
