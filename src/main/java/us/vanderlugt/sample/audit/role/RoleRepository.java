package us.vanderlugt.sample.audit.role;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Set;
import java.util.UUID;

public interface RoleRepository  extends PagingAndSortingRepository<AccessRole, UUID>, RevisionRepository<AccessRole, UUID, Integer> {
    Set<AccessRole> findAllById(Iterable<UUID> ids);
}
