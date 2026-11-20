package service;

import dao.PesananDao;
import dao.ProdukDao;
import entity.Pesanan;
import entity.Produk;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class PesananService {
    private final PesananDao pesananDao; private final ProdukDao produkDao;
    public PesananService(PesananDao pesananDao, ProdukDao produkDao) { this.pesananDao = pesananDao; this.produkDao = produkDao; }
    @Transactional(readOnly = true) public List<Pesanan> findAll() { return pesananDao.findAll(); }
    @Transactional public Pesanan create(Integer produkId, String pelanggan, int jumlah) { if (pelanggan == null || pelanggan.isBlank()) throw new IllegalArgumentException("Nama pelanggan wajib diisi."); if (jumlah <= 0) throw new IllegalArgumentException("Jumlah pesanan harus lebih dari 0."); Produk p = produkDao.findById(produkId); if (p == null) throw new IllegalArgumentException("Produk tidak ditemukan."); if (p.getJumlah() < jumlah) throw new IllegalArgumentException("Stok produk tidak cukup. Sisa stok: " + p.getJumlah()); p.setJumlah(p.getJumlah() - jumlah); produkDao.update(p); Pesanan order = new Pesanan(); order.setProduk(p); order.setNamaPelanggan(pelanggan.trim()); order.setJumlah(jumlah); order.setTotalHarga(p.getHarga() * jumlah); order.setTanggalPesanan(LocalDate.now()); pesananDao.save(order); return order; }
    @Transactional public void delete(Integer id) { Pesanan o = pesananDao.findById(id); if (o == null) throw new IllegalArgumentException("Pesanan tidak ditemukan."); Produk p = o.getProduk(); p.setJumlah(p.getJumlah() + o.getJumlah()); produkDao.update(p); pesananDao.delete(o); }
}
