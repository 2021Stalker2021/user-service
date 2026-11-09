package io.gsc.controller;

import io.gsc.model.constants.ApiLogMessage;
import io.gsc.model.dto.UserDTO;
import io.gsc.model.request.NewUserRequest;
import io.gsc.model.request.UpdateUserRequest;
import io.gsc.service.UserService;
import io.gsc.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "API для управления данными пользователей")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя и ссылки на возможные действия")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO response = userService.getById(id);

        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        response.add(linkTo(methodOn(UserController.class).updateUserById(id, null)).withRel("update"));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Validated @RequestBody NewUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO response = userService.createUser(request);

        response.add(linkTo(methodOn(UserController.class).getUserById(response.getId())).withSelfRel());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUserById(
            @PathVariable(name = "id") Long userId,
            @RequestBody @Valid UpdateUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO updatedUser = userService.updateUser(userId, request);
        updatedUser.add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());

        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long userId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
