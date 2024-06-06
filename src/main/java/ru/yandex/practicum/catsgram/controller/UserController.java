package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exceptions.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<String, User> emailToUsers = new HashMap<>();
    private final Map<Long, User> idToUsers = new HashMap<>();


    @GetMapping
    public Collection<User> findAll() {
        return emailToUsers.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (emailToUsers.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        idToUsers.put(user.getId(), user);
        emailToUsers.put(user.getEmail(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (user.getEmail() != null && emailToUsers.get(user.getEmail()) != null &&
                !emailToUsers.get(user.getEmail()).equals(user)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        User oldUser = idToUsers.get(user.getId());
        String oldEmail = oldUser.getEmail();
        if (user.getUsername() != null) {
            oldUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            oldUser.setPassword(user.getPassword());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
            emailToUsers.remove(oldEmail);
            emailToUsers.put(oldUser.getEmail(), oldUser);
        }
        return oldUser;
    }

    private long getNextId() {
        long maxID = idToUsers.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++maxID;
    }
}
