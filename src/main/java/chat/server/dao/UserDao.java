package chat.server.dao;

import chat.server.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User getByCookieValue(String value);
    User getByLoginAndPassword(String login, String password);
    void save(User user);
    void delete(User user);
    List<User> findAll();
}
