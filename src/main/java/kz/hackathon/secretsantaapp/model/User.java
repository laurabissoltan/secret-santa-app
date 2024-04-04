package kz.hackathon.secretsantaapp.model;

import jakarta.persistence.*;
import kz.hackathon.secretsantaapp.model.base.BaseEntityAudit;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", unique = true, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
  //  private Role role;
    @Builder.Default
    private Role role = Role.ROLE_PARTICIPANT;

/*    @Column(name = "time_zone")
    private String timeZone; // Field for storing the user's time zone

    @Column(name = "avatar")
    private String avatar;
*/
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "active")
    private boolean active;



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
