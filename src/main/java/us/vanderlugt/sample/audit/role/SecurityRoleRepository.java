package us.vanderlugt.sample.audit.role;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Set;
import java.util.UUID;

public interface SecurityRoleRepository extends PagingAndSortingRepository<SecurityRole, UUID>, RevisionRepository<SecurityRole, UUID, Integer> {
    Set<SecurityRole> findAllById(Iterable<UUID> ids);
}
