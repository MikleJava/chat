package chat.server.dao;

import chat.server.model.User;
import org.jetbrains.annotations.NotNull;
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

    @Override
    public User getByCookieValue(@NotNull final String cookieValue) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<User> criteria = builder.createQuery(User.class);
        final Root<User> from = criteria.from(User.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("cookieValue"), cookieValue));
        final TypedQuery<User> typed = em.createQuery(criteria);
        User user;
        try {
            user = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    public User getByRecentActionTime(@NotNull final LocalTime recentAction) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<User> criteria = builder.createQuery(User.class);
        final Root<User> from = criteria.from(User.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("recentAction"), recentAction));
        final TypedQuery<User> typed = em.createQuery(criteria);
        User user;
        try {
            user = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    public User getByLoginAndPassword(@NotNull final String login, @NotNull final String password) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<User> criteria = builder.createQuery(User.class);
        final Root<User> from = criteria.from(User.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("login"), login) , builder.equal(from.get("password"), password));
        final TypedQuery<User> typed = em.createQuery(criteria);
        User user;
        try {
            user = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    public void save(@NotNull final User user) {
        em.persist(user);
    }

    @Override
    public void delete(@NotNull final User user) {em.remove(em.contains(user) ? user : em.merge(user)); }

    @Override
    public void update(@NotNull final User user) {
        user.setRecentAction(LocalTime.now());
        em.merge(user);
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("Select t from " + User.class.getSimpleName() + " t").getResultList();
    }
}