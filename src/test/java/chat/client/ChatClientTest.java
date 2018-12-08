package chat.client;

import chat.server.controller.ChatController;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ChatClientTest {
    private static String myName = "Mikle";
    private static String myMessage = "Hello, from the dark side in";

    @Test
    public void login() throws IOException {
        Response response = ChatClient.login(myName, "qwerty");
        System.out.println("[" + response + "]");
        String body = response.body().string();
        System.out.println(body);
        assertTrue(response.code() == 200 || body.equals("Already logged in"));
    }

    @Test
    public void logout() throws IOException {
        Response response = ChatClient.logout(myName);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        System.out.println(body);
        assertTrue(response.code() == 200 || body.equals("User " + myName + " does not exist"));
    }

    @Test
    public void viewChat() throws IOException {
        Response response = ChatClient.viewChat();
        System.out.println("[" + response + "]");
        System.out.println(response.body().string());
        assertEquals(200, response.code());
    }

    @Test
    public void viewOnline() throws IOException {
        Response response = ChatClient.viewOnline();
        System.out.println("[" + response + "]");
        System.out.println(response.body().string());
        assertEquals(200, response.code());
    }

    @Test
    public void say() throws IOException {
        Response response = ChatClient.say(myName, myMessage);
        System.out.println("[" + response + "]");
        System.out.println(response.body().string());
        assertEquals(200, response.code());
    }

    @Test
    public void clear() throws IOException {
        Response response = ChatClient.say(myName, "/clear");
        ChatController chatController = new ChatController();
        System.out.println("[" + response + "]");
        System.out.println(response.body().string());
        //assertEquals(chatController.messages.size(), 0);
    }
}