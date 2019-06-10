package us.vanderlugt.sample.audit.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.UUID;

interface UserRepository extends PagingAndSortingRepository<UserAccount, UUID>, RevisionRepository<UserAccount, UUID, Integer> {
}
