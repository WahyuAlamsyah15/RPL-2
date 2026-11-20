package dao;

import entity.Produk;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProdukDao {

    private final SessionFactory sessionFactory;

    public ProdukDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Produk produk) {
        getCurrentSession().persist(produk);
    }

    public Produk findById(Integer id) {
        return getCurrentSession().find(Produk.class, id);
    }

    public Optional<Produk> findByName(String name) {
        return getCurrentSession()
                .createSelectionQuery("from Produk where name = :name", Produk.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    public List<Produk> searchByName(String keyword) {
        return getCurrentSession()
                .createSelectionQuery("from Produk where lower(name) like :keyword", Produk.class)
                .setParameter("keyword", "%" + keyword.toLowerCase() + "%")
                .getResultList();
    }

    public List<Produk> findAll() {
        return getCurrentSession()
                .createSelectionQuery("from Produk", Produk.class)
                .getResultList();
    }

    public Produk update(Produk produk) {
        return getCurrentSession().merge(produk);
    }

    public void delete(Produk produk) {
        getCurrentSession().remove(produk);
    }

    public void deleteById(Integer id) {
        Produk produk = findById(id);
        if (produk != null) {
            getCurrentSession().remove(produk);
        }
    }
}
