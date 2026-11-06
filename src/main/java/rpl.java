/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */



/**
 *
 * @author user
 */
import GUI.MainFrame;
import GUI.LoginPanel;
import entity.Produk;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import service.ProdukService;
public class rpl {

    public static void main(String[] args) {
    try {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {
       
        MainFrame frameUtama = new MainFrame();
    
        LoginPanel login = new LoginPanel(); 
        
        frameUtama.getContentPane().add(login);
        
        frameUtama.pack();
        
        frameUtama.setLocationRelativeTo(null); 
        
        frameUtama.setVisible(true);
    }
});

}

}

