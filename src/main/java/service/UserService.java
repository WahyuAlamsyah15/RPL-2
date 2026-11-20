package service;

import dao.UserDao;
import entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public User create(String username, String password) {
        validateCredentials(username, password);

        String cleanUsername = username.trim();
        if (userDao.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Username sudah digunakan.");
        }

        User user = new User();
        user.setUsername(cleanUsername);
        user.setPassword(password);

        userDao.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userDao.findById(id));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userDao.findByUsername(username.trim());
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User yang akan diubah tidak valid.");
        }
        validateCredentials(user.getUsername(), user.getPassword());

        user.setUsername(user.getUsername().trim());
        return userDao.update(user);
    }

    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            return;
        }
        userDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return Optional.empty();
        }

        return userDao.findByUsername(username.trim())
                .filter(user -> user.getPassword().equals(password));
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username wajib diisi.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password wajib diisi.");
        }
    }
}
