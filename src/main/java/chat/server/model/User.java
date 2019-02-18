package chat.server.model;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "chat")
public class User {
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

    public Integer getId() {
        return user_id;
    }

    public String getLogin() {
        return login;
    }

    public String getCookieValue() {return cookieValue;}

    public Integer getUserId() {
        return user_id;
    }

    public User setRecentAction(LocalTime recentAction) {
        this.recentAction = recentAction;
        return this;
    }

    public LocalTime getRecentAction() {
        return recentAction;
    }

    public User setFullUser(String login, String password, String cookie_value, LocalTime recentAction) {
        this.login = login;
        this.password = password;
        this.cookieValue = cookie_value;
        this.recentAction = recentAction;
        return this;
    }
}
