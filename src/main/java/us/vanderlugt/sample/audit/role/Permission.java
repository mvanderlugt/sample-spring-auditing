package us.vanderlugt.sample.audit.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
public class Permission extends BaseEntity  {
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    @NotNull
    @Size(min = 1, max = 100)
    private String code;
}
