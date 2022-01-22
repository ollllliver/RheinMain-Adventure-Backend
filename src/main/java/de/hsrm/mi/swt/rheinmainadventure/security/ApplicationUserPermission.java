package de.hsrm.mi.swt.rheinmainadventure.security;

public enum ApplicationUserPermission {
    BENUTZER_READ("student:read"),
    BENUTZER_WRITE("student:write"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:read");

    private final String permission;

    ApplicationUserPermission(String permission){
        this.permission = permission;
    }

    public String getPermission(){
        return  permission;
    }
}
