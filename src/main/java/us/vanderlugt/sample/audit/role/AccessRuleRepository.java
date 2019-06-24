package us.vanderlugt.sample.audit.role;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.UUID;

public interface AccessRuleRepository extends PagingAndSortingRepository<AccessRule, UUID>, RevisionRepository<AccessRule, UUID, Integer> {
}
