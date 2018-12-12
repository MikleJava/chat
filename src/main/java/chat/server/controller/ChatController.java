package chat.server.controller;

import chat.server.model.Message;
import chat.server.model.User;
import chat.server.sevice.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    private String name;
    private String pswd;

    @Autowired
    private ChatService chatService;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @RequestMapping (path = "setCookie",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String setCookie (HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("cookieName", request.getRequestURL().toString());
        cookie.setMaxAge(100);
        response.addCookie(cookie);
        System.out.println("Object: " + cookie + "; Name: " + cookie.getName() + "; Value: " + cookie.getValue());
        return "/cookie/cookieView";
    }

    @RequestMapping (path = "getCookie",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ModelAndView getCookie (@CookieValue(value = "cookieName", required = false)Cookie cookieName, HttpServletRequest request) {
        String cookieValue =  "cookie with name 'cookieName' is empty";
        if (cookieName != null) {
            cookieValue  = "Object: " + cookieName + ";<br/> Name: " + cookieName.getName() + ";<br/> Value: " + cookieName.getValue();
        }
        return new ModelAndView("/cookie/cookieView", "cookieValueObj", cookieValue);
    }

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name, @RequestParam("pswd") String pswd, HttpServletResponse response) {
        this.name = name;
        this.pswd = pswd;
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name");
        }
        if (name.length() > 25) {
            return ResponseEntity.badRequest().body("Too long name");
        }
        if (pswd.length() < 1) {
            return ResponseEntity.badRequest().body("Too short password");
        }
        if (pswd.length() > 25) {
            return ResponseEntity.badRequest().body("Too long password");
        }
        User alreadyLoggedIn = chatService.getLoggedIn(name, pswd);
        if(alreadyLoggedIn != null) {
            return ResponseEntity.badRequest().body("Already logged in");
        }
        chatService.login(name, pswd);
        Cookie cookie = new Cookie(name, pswd);
        cookie.setMaxAge(100);
        response.addCookie(cookie);
        System.out.println("Object: " + cookie + "; Name: " + cookie.getName() + "; Value: " + cookie.getValue());
        log.info("User '" + name + "' logged in chat");
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name, @RequestParam("pswd") String pswd) {
        User loggedInUser = chatService.getLoggedIn(name, pswd);
        if(loggedInUser == null) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        chatService.logout(loggedInUser);
        log.info("User '" + name + "' logged out chat");
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("msg") String msg) {
        User loggedInUser = chatService.getLoggedIn(name, pswd);
        if(loggedInUser == null) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        chatService.say(msg, sdf.format(new Date()), loggedInUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
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
    public ResponseEntity chat() {
        List<Message> messages = chatService.getMessages();
        String responseBody = messages.stream()
                .map(Message::getFullMsg)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }
}
