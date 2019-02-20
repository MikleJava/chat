package chat.server.controller;

import chat.server.model.Message;
import chat.server.model.User;
import chat.server.sevice.ChatService;
import chat.server.socket.Content;
import org.apache.tomcat.jni.Local;
import org.slf4j.LoggerFactory;
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
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("chat")
public class ChatController extends HttpServlet {

    SimpleDateFormat sdfHMS = new SimpleDateFormat("HH:mm:ss");
    private User loggedInUser;

    @MessageMapping("/content")
    @SendTo("/topic/messages")
    public Content greeting(Message message) throws Exception {
        return new Content(sdfHMS.format(new Date()) + " " + loggedInUser.getLogin() + " : " + HtmlUtils.htmlEscape(message.getValue()));
    }

    private static final String SESSION_ID = "SESSION_ID";
    @Autowired
    private ChatService chatService;

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name, @RequestParam("pswd") String pswd, HttpServletRequest request, HttpServletResponse response) {
        if (name.length() < 1) {return ResponseEntity.badRequest().body("Too short name");}
        if (name.length() > 25) {return ResponseEntity.badRequest().body("Too long name");}
        if (pswd.length() < 1) {return ResponseEntity.badRequest().body("Too short password");}
        if (pswd.length() > 25) {return ResponseEntity.badRequest().body("Too long password");}
        loggedInUser = chatService.getLoggedIn(name, pswd);
        if(loggedInUser != null) {return ResponseEntity.badRequest().body("Already logged in");}
        Cookie cookie = chatService.setCookie(SESSION_ID, name, pswd, response);
        chatService.login(name, pswd, cookie.getValue(), LocalTime.now());
        return ResponseEntity.ok().build();
    }

    SimpleDateFormat sdfHM = new SimpleDateFormat("HH:mm");
    @RequestMapping (
            path = "autologout",
            method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> autoLogout(HttpServletRequest request, HttpServletResponse response) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<User> onlineUsers = chatService.getOnlineUsers();
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
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if(request.getCookies() == null) {return ResponseEntity.badRequest().body("You must be logged in");}
        Optional<Cookie> cookies = Optional.of(Arrays.stream(request.getCookies()).findAny().get());
        loggedInUser = chatService.getLoggedInByCookie(cookies.get().getValue());
        chatService.logout(loggedInUser, request, response);
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("msg") String msg, HttpServletRequest request) {
        if(request.getCookies() == null) {return ResponseEntity.badRequest().body("You must be logged in");}
        Optional<Cookie> cookies = Optional.of(Arrays.stream(request.getCookies()).findAny().get());
        loggedInUser = chatService.getLoggedInByCookie(cookies.get().getValue());
        chatService.say(msg, LocalTime.now(), loggedInUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> online() {
        List<User> onlineUsers = chatService.getOnlineUsers();
        String responseBody = onlineUsers.stream()
                .map(User::getLogin)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> chat() {
        List<Message> messages = chatService.getMessages();
        String responseBody = messages.stream()
                .map(Message::getFullMsg)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }
}
