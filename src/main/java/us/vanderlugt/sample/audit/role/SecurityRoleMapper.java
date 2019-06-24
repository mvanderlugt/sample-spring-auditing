package us.vanderlugt.sample.audit.role;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
interface SecurityRoleMapper {
    SecurityRole create(NewSecurityRole newRole);

    void apply(UpdateSecurityRole update, @MappingTarget SecurityRole role);
}
