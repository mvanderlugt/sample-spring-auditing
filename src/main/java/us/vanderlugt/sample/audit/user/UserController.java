package us.vanderlugt.sample.audit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserRepository repository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(CREATED)
    public UserAccount createUserAccount(@Valid @RequestBody UserAccount userAccount) {
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        return repository.save(userAccount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public UserAccount deleteUserAccount(@PathVariable("id") UserAccount userAccount) {
        repository.delete(userAccount);
        return userAccount;
    }
}
