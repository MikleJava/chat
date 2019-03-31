package chat.server.dao;

import chat.server.model.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class MessageDaoImpl implements MessageDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(@NotNull final Message message) {
        em.persist(message);
    }

    @Override
    public List<Message> getAll() {
        return (List<Message>) em.createQuery("Select t from " + Message.class.getSimpleName() + " t").getResultList();
    }
}
