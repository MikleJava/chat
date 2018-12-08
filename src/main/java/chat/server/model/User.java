package chat.server.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Queue;

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

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "user_id")
    Collection<Message> messages;

    public Integer getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public User setLoginAndPassword(String login, String password) {
        this.login = login;
        this.password = password;
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
