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
public class SecurityRoleController {
    private final SecurityRoleRepository repository;
    private final SecurityRoleMapper mapper;

    /**
     * Create a new user account.
     */
    @PostMapping
    @ResponseStatus(CREATED)
    public SecurityRole createSecurityRole(@Valid @RequestBody NewSecurityRole newRole) {
        return repository.save(mapper.create(newRole));
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public SecurityRole getSecurityRole(@PathVariable("id") UUID id, @PathVariable("id") SecurityRole role) {
        if (role != null) {
            return role;
        } else {
            throw new NotFoundResponse(UserAccount.class, id);
        }
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<SecurityRole> getSecurityRoles(@PageableDefault Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PutMapping("/{id}")
    public SecurityRole updateSecurityRole(@PathVariable("id") UUID id,
                                          @PathVariable("id") SecurityRole existing,
                                          @RequestBody UpdateSecurityRole update) {
        if (existing != null) {
            mapper.apply(update, existing);
            return repository.save(existing);
        } else {
            throw new NotFoundResponse(UserAccount.class, id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public SecurityRole deleteSecurityRole(@PathVariable("id") SecurityRole role) {
        repository.delete(role);
        return role;
    }

    @GetMapping("/{id}/audit")
    @ResponseStatus(OK)
    public Page<AuditRecord<SecurityRole>> getSecurityRoleAuditHistory(@PathVariable("id") SecurityRole role, @PageableDefault Pageable pageable) {
        return repository.findRevisions(role.getId(), pageable)
                .map(rev -> new AuditRecord<>(rev.getRevisionNumber(), rev.getRevisionInstant(), rev.getEntity()));
    }
}
