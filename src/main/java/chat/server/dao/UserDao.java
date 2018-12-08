package chat.server.dao;

import chat.server.model.User;

import java.util.List;

public interface UserDao {
    User getByLoginAndPassword(String login, String password);
    void save(User user);
    void delete(User user);
    List<User> findAll();
}
