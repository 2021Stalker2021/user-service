package io.gsc.mapper;

import io.gsc.model.dto.UserDTO;
import io.gsc.model.entity.UserEntity;
import io.gsc.model.request.NewUserRequest;
import io.gsc.model.request.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserEntity createUser(NewUserRequest newUserRequest);

    UserDTO toUserDTO(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateUser(@MappingTarget UserEntity user, UpdateUserRequest request);
}
