package us.vanderlugt.sample.audit.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.vanderlugt.sample.audit.common.AuditRecord;
import us.vanderlugt.sample.audit.common.NotFoundResponse;
import us.vanderlugt.sample.audit.user.UserAccount;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class AccessRoleController {
    private final RoleRepository repository;
    private final AccessRoleMapper mapper;

    /**
     * Create a new user account.
     */
    @PostMapping
    @ResponseStatus(CREATED)
    public AccessRole createAccessRole(@Valid @RequestBody NewAccessRole newRole) {
        return repository.save(mapper.create(newRole));
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public AccessRole getAccessRole(@PathVariable("id") UUID id, @PathVariable("id") AccessRole role) {
        if (role != null) {
            return role;
        } else {
            throw new NotFoundResponse(UserAccount.class, id);
        }
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<AccessRole> getAccessRoles(@PageableDefault Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PutMapping("/{id}")
    public AccessRole updateUserAccount(@PathVariable("id") UUID id,
                                        @PathVariable("id") AccessRole existing,
                                        @RequestBody UpdateAccessRole update) {
        if (existing != null) {
            mapper.apply(update, existing);
            return repository.save(existing);
        } else {
            throw new NotFoundResponse(UserAccount.class, id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public AccessRole deleteUserAccount(@PathVariable("id") AccessRole role) {
        repository.delete(role);
        return role;
    }

    @GetMapping("/{id}/audit")
    @ResponseStatus(OK)
    public Page<AuditRecord<AccessRole>> getUserAuditHistory(@PathVariable("id") AccessRole role, @PageableDefault Pageable pageable) {
        return repository.findRevisions(role.getId(), pageable)
                .map(rev -> new AuditRecord<>(rev.getRevisionNumber(), rev.getRevisionInstant(), rev.getEntity()));
    }
}
