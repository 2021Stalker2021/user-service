package io.gsc.service.impl;

import io.gsc.mapper.UserMapper;
import io.gsc.model.dto.UserDTO;
import io.gsc.model.entity.UserEntity;
import io.gsc.model.exception.DataExistException;
import io.gsc.model.exception.NotFoundException;
import io.gsc.model.request.NewUserRequest;
import io.gsc.model.request.UpdateUserRequest;
import io.gsc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setName("TestUser");
        testUser.setEmail("testuser@gmail.com");
        testUser.setAge(55);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setName("TestUser");
        testUserDTO.setEmail("testuser@gmail.com");
        testUserDTO.setAge(55);
    }

    @Test
    void getById_UserExists_ReturnsUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getById_UserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository).findById(999L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void createUser_CreatesUserSuccessfully() {
        NewUserRequest request = new NewUserRequest("New User", "razor@gmail.com", 55);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.createUser(request)).thenReturn(testUser);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.createUser(request);

        assertNotNull(result);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        NewUserRequest request = new NewUserRequest("New User", "jack@gmail.com", 55);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DataExistException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_UserExists_UpdatesSuccessfully() {
        UpdateUserRequest request = new UpdateUserRequest("Updated Name", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.updateUser(1L, request);

        assertNotNull(result);
        verify(userMapper).updateUser(testUser, request);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        UpdateUserRequest request = new UpdateUserRequest("Name", 20);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_UserExists_DeletesSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.delete(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).deleteById(anyLong());
    }
}