package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private static List<User> users = new ArrayList<>();
    private static long id = 1;


    @Override
    public User create(User user) {
        user.setId(id);
        users.add(user);
        id++;
        return user;
    }

    @Override
    public Optional<User> get(long userId) {
        return users.stream().filter(user -> user.getId() == userId)
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public long update(User newUser) {
        User oldUser = get(newUser.getId()).get();
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        return oldUser.getId();
    }

    @Override
    public void delete(long id) {
        User user = get(id).get();
        users.remove(user);
    }
}
