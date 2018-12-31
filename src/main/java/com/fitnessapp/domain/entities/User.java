package com.fitnessapp.domain.entities;

import com.fitnessapp.common.constants.RoleConstants;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    private Integer id;

    private String username;

    private String password;

    private Set<Role> authorities;

    private Set<Card> cards;

    private User registeredBy;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    public User() {
        this.authorities = new HashSet<>();
        this.cards = new HashSet<>();
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(mappedBy = "client")
    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    @ManyToOne
    @JoinColumn(name = "registered_by", referencedColumnName = "id")
    public User getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(User registeredBy) {
        this.registeredBy = registeredBy;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Override
    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Transient
    public void addRole(Role role) {
        this.authorities.add(role);
    }

    @Transient
    public boolean isAdmin() {
        return this.authorities.stream().anyMatch(x -> x.getAuthority().equals(RoleConstants.ADMIN));
    }

    @Transient
    public boolean isInstructor() {
        return this.authorities.stream().anyMatch(x -> x.getAuthority().equals(RoleConstants.INSTRUCTOR));
    }

    @Transient
    public boolean isClient() {
        return this.authorities.stream().anyMatch(x -> x.getAuthority().equals(RoleConstants.CLIENT));
    }

    @Transient
    public String getRoleName() {
        return this.authorities.stream().findFirst().get().getAuthority();
    }
}
