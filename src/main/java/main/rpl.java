package main;

import config.SpringContext;
import gui.LoginPanel.LoginPanel;
import gui.MainFrame.MainFrame;

public class rpl {

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inisialisasi Spring Container & koneksi Hibernate sebelum GUI tampil
        SpringContext.init();

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
