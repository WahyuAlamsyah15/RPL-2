package service;

import dao.BahanBakuDao;
import dao.ProdukDao;
import dao.ResepDao;
import entity.BahanBaku;
import entity.Produk;
import entity.Resep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ResepService {
    private final ResepDao resepDao; private final ProdukDao produkDao; private final BahanBakuDao bahanDao;
    public ResepService(ResepDao resepDao, ProdukDao produkDao, BahanBakuDao bahanDao) { this.resepDao = resepDao; this.produkDao = produkDao; this.bahanDao = bahanDao; }
    @Transactional(readOnly = true) public List<Resep> findAll() { return resepDao.findAll(); }
    @Transactional public Resep create(Integer produkId, Integer bahanId, int jumlah) { if (jumlah <= 0) throw new IllegalArgumentException("Jumlah bahan harus lebih dari 0."); Produk p = produkDao.findById(produkId); BahanBaku b = bahanDao.findById(bahanId); if (p == null || b == null) throw new IllegalArgumentException("Produk atau bahan baku tidak ditemukan."); return save(p, b, jumlah); }
    @Transactional public Resep update(Integer id, Integer produkId, Integer bahanId, int jumlah) { Resep r = resepDao.findById(id); if (r == null) throw new IllegalArgumentException("Resep tidak ditemukan."); if (jumlah <= 0) throw new IllegalArgumentException("Jumlah bahan harus lebih dari 0."); r.setProduk(produkDao.findById(produkId)); r.setBahanBaku(bahanDao.findById(bahanId)); r.setJumlahBahan(jumlah); return resepDao.update(r); }
    private Resep save(Produk p, BahanBaku b, int jumlah) { Resep r = new Resep(p, b, jumlah); resepDao.save(r); return r; }
    @Transactional public void delete(Integer id) { resepDao.deleteById(id); }
    @Transactional public void produksi(Integer produkId, int jumlahProduk) { if (jumlahProduk <= 0) throw new IllegalArgumentException("Jumlah produksi harus lebih dari 0."); Produk p = produkDao.findById(produkId); if (p == null) throw new IllegalArgumentException("Produk tidak ditemukan."); List<Resep> resep = resepDao.findByProdukId(produkId); if (resep.isEmpty()) throw new IllegalArgumentException("Produk ini belum memiliki resep."); for (Resep r : resep) { int kebutuhan = r.getJumlahBahan() * jumlahProduk; BahanBaku b = r.getBahanBaku(); if (b.getJumlah() < kebutuhan) throw new IllegalArgumentException("Stok " + b.getName() + " tidak cukup. Dibutuhkan: " + kebutuhan + " " + b.getSatuan()); b.setJumlah(b.getJumlah() - kebutuhan); bahanDao.update(b); } p.setJumlah(p.getJumlah() + jumlahProduk); produkDao.update(p); }
}
