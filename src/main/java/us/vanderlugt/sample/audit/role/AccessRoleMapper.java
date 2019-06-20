package us.vanderlugt.sample.audit.role;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
interface AccessRoleMapper {
    AccessRole create(NewAccessRole newRole);

    void apply(UpdateAccessRole update, @MappingTarget AccessRole role);
}
