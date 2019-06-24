package us.vanderlugt.sample.audit.role;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateAccessRule {
    @Size(min = 1, max = 100)
    private String name;
    @Size(min = 1, max = 500)
    private String description;
    private String target;
    private String rule;
}
