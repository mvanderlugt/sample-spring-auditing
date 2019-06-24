package us.vanderlugt.sample.audit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.vanderlugt.sample.audit.common.NotFoundResponse;
import us.vanderlugt.sample.audit.role.SecurityRole;
import us.vanderlugt.sample.audit.role.SecurityRoleRepository;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/user/{userId}/role")
@RequiredArgsConstructor
public class UserRolesController {
    private final UserRepository userRepository;
    private final SecurityRoleRepository roleRepository;

    @GetMapping
    @ResponseStatus(OK)
    public Collection<SecurityRole> getUserRoles(@PathVariable("userId") UserAccount user) {
        return user.getRoles();
    }

    @PostMapping
    @ResponseStatus(OK)
    public Collection<SecurityRole> addRoleToUser(@PathVariable("userId") UserAccount user, @RequestBody UUID roleId) {
        SecurityRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundResponse(SecurityRole.class, roleId));
        if (user.add(role)) {
            log.debug("Adding role {} to user {}", role.getName(), user.getUsername());
            user = userRepository.save(user);
        } else {
            log.debug("User {} already has role {}", user.getUsername(), role.getName());
        }
        return user.getRoles();
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(OK)
    public Collection<SecurityRole> removeRoleFromUser(@PathVariable("userId") UserAccount user, @PathVariable("roleId") SecurityRole role) {
        if (user.getRoles().contains(role)) {
            user.remove(role);
            user = userRepository.save(user);
        } else {
            throw new NotFoundResponse("role", role.getId());
        }
        return user.getRoles();
    }

    @PutMapping
    @ResponseStatus(OK)
    public Collection<SecurityRole> setUserRoles(@PathVariable("userId") UserAccount user, @RequestBody Set<UUID> roleIds) {
        Set<SecurityRole> roles = roleRepository.findAllById(roleIds);
        //todo what does this do if some aren't found?
        log.debug("Updating user {} to have {} roles", user.getUsername(), roles.size());
        user.setRoles(roles);
        user = userRepository.save(user);
        return user.getRoles();
    }
}
