package io.gsc.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 30)
    private String name;
    @NotBlank(message = "Email cannot be empty")
    @Size(max = 50)
    private String email;
    @NotNull(message = "Age cannot be null")
    @Positive(message = "Age must be positive")
    private Integer age;
}
