package service;

import dao.ProdukDao;
import entity.Produk;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class ProdukService {

    private final ProdukDao produkDao;

    public ProdukService(ProdukDao produkDao) {
        this.produkDao = produkDao;
    }

    @Transactional
    public Produk add(Scanner scan) {
        System.out.println("INPUT DATA PRODUK");
        System.out.print("Masukkan nama produk : ");
        String nama = scan.nextLine().trim();

        System.out.print("Masukkan harga produk : ");
        int harga = scan.nextInt();

        System.out.print("Masukkan jumlah produk : ");
        int jumlah = scan.nextInt();
        scan.nextLine(); // Membersihkan sisa newline di buffer Scanner

        if (nama.isBlank()) {
            throw new IllegalArgumentException("Nama produk wajib diisi.");
        }
        if (harga < 0 || jumlah < 0) {
            throw new IllegalArgumentException("Harga dan jumlah tidak boleh negatif.");
        }

        Produk produk = new Produk();
        produk.setName(nama);
        produk.setHarga(harga);
        produk.setJumlah(jumlah);

        produkDao.save(produk);
        System.out.println("Produk berhasil ditambahkan ke database.");
        return produk;
    }

    @Transactional(readOnly = true)
    public void tampil() {
        List<Produk> daftarProduk = produkDao.findAll();

        System.out.println("=== DAFTAR PRODUK ===");
        if (daftarProduk.isEmpty()) {
            System.out.println("[ Data masih kosong! Belum ada data di database ]");
        } else {
            for (Produk p : daftarProduk) {
                System.out.println("ID     : " + p.getId());
                System.out.println("Nama   : " + p.getName());
                System.out.println("Harga  : " + p.getHarga());
                System.out.println("Jumlah : " + p.getJumlah());
                System.out.println("--------------------");
            }
        }
        System.out.println();
    }

    @Transactional
    public void kurangiStok(Scanner input) {
        System.out.println("=== STOK DATA KELUAR ===");
        List<Produk> daftarProduk = produkDao.findAll();

        if (daftarProduk.isEmpty()) {
            System.out.println("[ Gagal: Data produk masih kosong! ]\n");
            return;
        }

        System.out.print("Masukkan Nama Produk yang keluar: ");
        String namaCari = input.nextLine().trim();

        Optional<Produk> produkOpt = produkDao.findByName(namaCari);

        if (produkOpt.isEmpty()) {
            System.out.println("Gagal! Produk dengan nama '" + namaCari + "' tidak ditemukan.\n");
            return;
        }

        Produk p = produkOpt.get();
        System.out.print("Masukkan Jumlah Keluar: ");
        int jumlahKeluar = input.nextInt();
        input.nextLine(); // Membersihkan sisa newline

        if (jumlahKeluar <= 0) {
            System.out.println("Gagal! Jumlah keluar harus lebih dari 0.\n");
            return;
        }

        if (jumlahKeluar <= p.getJumlah()) {
            int stokBaru = p.getJumlah() - jumlahKeluar;
            p.setJumlah(stokBaru);
            produkDao.update(p);
            System.out.println("Berhasil! Stok " + p.getName() + " berkurang. Sisa stok: " + stokBaru + "\n");
        } else {
            System.out.println("Gagal! Jumlah keluar melebihi stok yang ada (Stok saat ini: " + p.getJumlah() + ")\n");
        }
    }

    @Transactional(readOnly = true)
    public List<Produk> findAll() {
        return produkDao.findAll();
    }

    @Transactional
    public Produk create(String nama, int harga, int jumlah) {
        validate(nama, harga, jumlah);
        Produk produk = new Produk(nama.trim(), harga, jumlah);
        produkDao.save(produk);
        return produk;
    }

    @Transactional
    public Produk update(Integer id, String nama, int harga, int jumlah) {
        validate(nama, harga, jumlah);
        Produk produk = produkDao.findById(id);
        if (produk == null) {
            throw new IllegalArgumentException("Produk tidak ditemukan.");
        }
        produk.setName(nama.trim());
        produk.setHarga(harga);
        produk.setJumlah(jumlah);
        return produkDao.update(produk);
    }

    @Transactional
    public Produk tambahStok(Integer id, int tambahanJumlah) {
        if (tambahanJumlah <= 0) {
            throw new IllegalArgumentException("Jumlah stok yang ditambahkan harus lebih dari 0.");
        }
        Produk produk = produkDao.findById(id);
        if (produk == null) {
            throw new IllegalArgumentException("Produk tidak ditemukan.");
        }
        produk.setJumlah(produk.getJumlah() + tambahanJumlah);
        return produkDao.update(produk);
    }

    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Produk tidak valid.");
        }
        produkDao.deleteById(id);
    }

    private void validate(String nama, int harga, int jumlah) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama produk wajib diisi.");
        }
        if (harga < 0 || jumlah < 0) {
            throw new IllegalArgumentException("Harga dan jumlah tidak boleh negatif.");
        }
    }
}
