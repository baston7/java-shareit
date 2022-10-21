package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    Optional<User> get(long id);

    List<User> findAll();

    long update(User user);

    void delete(long id);
}
