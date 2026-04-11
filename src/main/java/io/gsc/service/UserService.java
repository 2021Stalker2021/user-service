package io.gsc.service;

import io.gsc.dao.UserDao;
import io.gsc.entity.UserEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public void registerUser(UserEntity user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        userDao.save(user);
    }

    public Optional<UserEntity> getUser(Long id) {
        return userDao.findById(id);
    }
}
