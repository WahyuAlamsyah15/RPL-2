package entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pesanan")
public class Pesanan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false) @JoinColumn(name = "produk_id")
    private Produk produk;
    @Column(nullable = false, length = 100)
    private String namaPelanggan;
    @Column(nullable = false)
    private int jumlah;
    @Column(nullable = false)
    private int totalHarga;
    @Column(nullable = false)
    private LocalDate tanggalPesanan;

    public Pesanan() { }
    public Integer getId() { return id; }
    public Produk getProduk() { return produk; }
    public void setProduk(Produk produk) { this.produk = produk; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public int getTotalHarga() { return totalHarga; }
    public void setTotalHarga(int totalHarga) { this.totalHarga = totalHarga; }
    public LocalDate getTanggalPesanan() { return tanggalPesanan; }
    public void setTanggalPesanan(LocalDate tanggalPesanan) { this.tanggalPesanan = tanggalPesanan; }
}
