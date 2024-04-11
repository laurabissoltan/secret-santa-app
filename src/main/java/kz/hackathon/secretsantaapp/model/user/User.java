package kz.hackathon.secretsantaapp.model.user;

import jakarta.persistence.*;
import kz.hackathon.secretsantaapp.model.base.BaseEntityAudit;
import kz.hackathon.secretsantaapp.model.password.PasswordResetToken;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="_user")
public class User extends BaseEntityAudit implements UserDetails {

    @Column(name="login", unique = true, nullable = false)
    private String login;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", unique = true, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
  //  private Role role;
    @Builder.Default
    private Role role = Role.PARTICIPANT;

    @Column(name = "last_password_reset_date")
    private LocalDateTime lastPasswordResetDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture")
    private String profilePictureURL;

    /* @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordResetToken passwordResetToken;*/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
