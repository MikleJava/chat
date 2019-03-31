package chat.server.sevice;

import chat.server.dao.MessageDao;
import chat.server.dao.UserDao;
import chat.server.model.Message;
import chat.server.model.User;
import chat.server.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
public final class ChatService {

    @NotNull
    private static final org.slf4j.Logger log = Util.getLogger(ChatService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageDao messageDao;

    @Nullable
    public User getLoggedIn(@NotNull final String name, @NotNull final String pswd) {return userDao.getByLoginAndPassword(name, pswd);}

    @Nullable
    public User getLoggedInByCookie(@NotNull final String value) {
        return userDao.getByCookieValue(value);
    }

    @Nullable
    public User getLoggedInByTime(@NotNull final LocalTime rec_act) {return userDao.getByRecentActionTime(rec_act);}

    @Transactional
    public void login(@NotNull final String login, @NotNull final String password, @NotNull final String value, @NotNull final LocalTime time) {
        final User user = new User();
        userDao.save(user.setFullUser(login, password, value, time));
        log.info("[" + login + "] logged in");
    }

    @Transactional
    public void logout(@NotNull final User user, @NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) {
        userDao.delete(user);
        log.info("[" + user.getLogin() + "] logged out");
        deleteCookie(user, request, response);
    }

    @Transactional
    public void updateUser(@NotNull final User user) {
        userDao.update(user);
    }

    @NotNull
    public List<User> getOnlineUsers() {
        return new ArrayList<>(userDao.findAll());
    }

    @NotNull
    public List<Message> getMessages() {
        return new ArrayList<>(messageDao.getAll());
    }

    @Transactional
    public void say(@NotNull final String value, @NotNull final LocalTime time, @NotNull final User user_id) {
        final Message message = new Message();
        messageDao.save(message.setFullMsg(value, time, user_id));
        updateUser(user_id);
    }

    public Cookie setCookie(@NotNull final String SESSION_ID, @NotNull final String name, @NotNull final String pswd, @NotNull final HttpServletResponse response){
        final String value = name.hashCode() + "/" +  name.charAt(0) + pswd.hashCode() + "/" + LocalTime.now();
        final Cookie cookie = new Cookie(SESSION_ID, value);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
        return cookie;
    }

    public void deleteCookie(@NotNull final User user, @NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getValue().equals(user.getCookieValue())) {
                    response.setContentType("text/html");
                    Cookie deletingCookie = new Cookie(cookie.getName(), user.getCookieValue());
                    deletingCookie.setMaxAge(0);
                    deletingCookie.setPath("/");
                    response.addCookie(deletingCookie);
                }
            }
        }
    }
}