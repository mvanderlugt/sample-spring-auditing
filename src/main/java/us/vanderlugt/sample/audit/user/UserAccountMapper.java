package us.vanderlugt.sample.audit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import us.vanderlugt.sample.audit.common.CustomMapping;
import us.vanderlugt.sample.audit.common.Password;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", uses = CustomMapping.class, unmappedTargetPolicy = IGNORE)
interface UserAccountMapper {
    @Mapping(source = "password", target = "password", qualifiedBy = Password.class)
    UserAccount create(NewUserAccount newAccount);

    @Mapping(source = "password", target = "password", qualifiedBy = Password.class)
    void apply(UpdateUserAccount update, @MappingTarget UserAccount userAccount);
}
