package io.gsc.service;

import io.gsc.model.dto.UserDTO;
import io.gsc.model.request.NewUserRequest;
import io.gsc.model.request.UpdateUserRequest;
import jakarta.validation.constraints.NotNull;

public interface UserService {
    UserDTO getById(@NotNull Long userId);

    UserDTO createUser(@NotNull NewUserRequest newUserRequest);

    UserDTO updateUser(@NotNull Long userId, @NotNull UpdateUserRequest request);

    void delete(Long userId);
}
