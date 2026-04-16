package io.gsc.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(max = 30)
    private String name;

    @NotNull(message = "Age is mandatory")
    @Positive(message = "Age must be positive")
    private Integer age;
}
