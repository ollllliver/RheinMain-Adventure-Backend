package de.hsrm.mi.swt.rheinmainadventure.security;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {


    public Benutzer getBenutzer() {
        return benutzer;
    }

    private Benutzer benutzer;
    private List<GrantedAuthority> authorities;

    public MyUserDetails(Benutzer userName){
        this.benutzer = userName;
        this.authorities = Arrays.stream(userName.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return benutzer.getPasswort();
    }

    @Override
    public String getUsername() {
        return benutzer.getBenutzername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // TODO: huh?
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO: huh?
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO: huh?
    }

    @Override
    public boolean isEnabled() {
        return benutzer.getActive();
    }
}
