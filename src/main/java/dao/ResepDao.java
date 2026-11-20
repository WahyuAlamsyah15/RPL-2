package dao;

import entity.Resep;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ResepDao {
    private final SessionFactory sessionFactory;
    public ResepDao(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory; }
    public void save(Resep resep) { sessionFactory.getCurrentSession().persist(resep); }
    public Resep findById(Integer id) { return sessionFactory.getCurrentSession().find(Resep.class, id); }
    public List<Resep> findAll() { return sessionFactory
            .getCurrentSession().createSelectionQuery("from Resep order by produk.name, bahanBaku.name", Resep.class)
            .getResultList(); }
    public List<Resep> findByProdukId(Integer id) { return sessionFactory
            .getCurrentSession().createSelectionQuery("from Resep where produk.id = :id", Resep.class)
            .setParameter("id", id).getResultList(); }
    public Resep update(Resep resep) { return sessionFactory.getCurrentSession().merge(resep); }
    public void deleteById(Integer id) { Resep r = findById(id); if (r != null) sessionFactory.getCurrentSession().remove(r); }
}
