package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resep", uniqueConstraints = @UniqueConstraint(columnNames = {"produk_id", "bahan_baku_id"}))
public class Resep {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false) @JoinColumn(name = "produk_id")
    private Produk produk;
    @ManyToOne(optional = false) @JoinColumn(name = "bahan_baku_id")
    private BahanBaku bahanBaku;
    @Column(nullable = false)
    private int jumlahBahan;

    public Resep() { }
    public Resep(Produk produk, BahanBaku bahanBaku, int jumlahBahan) { this.produk = produk; this.bahanBaku = bahanBaku; this.jumlahBahan = jumlahBahan; }
    public Integer getId() { return id; }
    public Produk getProduk() { return produk; }
    public void setProduk(Produk produk) { this.produk = produk; }
    public BahanBaku getBahanBaku() { return bahanBaku; }
    public void setBahanBaku(BahanBaku bahanBaku) { this.bahanBaku = bahanBaku; }
    public int getJumlahBahan() { return jumlahBahan; }
    public void setJumlahBahan(int jumlahBahan) { this.jumlahBahan = jumlahBahan; }
}
