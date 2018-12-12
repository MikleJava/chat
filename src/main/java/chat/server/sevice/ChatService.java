package chat.server.sevice;

import chat.server.dao.MessageDao;
import chat.server.dao.UserDao;
import chat.server.model.Message;
import chat.server.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private MessageDao messageDao;

    @Nullable
    @Transactional
    public User getLoggedIn(@NotNull String name, @NotNull String password) {
        return userDao.getByLoginAndPassword(name, password);
    }

    @Transactional
    public void login(@NotNull String login, @NotNull String password) {
        User user = new User();
        userDao.save(user.setLoginAndPassword(login, password));
        log.info("[" + login + "] logged in");
    }

    @Transactional
    public void logout(@NotNull User user) {
        userDao.delete(user);
        log.info("[" + user.getLogin() + "] logged out");
    }

    @NotNull
    @Transactional
    public List<User> getOnlineUsers() {
        return new ArrayList<>(userDao.findAll());
    }

    @NotNull
    @Transactional
    public List<Message> getMessages() {
        return new ArrayList<>(messageDao.getAll());
    }

    @Transactional
    public void say(@NotNull String value, @NotNull String time, @NotNull User user_id) {
        Message message = new Message();
        messageDao.save(message.setFullMsg(value, time, user_id));
    }
}