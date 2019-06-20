package us.vanderlugt.sample.audit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.vanderlugt.sample.audit.common.AuditRecord;
import us.vanderlugt.sample.audit.common.NotFoundResponse;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository repository;
    private final UserAccountMapper mapper;

    /**
     * Create a new user account.
     */
    @PostMapping
    @ResponseStatus(CREATED)
    public UserAccount createUserAccount(@Valid @RequestBody NewUserAccount newAccount) {
        return repository.save(mapper.create(newAccount));
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public UserAccount getUserAccount(@PathVariable("id") UUID id, @PathVariable("id") UserAccount userAccount) {
        if (userAccount != null) {
            return userAccount;
        } else {
            throw new NotFoundResponse(UserAccount.class, id);
        }
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<UserAccount> getUserAccounts(@PageableDefault Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PutMapping("/{id}")
    public UserAccount updateUserAccount(@PathVariable("id") UUID id,
                                         @PathVariable("id") UserAccount existing,
                                         @RequestBody UpdateUserAccount update) {
        if (existing != null) {
            mapper.apply(update, existing);
            return repository.save(existing);
        } else {
            throw new NotFoundResponse(UserAccount.class, id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public UserAccount deleteUserAccount(@PathVariable("id") UserAccount userAccount) {
        repository.delete(userAccount);
        return userAccount;
    }

    @GetMapping("/{id}/audit")
    @ResponseStatus(OK)
    public Page<AuditRecord<UserAccount>> getUserAuditHistory(@PathVariable("id") UserAccount userAccount, @PageableDefault Pageable pageable) {
        return repository.findRevisions(userAccount.getId(), pageable)
                .map(rev -> new AuditRecord<>(rev.getRevisionNumber(), rev.getRevisionInstant(), rev.getEntity()));
    }


}
