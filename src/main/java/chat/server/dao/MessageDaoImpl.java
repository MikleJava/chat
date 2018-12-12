package chat.server.dao;

import chat.server.model.Message;
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
    public void save(Message message) {
        em.persist(message);
    }

    @Override
    public List<Message> getAll() {
        List<Message> m = em.createQuery("Select t from " + Message.class.getSimpleName() + " t").getResultList();
        return m;
    }
}
