package chat.server.dao;

import chat.server.model.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;

@Transactional
@Repository
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager em;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    @Override
    public User getByCookieValue(String cookieValue) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> from = criteria.from(User.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("cookieValue"), cookieValue));
        TypedQuery<User> typed = em.createQuery(criteria);
        User user;
        try {
            user = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    public User getByRecentActionTime(LocalTime recentAction) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> from = criteria.from(User.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("recentAction"), recentAction));
        TypedQuery<User> typed = em.createQuery(criteria);
        User user;
        try {
            user = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    public User getByLoginAndPassword(String login, String password) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> from = criteria.from(User.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("login"), login) , builder.equal(from.get("password"), password));
        TypedQuery<User> typed = em.createQuery(criteria);
        User user;
        try {
            user = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public void delete(User user) {em.remove(em.contains(user) ? user : em.merge(user)); }

    @Override
    public void update(User user) {
        user.setRecentAction(LocalTime.now());
        em.merge(user);
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("Select t from " + User.class.getSimpleName() + " t").getResultList();
    }
}