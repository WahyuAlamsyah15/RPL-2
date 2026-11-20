package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bahan_baku")
public class BahanBaku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 30)
    private String satuan;

    @Column(nullable = false)
    private int hargaBeli;

    @Column(nullable = false)
    private int jumlah;

    @Column(nullable = false)
    private int stokMinimum;

    public BahanBaku() { }

    public BahanBaku(String name, String satuan, int hargaBeli, int jumlah, int stokMinimum) {
        this.name = name;
        this.satuan = satuan;
        this.hargaBeli = hargaBeli;
        this.jumlah = jumlah;
        this.stokMinimum = stokMinimum;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public int getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(int hargaBeli) { this.hargaBeli = hargaBeli; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public int getStokMinimum() { return stokMinimum; }
    public void setStokMinimum(int stokMinimum) { this.stokMinimum = stokMinimum; }
}
