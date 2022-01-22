package de.hsrm.mi.swt.rheinmainadventure.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static de.hsrm.mi.swt.rheinmainadventure.security.ApplicationUserPermission.BENUTZER_READ;
import static de.hsrm.mi.swt.rheinmainadventure.security.ApplicationUserPermission.BENUTZER_WRITE;
import static de.hsrm.mi.swt.rheinmainadventure.security.ApplicationUserPermission.ADMIN_READ;
import static de.hsrm.mi.swt.rheinmainadventure.security.ApplicationUserPermission.ADMIN_WRITE;

public enum ApplicationUserRole {
    BENUTZER(Sets.newHashSet(BENUTZER_READ, BENUTZER_WRITE)),
    ADMIN(Sets.newHashSet(ADMIN_READ,ADMIN_WRITE, BENUTZER_READ, BENUTZER_WRITE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions){
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());

        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
