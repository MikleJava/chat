package chat.server.dao;

import chat.server.model.Message;

import java.util.List;

public interface MessageDao {
    void save(Message message);
    List<Message> getAll();
}
