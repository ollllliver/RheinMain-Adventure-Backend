package de.hsrm.mi.swt.rheinmainadventure.jwt;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes(names = {"loggedinBenutzername"})
@RestController
public class JwtLoginController2 {
}
