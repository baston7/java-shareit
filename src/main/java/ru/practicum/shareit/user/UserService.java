package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.InputMismatchException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

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
        long id = userDao.update(user);
        return getUser(id);
    }

    public void deleteUser(long id) {
        userDao.delete(id);
    }

    private void validatorCreate(User user) {
        if (user.getId() != 0) {
            throw new ValidationException("При создании пользователя id не должен быть указан");
        } else if (findAll().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new InputMismatchException("email пользователя должен быть уникальным");
        }
    }

    private void validatorUpdated(User user) {
        User oldUser = getUser(user.getId());
        if (!oldUser.getEmail().equals(user.getEmail())) {
            if (findAll().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
                throw new InputMismatchException("email пользователя должен быть уникальным");
            }
        }
    }

    public void setNewFieldsForUpdate(User newUser, User oldUser) {
        if (newUser.getName() == null && newUser.getEmail() == null) {
            throw new ValidationException("Поля значений пустые");
        }
        if (newUser.getName() != null && newUser.getEmail() != null) {
            return;
        }
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
    }
}
