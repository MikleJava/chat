package chat.server.model;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "message", schema = "chat")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer message_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "value", nullable = false, length = 145)
    private String value;

    public Message setFullMsg(String value, LocalTime time, User user_id) {
        this.value = value;
        this.time = time;
        this.user_id = user_id;
        return this;
    }

    public String getFullMsg() {
        return time + " " + user_id.getLogin() + " : " + value;
    }

    public User getUserId() {
        return user_id;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }

    public Integer getId() {
        return message_id;
    }
}
