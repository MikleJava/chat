package chat.server;

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
import java.util.stream.Collectors;

@Controller
//@Scope("prototype")
@RequestMapping("chat")
public class ChatController {
    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    public Map<String, String> usersOnline = new HashMap<>();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @RequestMapping (
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name");
        }
        if (name.length() > 25) {
            return ResponseEntity.badRequest().body("Too long name");
        }
        if (usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("Already logged in");
        }
        usersOnline.put(name, name);
        messages.add(sdf.format(new Date())+ " [" + name + "] logged in");
        return ResponseEntity.ok().build();
    }

    @RequestMapping (
            path = "logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name");
        }
        if (name.length() > 25) {
            return ResponseEntity.badRequest().body("Too long name");
        }
        if(usersOnline.containsKey(name)) {
            usersOnline.remove(name, name);
            messages.add(sdf.format(new Date()) + " [" + name + "] logged out");
        }
        else {
            return ResponseEntity.badRequest().body("User " + name + " does not exist");
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        String responseBody = String.join("\n", usersOnline.keySet().stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping (
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        if(!usersOnline.containsKey(name)) return ResponseEntity.badRequest().body("User " + name + " does not exist");
        messages.add(sdf.format(new Date()) + " [" + name + "] : " + msg);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity chat() {
        String responseBody = String.join("\n", messages.stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }
}
