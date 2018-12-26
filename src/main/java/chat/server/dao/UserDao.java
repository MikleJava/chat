package chat.server.dao;

import chat.server.model.User;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User getByCookieValue(String value);
    User getByLoginAndPassword(String login, String password);
    User getByRecentActionTime(LocalTime rec_act);
    void save(User user);
    void delete(User user);
    void update(User user);
    List<User> findAll();
}
