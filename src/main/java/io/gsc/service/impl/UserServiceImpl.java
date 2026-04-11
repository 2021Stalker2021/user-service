package io.gsc.service.impl;

import io.gsc.mapper.UserMapper;
import io.gsc.model.constants.ApiErrorMessage;
import io.gsc.model.dto.UserDTO;
import io.gsc.model.entity.UserEntity;
import io.gsc.model.exception.DataExistException;
import io.gsc.model.exception.NotFoundException;
import io.gsc.model.request.NewUserRequest;
import io.gsc.model.request.UpdateUserRequest;
import io.gsc.repository.UserRepository;
import io.gsc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        return userMapper.toUserDTO(userEntity);
    }

    @Override
    public UserDTO createUser(NewUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

        UserEntity user = userMapper.createUser(request);
        UserEntity savedUser = userRepository.save(user);

        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        userMapper.updateUser(userEntity, request);
        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public void delete(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        userRepository.deleteById(userId);
    }
}
