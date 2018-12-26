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
    private String cookie_val;

    @Column(name = "rec_act", nullable = false)
    private LocalTime rec_act;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "user_id")
    Collection<Message> messages;

    public Integer getId() {
        return user_id;
    }

    public String getLogin() {
        return login;
    }

    public String getValue() {return cookie_val;}

    public Integer getUser_id() {
        return user_id;
    }

    public User setRec_act(LocalTime rec_act) {
        this.rec_act = rec_act;
        return this;
    }

    public LocalTime getRec_act() {
        return rec_act;
    }

    public User setFullUser(String login, String password, String cookie_value, LocalTime rec_act) {
        this.login = login;
        this.password = password;
        this.cookie_val = cookie_value;
        this.rec_act = rec_act;
        return this;
    }
}
