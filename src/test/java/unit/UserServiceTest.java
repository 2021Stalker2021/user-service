package unit;

import io.gsc.dao.UserDao;
import io.gsc.entity.UserEntity;
import io.gsc.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_ShouldCallSave_WhenEmailIsValid() {
        UserEntity user = UserEntity.builder()
                .email("test@mail.com")
                .name("Ivan")
                .build();

        userService.registerUser(user);

        verify(userDao).save(user);
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
        UserEntity user = UserEntity.builder()
                .email("invalid-email")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verifyNoMoreInteractions(userDao);
    }
}