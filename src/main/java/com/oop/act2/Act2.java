/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.oop.act2;

/**
 *
 * @author user
 */
import entity.Produk;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import service.ProdukService;
public class Act2 {

    public static void main(String[] args) {
    // Mengatur tampilan GUI agar mengikuti tema Sistem Operasi (Windows/Mac/Linux)
    try {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {
        // 1. Membuat objek MainFrame (JFrame)
        MainFrame frameUtama = new MainFrame();
        
        // 2. Membuat objek LoginPanel (JPanel)
        LoginPanel login = new LoginPanel(); 
        
        // 3. Masukkan Panel ke dalam Frame
        frameUtama.getContentPane().add(login);
        
        // 4. Sesuaikan ukuran jendela otomatis dengan ukuran panel
        frameUtama.pack();
        
        // CORRECTION: Panggil setLocationRelativeTo pada frameUtama, BUKAN pada login!
        frameUtama.setLocationRelativeTo(null); 
        
        // 5. Tampilkan jendela utama ke layar
        frameUtama.setVisible(true);
    }
});

}

}

