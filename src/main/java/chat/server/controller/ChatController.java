package chat.server.controller;

import chat.server.model.Message;
import chat.server.model.User;
import chat.server.sevice.ChatService;
import chat.server.socket.Content;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController extends HttpServlet {
    private static final String SESSION_ID = "SESSION_ID";
    private User loggedInUser;

    @Autowired
    private ChatService chatService;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @MessageMapping("/content")
    @SendTo("/topic/messages")
    public Content greeting(Message message) throws Exception {
        return new Content(sdf.format(new Date()) + " " + loggedInUser.getLogin() + " : " + HtmlUtils.htmlEscape(message.getValue()));
    }

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name, @RequestParam("pswd") String pswd, HttpServletResponse response) {
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

    @RequestMapping (
            path = "logout",
            method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                loggedInUser = chatService.getLoggedInByCookie(cookie.getValue());
                if (loggedInUser == null) {
                    return ResponseEntity.badRequest().body("You must be logged in");
                }
                chatService.logout(loggedInUser, request, response);
            }
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("msg") String msg, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {return ResponseEntity.badRequest().body("You must be logged in");}  //this check in case if no one user logged in
        for(Cookie cookie : cookies) {
            loggedInUser = chatService.getLoggedInByCookie(cookie.getValue());
            if (loggedInUser == null) {
                return ResponseEntity.badRequest().body("You must be logged in");
            }
        }
        chatService.say(msg, LocalTime.now(), loggedInUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online(HttpServletRequest request, HttpServletResponse response) {
        List<User> onlineUsers = chatService.getOnlineUsers();
        String responseBody = onlineUsers.stream()
                .map(User::getLogin)
                .collect(Collectors.joining("\n"));
        List<LocalTime> timeOut = onlineUsers.stream().map(User::getRec_act).collect(Collectors.toList());
        for(LocalTime lt : timeOut) {
            //if user's last action was 5 or more minutes ago, server deletes him out from db
            if(LocalTime.now().getMinute() >= lt.getMinute() && LocalTime.now().getMinute() - lt.getMinute() >= 5) {
                onlineUsers.stream().map(loctime -> chatService.getLoggedInByTime(lt))
                        .filter(Objects::nonNull)
                        .forEach(user -> chatService.logout(user, request,  response));
            }
            //the same situation, but if the time of the last user's action was, for example, at 10:57 and current time 11:02,
            //it means that 02<57, so we need add 60 minutes to correctly count period of the last user action. 62-57 = 5 minutes
            else if(LocalTime.now().getMinute() < lt.getMinute() && (LocalTime.now().getMinute() + 60) - lt.getMinute() >= 5) {
                onlineUsers.stream().map(loctime -> chatService.getLoggedInByTime(lt))
                        .filter(Objects::nonNull)
                        .forEach(user -> chatService.logout(user, request , response));
            }
        }
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity chat() {
        List<Message> messages = chatService.getMessages();
        String responseBody = messages.stream()
                .map(Message::getFullMsg)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }
}
