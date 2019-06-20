package us.vanderlugt.sample.audit.role;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateAccessRole {
    @Size(min = 1, max = 100)
    private String name;
}
