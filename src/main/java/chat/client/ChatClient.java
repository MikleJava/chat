package chat.client;

import okhttp3.*;

import java.io.IOException;

public class ChatClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String PROTOCOL = "http://";
    private static final String HOST = "localhost";
    private static final String PORT = ":8080";

    //POST запрос для того, чтобы залогиниться пользователю "http://localhost:8080/chat/login"
    public static Response login(String name) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .post(RequestBody.create(mediaType,""))
                .url(PROTOCOL + HOST + PORT + "/chat/login?name=" + name)
                .build();
        return client.newCall(request).execute();
    }

    //GET запрос для того, чтобы увидеть основной чат со списокм сообщений "http://localhost:8080/chat/chat"
    public static Response viewChat() throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(PROTOCOL + HOST + PORT + "/chat/chat")
                .addHeader("host", HOST + PORT)
                .build();
        return client.newCall(request).execute();
    }

    //GET запрос для того, чтобы увидеть список онлайн пользователей "http://localhost:8080/chat/online"
    public static Response viewOnline() throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(PROTOCOL + HOST + PORT + "/chat/online")
                .addHeader("host", HOST + PORT)
                .build();
        return client.newCall(request).execute();
    }

    //POST запрос для того, чтобы пользователь мог написать сообщение в чат "http://localhost:8080/chat/say"
    public static Response say(String name, String msg) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .post(RequestBody.create(mediaType,""))
                .url(PROTOCOL + HOST + PORT + "/chat/say?name=" + name + "&msg=" + msg)
                .build();
        return client.newCall(request).execute();
    }

}
