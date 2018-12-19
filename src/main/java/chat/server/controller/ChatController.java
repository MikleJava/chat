package chat.server.controller;

import chat.server.model.Message;
import chat.server.model.User;
import chat.server.sevice.ChatService;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    private static final String SESSION_ID = "SESSION_ID";
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name, @RequestParam("pswd") String pswd,
                                        HttpServletRequest request, HttpServletResponse response) {
        if (name.length() < 1) {return ResponseEntity.badRequest().body("Too short name");}
        if (name.length() > 25) {return ResponseEntity.badRequest().body("Too long name");}
        if (pswd.length() < 1) {return ResponseEntity.badRequest().body("Too short password");}
        if (pswd.length() > 25) {return ResponseEntity.badRequest().body("Too long password");}
        User alreadyLoggedIn = chatService.getLoggedIn(name, pswd);
        if(alreadyLoggedIn != null) {
            return ResponseEntity.badRequest().body("Already logged in");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            Cookie cookie = chatService.setCookie(SESSION_ID, name, pswd, response);
            chatService.login(name, pswd, cookie.getValue());
        } else {
            User loggedInUser;
            for(Cookie cookie : cookies) {
                loggedInUser = chatService.getLoggedIn(cookie.getValue());
                chatService.logout(loggedInUser);
                chatService.deleteCookie(request, response);
            }
            Cookie cookie = chatService.setCookie(SESSION_ID, name, pswd, response);
            chatService.login(name, pswd, cookie.getValue());
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "logout",
            method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            loggedInUser = chatService.getLoggedIn(cookie.getValue());
            if(loggedInUser == null) {
                return ResponseEntity.badRequest().body("You must be logged in");
            }
            chatService.logout(loggedInUser);
            chatService.deleteCookie(request, response);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("msg") String msg, HttpServletRequest request) {
        User loggedInUser = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            loggedInUser = chatService.getLoggedIn(cookie.getValue());
            if (loggedInUser == null) {
                return ResponseEntity.badRequest().body("You must be logged in");
            }
        }
        chatService.say(msg, sdf.format(new Date()), loggedInUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online(HttpServletRequest request) {
        List<User> onlineUsers = chatService.getOnlineUsers();
        Cookie[] cookies = request.getCookies();
        // Делаем проверку, получаем список текущих cookies, и проверяем есть ли такие cookie в базе.
        // Если в базе они есть, а в текущем списке нет, то нужно сделать chatService.logout() по данному пользователю
        for(User user : onlineUsers) {
            if(!Arrays.asList(cookies).stream().map(Cookie::getValue).collect(Collectors.toList()).contains(user.getValue())){
                chatService.logout(user);
            }
        }

        String responseBody = onlineUsers.stream()
                .map(User::getLogin)
                .collect(Collectors.joining("\n"));
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
