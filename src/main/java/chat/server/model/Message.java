package chat.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "message", schema = "chat")
public final class Message {
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
}
