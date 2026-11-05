/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author user
 */

import entity.Produk;
import java.util.List;
import java.util.Scanner;
public class ProdukService {
    
    public Produk add(Scanner scan){
        
        
        System.out.println("INPUT DATA PRODUK");
        System.out.print("Masukkan nama produk : ");
        String nama = scan.nextLine();
        
        System.out.print("Masukkan harga produk : ");
        int harga = scan.nextInt();
        
        System.out.print("Masukkan jumlah produk : ");
        int jumlah = scan.nextInt();
        
        Produk produk = new Produk();
        produk.setName(nama);
        produk.setHarga(harga);
        produk.setJumlah(jumlah);
        
        System.out.println("Produk berhasil ditambahkan");
        return produk;
    }
    
     public void tampil(List<Produk> daftarProduk) {
        System.out.println("=== DAFTAR PRODUK ===");
        if (daftarProduk.isEmpty()) {
            System.out.println("[ List masih kosong! Belum ada data ]");
        } else {
            for (Produk p : daftarProduk) {
                System.out.println("Nama   : " + p.getName());
                System.out.println("Harga  : " + p.getHarga());
                System.out.println("Jumlah : " + p.getJumlah());
                System.out.println("--------------------");
            }
        }
        System.out.println(); 
    }
     
    public void kurangiStok(List<Produk> daftarProduk, Scanner input) {
        System.out.println("=== STOK DATA KELUAR ===");
        if (daftarProduk.isEmpty()) {
            System.out.println("[ Gagal: List masih kosong! ]\n");
            return;
        }

        System.out.print("Masukkan Nama Produk yang keluar: ");
        String namaCari = input.nextLine();

        boolean ditemukan = false;

        for (Produk p : daftarProduk) {
            
            if (p.getName().equalsIgnoreCase(namaCari)) {
                ditemukan = true;
                
                System.out.print("Masukkan Jumlah Keluar: ");
                int jumlahKeluar = input.nextInt();
                input.nextLine(); 

                
                if (jumlahKeluar <= p.getJumlah()) {
                    
                    int stokBaru = p.getJumlah() - jumlahKeluar;
                    p.setJumlah(stokBaru);
                    System.out.println("Berhasil! Stok " + p.getName() + " berkurang. Sisa stok: " + stokBaru + "\n");
                } else {
                    System.out.println("Gagal! Jumlah keluar melebihi stok yang ada (Stok saat ini: " + p.getJumlah() + ")\n");
                }
                break; 
            }
        }

        if (!ditemukan) {
            System.out.println("Gagal! Produk dengan nama '" + namaCari + "' tidak ditemukan.\n");
        }
    }
}
