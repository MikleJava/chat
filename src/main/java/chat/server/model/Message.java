package chat.server.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message", schema = "chat")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "value", nullable = false, length = 145)
    private String value;

    public Message setFullMsg(String value, Date time, User user_id) {
        this.value = value;
        this.time = time;
        this.user_id = user_id;
        return this;
    }

    public User getUserId() {
        return user_id;
    }

    public Date getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user_id=" + user_id +
                ", timestamp=" + time +
                ", value='" + value + '\'' +
                '}';
    }
}
