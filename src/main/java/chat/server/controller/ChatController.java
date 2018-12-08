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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name, @RequestParam("pswd") String pswd) {
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

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("pswd") String pswd, @RequestParam("msg") String msg) {
        User loggedInUser = chatService.getLoggedIn(name, pswd);
        if(loggedInUser == null) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        chatService.say(msg, new Date(), loggedInUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity chat() {
        List<Message> messages = chatService.getMessages();
        String responseBody = messages.stream()
                .map(Message::getValue)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(responseBody);
    }
}
