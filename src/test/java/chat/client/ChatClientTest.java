package chat.client;

import okhttp3.Response;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class ChatClientTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ChatClientTest.class);
    private static String myName = "Mikle";
    private static String myPassword = "qwerty";
    private static String myMessage = "Hello, from the dark side in";

    @Test
    public void login() throws IOException {
        Response response = ChatClient.login(myName, myPassword);
        log.info("[" + response + "]");
        String body = response.body().string();
        log.info(body);
        assertTrue(response.code() == 200 || body.equals("Already logged in"));
    }

    @Test
    public void logout() throws IOException {
        Response response = ChatClient.logout();
        log.info("[" + response + "]");
        String body = response.body().string();
        log.info(body);
        assertTrue(response.code() == 200 || body.equals("User " + myName + " does not exist"));
    }

    @Test
    public void viewChat() throws IOException {
        Response response = ChatClient.viewChat();
        log.info("[" + response + "]");
        log.info(response.body().string());
        assertEquals(200, response.code());
    }

    @Test
    public void viewOnline() throws IOException {
        Response response = ChatClient.viewOnline();
        log.info("[" + response + "]");
        log.info(response.body().string());
        assertEquals(200, response.code());
    }

    @Test
    public void say() throws IOException {
        Response response = ChatClient.say(myMessage);
        log.info("[" + response + "]");
        log.info(response.body().string());
        assertEquals(200, response.code());
    }
}