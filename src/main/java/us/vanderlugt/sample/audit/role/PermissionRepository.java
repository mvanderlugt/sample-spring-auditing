package us.vanderlugt.sample.audit.role;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.UUID;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, UUID>, RevisionRepository<Permission, UUID, Integer> {
}
