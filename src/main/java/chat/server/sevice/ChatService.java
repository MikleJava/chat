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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;
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
    public User getLoggedIn(@NotNull String name, @NotNull String pswd) {return userDao.getByLoginAndPassword(name, pswd);}

    @Nullable
    @Transactional
    public User getLoggedInByCookie(@NotNull String value) {
        return userDao.getByCookieValue(value);
    }

    @Nullable
    @Transactional
    public User getLoggedInByTime(@NotNull LocalTime rec_act) {return userDao.getByRecentActionTime(rec_act);}

    @Transactional
    public void login(@NotNull String login, @NotNull String password, @NotNull String value, @NotNull LocalTime time) {
        User user = new User();
        userDao.save(user.setFullUser(login, password, value, time));
        log.info("[" + login + "] logged in");
    }

    @Transactional
    public void logout(@NotNull User user, HttpServletRequest request, HttpServletResponse response) {
        userDao.delete(user);
        log.info("[" + user.getLogin() + "] logged out");
        deleteCookie(user, request, response);
    }

    @Transactional
    public void updateUser(@NotNull User user) {
        userDao.update(user);
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
    public void say(@NotNull String value, @NotNull LocalTime time, @NotNull User user_id) {
        Message message = new Message();
        messageDao.save(message.setFullMsg(value, time, user_id));
        updateUser(user_id);
    }

    public Cookie setCookie(String SESSION_ID, @NotNull String name, @NotNull String pswd, HttpServletResponse response){
        String value = name.hashCode() + "/" +  name.charAt(0) + pswd.hashCode() + "/" + LocalTime.now();
        Cookie cookie = new Cookie(SESSION_ID, value);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
        return cookie;
    }

    public void deleteCookie(@NotNull User user, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getValue().equals(user.getValue())) {
                response.setContentType("text/html");
                Cookie deletingCookie = new Cookie(cookie.getName(), user.getValue());
                deletingCookie.setMaxAge(0);
                deletingCookie.setPath("/");
                response.addCookie(deletingCookie);
            }
        }
    }
}