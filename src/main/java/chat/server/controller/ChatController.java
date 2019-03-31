package chat.server.controller;

import chat.server.model.Message;
import chat.server.model.User;
import chat.server.sevice.ChatService;
import chat.server.socket.Content;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static chat.server.util.Util.*;

@Controller
@RequestMapping("chat")
public final class ChatController extends HttpServlet {

    private User loggedInUser;

    @MessageMapping("/content")
    @SendTo("/topic/messages")
    public Content greeting(@NotNull final Message message) {
        return new Content(sdfHMS.format(new Date()) + " " + loggedInUser.getLogin() + " : " + HtmlUtils.htmlEscape(message.getValue()));
    }

    @NotNull
    private static final String SESSION_ID = "SESSION_ID";

    @Autowired
    private ChatService chatService;

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@NotNull @RequestParam("name") final String name, @NotNull @RequestParam("pswd") final String pswd, @NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) {
        if (name.length() < 1) {return ResponseEntity.badRequest().body("Too short name");}
        if (name.length() > 25) {return ResponseEntity.badRequest().body("Too long name");}
        if (pswd.length() < 1) {return ResponseEntity.badRequest().body("Too short password");}
        if (pswd.length() > 25) {return ResponseEntity.badRequest().body("Too long password");}
        loggedInUser = chatService.getLoggedIn(name, pswd);
        if(loggedInUser != null) {return ResponseEntity.badRequest().body("Already logged in");}
        final Cookie cookie = chatService.setCookie(SESSION_ID, name, pswd, response);
        chatService.login(name, pswd, cookie.getValue(), LocalTime.now());
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "autologout",
            method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> autoLogout(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final List<User> onlineUsers = chatService.getOnlineUsers();
                onlineUsers.stream()
                        .map(User::getRecentAction)
                        .filter(lt -> (lt.plusMinutes(1).getHour() + ":" + lt.plusMinutes(1).getMinute()).equals(sdfHM.format(new Date())))
                        .map(chatService::getLoggedInByTime)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                        .forEach(user -> chatService.logout(user, request, response));
            }
        }, 10, 10, TimeUnit.SECONDS);
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "logout",
            method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) {
        if(request.getCookies() == null) {return ResponseEntity.badRequest().body("You must be logged in");}
        final Optional<Cookie> cookies = Optional.of(Arrays.stream(request.getCookies()).findAny().get());
        loggedInUser = chatService.getLoggedInByCookie(cookies.get().getValue());
        chatService.logout(loggedInUser, request, response);
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@NotNull @RequestParam("msg") final String msg, @NotNull final HttpServletRequest request) {
        if(request.getCookies() == null) {return ResponseEntity.badRequest().body("You must be logged in");}
        final Optional<Cookie> cookies = Optional.of(Arrays.stream(request.getCookies()).findAny().get());
        loggedInUser = chatService.getLoggedInByCookie(cookies.get().getValue());
        chatService.say(msg, LocalTime.now(), loggedInUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> online() {
        final List<User> onlineUsers = chatService.getOnlineUsers();
        final String responseBody = onlineUsers.stream()
                .map(User::getLogin)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> chat() {
        final List<Message> messages = chatService.getMessages();
        final String responseBody = messages.stream()
                .map(Message::getFullMsg)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }
}
