package dao;

import entity.Pesanan;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PesananDao {
    private final SessionFactory sessionFactory;
    public PesananDao(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory; }
    public void save(Pesanan pesanan) { sessionFactory.getCurrentSession().persist(pesanan); }
    public List<Pesanan> findAll() { return sessionFactory
            .getCurrentSession().createSelectionQuery("from Pesanan order by tanggalPesanan desc, id desc", Pesanan.class)
            .getResultList(); }
    public Pesanan findById(Integer id) { return sessionFactory.getCurrentSession().find(Pesanan.class, id); }
    public void delete(Pesanan pesanan) { sessionFactory.getCurrentSession().remove(pesanan); }
}
