package de.hsrm.mi.swt.rheinmainadventure.benutzer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("admin/api/benutzer")
public class Benutzer2AdminController {

    private static final List<Benutzer2> BENUTZER = Arrays.asList(
            new Benutzer2(1,"Peter Enis"),
            new Benutzer2(2, "Klaus Maus"),
            new Benutzer2(3, "Angelika Nuss")
    );

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_BENUTZER')")
    public List<Benutzer2> getAllBenutzer2(){
        return BENUTZER;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('benutzer:write')")
    public void registerNewBenutzer(@RequestBody Benutzer2 benutzer2){
        System.out.println(benutzer2);
    }

    @DeleteMapping(path = "{benutzerId}")
    @PreAuthorize("hasAuthority('benutzer:write')")
    public void deleteBenutzer(@PathVariable("benutzerId") Integer benutzerId){
        System.out.println(benutzerId);
    }

    @PutMapping(path = "{benutzerId}")
    @PreAuthorize("hasAuthority('benutzer:write')")
    public void updateBenutzer(@PathVariable("benutzerId") Integer benutzerId, Benutzer2 benutzer2){
        System.out.println(String.format("%s %s", benutzerId, benutzer2));
    }
}
