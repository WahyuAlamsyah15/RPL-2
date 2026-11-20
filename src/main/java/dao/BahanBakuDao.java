package dao;

import entity.BahanBaku;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BahanBakuDao {
    private final SessionFactory sessionFactory;

    public BahanBakuDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() { return sessionFactory.getCurrentSession(); }
    public void save(BahanBaku bahan) { currentSession().persist(bahan); }
    public BahanBaku findById(Integer id) { return currentSession().find(BahanBaku.class, id); }
    public List<BahanBaku> findAll() { return currentSession()
            .createSelectionQuery("from BahanBaku order by name", BahanBaku.class).getResultList(); }
    public BahanBaku update(BahanBaku bahan) { return currentSession().merge(bahan); }
    public void deleteById(Integer id) { BahanBaku bahan = findById(id); if (bahan != null) currentSession().remove(bahan); }
}
