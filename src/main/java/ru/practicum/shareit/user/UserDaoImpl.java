package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private List<User> users = new ArrayList<>();
    private long id = 1;

    @Override
    public User create(User user) {
        user.setId(id);
        users.add(user);
        id++;
        return user;
    }

    @Override
    public Optional<User> get(long userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public long update(User newUser) {
        User oldUser = get(newUser.getId()).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        return oldUser.getId();
    }

    @Override
    public void delete(long id) {
        User user = get(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        users.remove(user);
    }
}
