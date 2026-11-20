package dao;

import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(User user) {
        getCurrentSession().persist(user);
    }

    public User findById(Integer id) {
        return getCurrentSession().find(User.class, id);
    }

    public Optional<User> findByUsername(String username) {
        return getCurrentSession()
                .createSelectionQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public boolean existsByUsername(String username) {
        Long count = getCurrentSession()
                .createSelectionQuery("select count(u) from User u where u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    public List<User> findAll() {
        return getCurrentSession()
                .createSelectionQuery("from User", User.class)
                .getResultList();
    }

    public User update(User user) {
        return getCurrentSession().merge(user);
    }

    public void delete(User user) {
        getCurrentSession().remove(user);
    }

    public void deleteById(Integer id) {
        User user = findById(id);
        if (user != null) {
            getCurrentSession().remove(user);
        }
    }
}
