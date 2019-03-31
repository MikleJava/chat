package chat.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Collection;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user", schema = "chat")
public final class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer user_id;

    @Column(name = "login", nullable = false, length = 25)
    private String login;

    @Column(name = "password", nullable = false, length = 25)
    private String password;

    @Column(name = "cookie_val", nullable = false, length = 45)
    private String cookieValue;

    @Column(name = "rec_act", nullable = false)
    private LocalTime recentAction;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "user_id")
    Collection<Message> messages;

    public User setFullUser(String login, String password, String cookie_value, LocalTime recentAction) {
        this.login = login;
        this.password = password;
        this.cookieValue = cookie_value;
        this.recentAction = recentAction;
        return this;
    }
}
