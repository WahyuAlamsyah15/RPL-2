package service;

import dao.BahanBakuDao;
import entity.BahanBaku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BahanBakuService {
    private final BahanBakuDao bahanBakuDao;

    public BahanBakuService(BahanBakuDao bahanBakuDao) {
        this.bahanBakuDao = bahanBakuDao;
    }

    @Transactional(readOnly = true)
    public List<BahanBaku> findAll() { return bahanBakuDao.findAll(); }

    @Transactional
    public BahanBaku create(String nama, String satuan, int hargaBeli, int jumlah, int stokMinimum) {
        validate(nama, satuan, hargaBeli, jumlah, stokMinimum);
        BahanBaku bahan = new BahanBaku(nama.trim(), satuan.trim(), hargaBeli, jumlah, stokMinimum);
        bahanBakuDao.save(bahan);
        return bahan;
    }

    @Transactional
    public BahanBaku update(Integer id, String nama, String satuan, int hargaBeli, int jumlah, int stokMinimum) {
        validate(nama, satuan, hargaBeli, jumlah, stokMinimum);
        BahanBaku bahan = bahanBakuDao.findById(id);
        if (bahan == null) throw new IllegalArgumentException("Bahan baku tidak ditemukan.");
        bahan.setName(nama.trim());
        bahan.setSatuan(satuan.trim());
        bahan.setHargaBeli(hargaBeli);
        bahan.setJumlah(jumlah);
        bahan.setStokMinimum(stokMinimum);
        return bahanBakuDao.update(bahan);
    }

    @Transactional
    public BahanBaku tambahStok(Integer id, int tambahanJumlah, int tambahanHarga) {
        if (tambahanJumlah <= 0) throw new IllegalArgumentException("Jumlah stok yang ditambahkan harus lebih dari 0.");
        if (tambahanHarga < 0) throw new IllegalArgumentException("Harga tambahan tidak boleh negatif.");
        BahanBaku bahan = bahanBakuDao.findById(id);
        if (bahan == null) throw new IllegalArgumentException("Bahan baku tidak ditemukan.");
        bahan.setJumlah(bahan.getJumlah() + tambahanJumlah);
        bahan.setHargaBeli(bahan.getHargaBeli() + tambahanHarga);
        return bahanBakuDao.update(bahan);
    }

    @Transactional
    public void delete(Integer id) { if (id == null) throw new IllegalArgumentException("Bahan baku tidak valid."); bahanBakuDao.deleteById(id); }

    private void validate(String nama, String satuan, int hargaBeli, int jumlah, int stokMinimum) {
        if (nama == null || nama.isBlank()) throw new IllegalArgumentException("Nama bahan baku wajib diisi.");
        if (satuan == null || satuan.isBlank()) throw new IllegalArgumentException("Satuan wajib diisi.");
        if (hargaBeli < 0 || jumlah < 0 || stokMinimum < 0) throw new IllegalArgumentException("Nilai stok dan harga tidak boleh negatif.");
    }
}
