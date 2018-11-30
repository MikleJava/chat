package chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
@RequestMapping("chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    public Queue<String> messages = new ConcurrentLinkedQueue<>();
    public Map<String, String> usersOnline = new HashMap<>();

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
        if (usersOnline.containsKey(pswd) && usersOnline.containsValue(name)) {
            return ResponseEntity.badRequest().body("Already logged in");
        }
        usersOnline.put(pswd, name);
        messages.add(sdf.format(new Date())+ " [" + name + "] logged in");
        log.info("User '" + name + "' logged in chat");
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name, @RequestParam("pswd") String pswd) {
        if(usersOnline.containsKey(pswd) && usersOnline.containsValue(name)) {
            usersOnline.remove(pswd, name);
            messages.add(sdf.format(new Date()) + " [" + name + "] logged out");
        }
        else if(!usersOnline.containsValue(name)){
            return ResponseEntity.badRequest().body("User '" + name + "' does not exist");
        }
        else if(!usersOnline.containsKey(pswd)) {
            return ResponseEntity.badRequest().body("Wrong password");
        }
        log.info("User '" + name + "' logged out chat");
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        String responseBody = String.join("\n", usersOnline.values());
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        if(!usersOnline.containsValue(name)) return ResponseEntity.badRequest().body("User '" + name + "' does not exist");
        messages.add(sdf.format(new Date()) + " " + name + " : " + msg);
        if(msg.equals("/clear")) {messages.removeAll(messages); log.info("User '" + name + "' removes all messages");}  //Команда "/clear" очищает все сообщения чата
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity chat() {
        String responseBody = String.join("\n", messages);
        return ResponseEntity.ok(responseBody);
    }
}
