package us.vanderlugt.sample.audit.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

interface UserRepository extends PagingAndSortingRepository<UserAccount, UUID> {
}
