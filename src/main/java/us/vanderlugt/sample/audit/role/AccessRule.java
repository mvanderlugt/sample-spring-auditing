package us.vanderlugt.sample.audit.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Audited
@JsonPropertyOrder({"id", "name", "description" ,"target", "rule"})
@EqualsAndHashCode(callSuper = false, exclude = "role")
public class AccessRule extends BaseEntity  {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private SecurityRole role;
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    @Size(min = 1, max = 500)
    private String description;
    @NotNull
    private String target;
    @NotNull
    private String rule;
}
