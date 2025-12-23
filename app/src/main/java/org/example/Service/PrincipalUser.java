package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.Entities.Role;
import org.example.Entities.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Data
public class PrincipalUser implements UserDetails {
    private String username;

    private String password;

    Collection<? extends GrantedAuthority> authorities;

    public PrincipalUser(UserInfo fromUsername) {
        this.username = fromUsername.getUserName();
        this.password = fromUsername.getPassword();
        List<GrantedAuthority> auth = new ArrayList<>();

        for(Role role: fromUsername.getRoles()){
            auth.add(new SimpleGrantedAuthority(role.getRoleName().toUpperCase()));
        }

        this.authorities = auth;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public String getUsername(){
        return username;
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
