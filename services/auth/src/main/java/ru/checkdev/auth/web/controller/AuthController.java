package ru.checkdev.auth.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author parsentev
 * @since 26.09.2016
 */
@RestController
public class AuthController {
    private final PersonService persons;
    private final String ping = "{}";

    @Autowired
    public AuthController(final PersonService persons) {
        this.persons = persons;
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/ping")
    public String ping() {
        return this.ping;
    }

    @GetMapping("/auth/activated/{key}")
    public Object activated(@PathVariable String key) {
        if (this.persons.activated(key)) {
            return new Object() {
                public boolean getSuccess() {
                    return true;
                }
            };
        } else {
            return new Object() {
                public String getError() {
                    return "Notify has already activated";
                }
            };
        }
    }

    @PostMapping("/registration")
    public Object registration(@RequestBody Profile profile) {
        Optional<Profile> result = this.persons.reg(profile);
        return result.<Object>map(prs -> new Object() {
            public Profile getPerson() {
                return prs;
            }
        }).orElseGet(() -> new Object() {
            public String getError() {
                return String.format("Пользователь с почтой %s уже существует.", profile.getEmail());
            }
        });
    }

    @PostMapping("/v2/registration")
    public ResponseEntity<Profile> v2registration(@RequestBody Profile profile) {
        Optional<Profile> result = this.persons.reg(profile);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @PostMapping("/forgot")
    public Object forgot(@RequestBody Profile profile) {
        Optional<Profile> result = this.persons.forgot(profile);
        if (result.isPresent()) {
            return new Object() {
                public String getOk() {
                    return "ok";
                }
            };
        } else {
            return new Object() {
                public String getError() {
                    return "E-mail не найден.";
                }
            };
        }
    }

    @GetMapping("/v2/forgot/{userId}")
    public ResponseEntity<Void> v2forgot(@PathVariable int userId) {
        try {
            Profile profile = this.persons.findById(userId);
            this.persons.forgot(profile).orElseThrow(NoSuchElementException::new);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/revoke")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {

    }
}
