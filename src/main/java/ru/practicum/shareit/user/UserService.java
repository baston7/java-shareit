package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUser(long userId) {
        return userDao.get(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User createUser(User user) {
        validatorCreate(user);
        return userDao.create(user);
    }

    public User updateUser(User user) {
        validatorUpdated(user);
        return getUser(userDao.update(user));

    }

    public void deleteUser(long id) {
        userDao.delete(id);
    }

    private void validatorCreate(User user) {
        if (user.getId() != 0) {
            throw new ValidationException("При создании пользователя id не должен быть указан");
        } else if (findAll().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new RuntimeException("email пользователя должен быть уникальным");
        }
    }

    private void validatorUpdated(User user) {
        getUser(user.getId());
        if (findAll().stream().map(user1 -> user1.getEmail().equals(user.getEmail())).count()>1) {
            throw new RuntimeException("email пользователя должен быть уникальным");
        }
    }
    public void changeUser(User oldUser,User user){

    }
}
