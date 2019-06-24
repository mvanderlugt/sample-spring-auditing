package us.vanderlugt.sample.audit.role;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
interface AccessRuleMapper {
    AccessRule create(NewAccessRule newRule);

    void apply(UpdateAccessRule update, @MappingTarget AccessRule role);
}
