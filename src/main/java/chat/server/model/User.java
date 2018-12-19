package chat.server.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "chat")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "login", nullable = false, length = 25)
    private String login;

    @Column(name = "password", nullable = false, length = 25)
    private String password;

    //Значение cookie
    @Column(name = "value", nullable = false, length = 45)
    private String value;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "user_id")
    Collection<Message> messages;

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getValue() {return value;}

    public User setLoginPasswordCookie(String login, String password, String value) {
        this.login = login;
        this.password = password;
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login=" + login +
                ", password='" + '\'' +
                '}';
    }
}
