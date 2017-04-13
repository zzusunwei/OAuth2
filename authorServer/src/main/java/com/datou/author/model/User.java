package com.datou.author.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Document
public class User implements UserDetails, CredentialsContainer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    private String username;
    private String password;
    private UUID userUUID;
    private Set<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public User() {
    }

    @PersistenceConstructor
    public User(final String password,
                final String username,
                final UUID userUUID,
                final Set<GrantedAuthority> authorities,
                final boolean accountNonExpired,
                final boolean accountNonLocked,
                final boolean credentialsNonExpired,
                final boolean enabled) {
        this.password = password;
        this.username = username;
        this.userUUID = userUUID;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }

    public UUID getUserUUID() {
        return userUUID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(password, username, userUUID, authorities, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.password, other.password) && Objects.equals(this.username, other.username) && Objects.equals(this.userUUID, other.userUUID) && Objects.equals(this.authorities, other.authorities) && Objects.equals(this.accountNonExpired, other.accountNonExpired) && Objects.equals(this.accountNonLocked, other.accountNonLocked) && Objects.equals(this.credentialsNonExpired, other.credentialsNonExpired) && Objects.equals(this.enabled, other.enabled);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userUUID=" + userUUID +
                ", authorities=" + authorities +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }
}
