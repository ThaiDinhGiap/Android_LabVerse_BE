package com.mss.prm_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    int userId;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "password")
    String password;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "full_name", columnDefinition = "nvarchar(255)")
    String fullName;

    @Column(name = "avatar_url")
    String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @ManyToMany(mappedBy = "users")
    Set<Collection> collections = new HashSet<>();


    @OneToMany(mappedBy = "user")
    List<Paper> paper;

    @Column(name = "enabled")
    boolean enabled;

    @Column(name = "google_sub", unique = true)
    private String googleSub;

    @Column(name = "email_verify_at")
    private LocalDateTime emailVerifyAt;

    @Column(name = "google_link_at")
    private LocalDateTime googleLinkAt;

    @Column(name = "push_notifications", columnDefinition = "boolean default true")
    private boolean pushNotifications;

    @Column(name = "email_notifications", columnDefinition = "boolean default true")
    private boolean emailNotifications;

    @Override
    public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase()));
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
        return this.enabled;
    }
}
