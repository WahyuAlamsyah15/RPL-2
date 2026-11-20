/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.AdminPanel;

import entity.BahanBaku;
import entity.Pesanan;
import entity.Produk;
import entity.Resep;
import service.BahanBakuService;
import service.PesananService;
import service.ProdukService;
import service.ResepService;
import service.ProdukReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

/** Dashboard utama admin toko kue. Seluruh komponen dibuat native Swing agar mudah dipaketkan sebagai desktop app. */
public class AdminPanel extends JPanel {
    private static final Color NAVY = new Color(33, 38, 74);
    private static final Color PURPLE = new Color(116, 91, 205);
    private static final Color CREAM = new Color(250, 248, 245);
    private static final Color TEXT = new Color(47, 47, 62);
    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final ProdukService produkService = config.SpringContext.getBean(ProdukService.class);
    private final BahanBakuService bahanBakuService = config.SpringContext.getBean(BahanBakuService.class);
    private final ResepService resepService = config.SpringContext.getBean(ResepService.class);
    private final PesananService pesananService = config.SpringContext.getBean(PesananService.class);
    private final ProdukReportService reportService = config.SpringContext.getBean(ProdukReportService.class);
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Produk", "Harga", "Stok", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable productTable = new JTable(tableModel);
    private final JTextField searchField = new JTextField();
    private final JTextField bahanSearchField = new JTextField();
    private final JTextField createProductName = new JTextField();
    private final JTextField createProductPrice = new JTextField();
    private final JTextField createProductStock = new JTextField();
    private final JTextField createBahanName = new JTextField();
    private final JTextField createBahanUnit = new JTextField();
    private final JTextField createBahanPrice = new JTextField();
    private final JTextField createBahanStock = new JTextField();
    private final JTextField createBahanMinimum = new JTextField("5");
    private final DefaultTableModel bahanTableModel = new DefaultTableModel(new Object[]{"ID", "Nama Bahan", "Satuan", "Harga Beli", "Stok", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable bahanTable = new JTable(bahanTableModel);
    private final DefaultTableModel resepTableModel = new DefaultTableModel(new Object[]{"ID", "Produk", "Bahan Baku", "Kebutuhan / Produk"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    private final JTable resepTable = new JTable(resepTableModel);
    private final DefaultTableModel orderTableModel = new DefaultTableModel(new Object[]{"ID", "Tanggal", "Pelanggan", "Produk", "Jumlah", "Total"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    private final JTable orderTable = new JTable(orderTableModel);
    private final JComboBox<String> recipeProductBox = new JComboBox<>();
    private final JComboBox<String> recipeMaterialBox = new JComboBox<>();
    private final JTextField recipeAmount = new JTextField();
    private final JComboBox<String> productionProductBox = new JComboBox<>();
    private final JTextField productionAmount = new JTextField();
    private final JComboBox<String> orderProductBox = new JComboBox<>();
    private final JTextField orderCustomer = new JTextField();
    private final JTextField orderAmount = new JTextField();
    private final JLabel productCount = new JLabel("0 produk");
    private final JLabel stockCount = new JLabel("0");
    private final JLabel lowStockCount = new JLabel("0");
    private final JLabel inventoryValue = new JLabel("Rp0");
    private final ChartPanel chartPanel = new ChartPanel();
    private final List<JButton> sidebarButtons = new ArrayList<>();

    public AdminPanel() {
        setBackground(CREAM);
        setPreferredSize(new Dimension(1280, 760));
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildWorkspace(), BorderLayout.CENTER);
        loadProducts();
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(236, 760));
        side.setBackground(new Color(244, 243, 248));
        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(true);
        brand.setBackground(NAVY);
        brand.setBorder(new EmptyBorder(28, 24, 28, 18));
        JLabel icon = new JLabel("✦", SwingConstants.CENTER);
        icon.setOpaque(true); icon.setBackground(new Color(247, 199, 99)); icon.setForeground(NAVY);
        icon.setFont(new Font("SansSerif", Font.BOLD, 22)); icon.setPreferredSize(new Dimension(38, 38));
        JLabel name = label("TOKO\nKUE", 19, Color.WHITE, true);
        name.setText("TOKOKUE");
        brand.add(icon, BorderLayout.WEST); brand.add(name, BorderLayout.CENTER);
        side.add(brand, BorderLayout.NORTH);

        JPanel menu = new JPanel(); menu.setOpaque(false); menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(12, 14, 0, 14));
        menu.add(section("MENU UTAMA"));
        menu.add(navButton("▦", "Dashboard", true, () -> contentLayout.show(content, "dashboard")));
        menu.add(navButton("▤", "Produk", false, () -> contentLayout.show(content, "products")));
        menu.add(navButton("▧", "Bahan Baku", false, () -> { loadBahanBaku(); contentLayout.show(content, "materials"); }));
        menu.add(navButton("♨", "Resep & Produksi", false, () -> { loadResep(); contentLayout.show(content, "recipes"); }));
        menu.add(navButton("▣", "Pesanan", false, () -> { loadOrders(); contentLayout.show(content, "orders"); }));
        menu.add(navButton("▥", "Laporan", false, () -> showReportInfo()));
        menu.add(Box.createVerticalStrut(22)); menu.add(section("PENGATURAN"));
        menu.add(navButton("⚙", "Pengaturan", false, () -> JOptionPane.showMessageDialog(this, "Pengaturan toko dapat ditambahkan pada modul konfigurasi.", "Pengaturan", JOptionPane.INFORMATION_MESSAGE)));
        side.add(menu, BorderLayout.CENTER);
        JPanel user = new JPanel(new BorderLayout(10, 0)); user.setOpaque(false); user.setBorder(new EmptyBorder(18, 18, 20, 18));
        JLabel avatar = new JLabel("A", SwingConstants.CENTER); avatar.setOpaque(true); avatar.setBackground(new Color(230, 222, 255)); avatar.setForeground(PURPLE); avatar.setFont(new Font("SansSerif", Font.BOLD, 16)); avatar.setPreferredSize(new Dimension(36,36));
        JLabel admin = label("Admin Toko\nOnline", 12, TEXT, false); admin.setText("Admin Toko   • Online");
        user.add(avatar, BorderLayout.WEST); user.add(admin, BorderLayout.CENTER); side.add(user, BorderLayout.SOUTH);
        return side;
    }

    private JPanel buildWorkspace() {
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(CREAM);
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(Color.WHITE); header.setBorder(new EmptyBorder(24, 30, 22, 30));
        JPanel title = new JPanel(); title.setOpaque(false); title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.add(label("Dashboard Admin", 26, TEXT, true)); title.add(Box.createVerticalStrut(5)); title.add(label("Kelola toko kue dengan lebih mudah hari ini.", 13, new Color(130,130,145), false));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4)); actions.setOpaque(false);
        JButton refresh = button("↻  Refresh", Color.WHITE, TEXT); refresh.addActionListener(e -> loadProducts());
        JButton report = button("▣  Cetak Laporan", PURPLE, Color.WHITE); report.addActionListener(e -> showReportInfo());
        actions.add(refresh); actions.add(report); header.add(title, BorderLayout.WEST); header.add(actions, BorderLayout.EAST); root.add(header, BorderLayout.NORTH);
        content.setBackground(CREAM); content.setBorder(new EmptyBorder(25, 30, 30, 30));
        content.add(buildDashboard(), "dashboard"); content.add(buildProducts(), "products"); content.add(buildBahanBaku(), "materials"); content.add(buildRecipes(), "recipes"); content.add(buildOrders(), "orders"); root.add(content, BorderLayout.CENTER); return root;
    }

    private JPanel buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 22)); panel.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0)); stats.setOpaque(false);
        stats.add(statCard("TOTAL PRODUK", productCount, "Katalog aktif", new Color(238, 232, 255)));
        stats.add(statCard("TOTAL STOK", stockCount, "Siap dijual", new Color(224, 244, 235)));
        stats.add(statCard("STOK MENIPIS", lowStockCount, "Perlu diperhatikan", new Color(255, 239, 217)));
        JPanel lower = new JPanel(new GridLayout(1, 2, 18, 0)); lower.setOpaque(false); lower.add(chartPanel); lower.add(buildInsight()); panel.add(stats, BorderLayout.NORTH); panel.add(lower, BorderLayout.CENTER); return panel;
    }

    private JPanel buildInsight() {
        JPanel box = cardPanel(); box.setLayout(new BorderLayout(0, 12)); box.setBorder(new EmptyBorder(22, 22, 22, 22));
        box.add(label("Ringkasan Inventori", 17, TEXT, true), BorderLayout.NORTH);
        JPanel body = new JPanel(); body.setOpaque(false); body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(label("Nilai seluruh stok", 12, new Color(135,135,145), false)); body.add(Box.createVerticalStrut(5)); inventoryValue.setFont(new Font("SansSerif", Font.BOLD, 26)); inventoryValue.setForeground(PURPLE); body.add(inventoryValue); body.add(Box.createVerticalStrut(22));
        JLabel note = label("Tip hari ini", 12, new Color(135,135,145), false); body.add(note); body.add(Box.createVerticalStrut(6)); body.add(label("Pastikan stok kue dengan jumlah di bawah 5 segera diproduksi kembali.", 13, TEXT, false)); box.add(body, BorderLayout.CENTER); return box;
    }

    private JPanel buildProducts() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false); top.add(label("Produk", 22, TEXT, true), BorderLayout.NORTH); top.add(buildProductCreateForm(), BorderLayout.CENTER);
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); tools.setOpaque(false); searchField.setPreferredSize(new Dimension(230, 36)); searchField.putClientProperty("JTextField.placeholderText", "Cari nama produk...");
        searchField.addActionListener(e -> filterProducts()); JButton search = button("Cari", Color.WHITE, TEXT); search.addActionListener(e -> filterProducts()); JButton add = button("+  Form Dialog", Color.WHITE, TEXT); add.addActionListener(e -> productDialog(null)); JButton addStock = button("↓  Tambah Stok", new Color(224,244,235), new Color(47,145,88)); addStock.addActionListener(e -> tambahStokProdukSelected()); tools.add(searchField); tools.add(search); tools.add(addStock); tools.add(add); top.add(tools, BorderLayout.SOUTH); panel.add(top, BorderLayout.NORTH);
        productTable.setRowHeight(42); productTable.setShowGrid(false); productTable.setIntercellSpacing(new Dimension(0, 0)); productTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12)); productTable.getTableHeader().setBackground(new Color(245,243,249)); productTable.getTableHeader().setForeground(new Color(95,90,105));
        productTable.getColumnModel().getColumn(0).setPreferredWidth(45); productTable.getColumnModel().getColumn(2).setPreferredWidth(130); productTable.getColumnModel().getColumn(3).setPreferredWidth(70); productTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer()); productTable.getColumnModel().getColumn(3).setCellRenderer(new CenterRenderer()); productTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        productTable.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) editSelected(); }});
        JScrollPane scroll = new JScrollPane(productTable); scroll.setBorder(BorderFactory.createLineBorder(new Color(232,229,235))); scroll.getViewport().setBackground(Color.WHITE); panel.add(scroll, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout()); footer.setOpaque(false); footer.add(productCount, BorderLayout.WEST); JButton edit = button("Edit", Color.WHITE, TEXT); edit.addActionListener(e -> editSelected()); JButton delete = button("Hapus", new Color(255,235,235), new Color(185,65,65)); delete.addActionListener(e -> deleteSelected()); JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); right.setOpaque(false); right.add(edit); right.add(delete); footer.add(right, BorderLayout.EAST); panel.add(footer, BorderLayout.SOUTH); return panel;
    }

    private JPanel buildBahanBaku() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false); top.add(label("Bahan Baku", 22, TEXT, true), BorderLayout.NORTH); top.add(buildBahanCreateForm(), BorderLayout.CENTER);
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); tools.setOpaque(false);
        bahanSearchField.setPreferredSize(new Dimension(230, 36)); bahanSearchField.putClientProperty("JTextField.placeholderText", "Cari nama bahan...");
        bahanSearchField.addActionListener(e -> filterBahanBaku());
        JButton search = button("Cari", Color.WHITE, TEXT); search.addActionListener(e -> filterBahanBaku());
        JButton add = button("+  Tambah Bahan", PURPLE, Color.WHITE); add.addActionListener(e -> bahanDialog(null)); JButton addStock = button("↓  Tambah Stok", new Color(224,244,235), new Color(47,145,88)); addStock.addActionListener(e -> tambahStokBahanSelected());
        tools.add(bahanSearchField); tools.add(search); tools.add(addStock); tools.add(add); top.add(tools, BorderLayout.SOUTH); panel.add(top, BorderLayout.NORTH);
        bahanTable.setRowHeight(42); bahanTable.setShowGrid(false); bahanTable.setIntercellSpacing(new Dimension(0, 0)); bahanTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12)); bahanTable.getTableHeader().setBackground(new Color(245,243,249)); bahanTable.getTableHeader().setForeground(new Color(95,90,105));
        bahanTable.getColumnModel().getColumn(0).setPreferredWidth(45); bahanTable.getColumnModel().getColumn(2).setPreferredWidth(85); bahanTable.getColumnModel().getColumn(3).setPreferredWidth(130); bahanTable.getColumnModel().getColumn(4).setPreferredWidth(75); bahanTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        bahanTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer()); bahanTable.getColumnModel().getColumn(4).setCellRenderer(new CenterRenderer()); bahanTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        bahanTable.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) editBahanSelected(); }});
        JScrollPane scroll = new JScrollPane(bahanTable); scroll.setBorder(BorderFactory.createLineBorder(new Color(232,229,235))); scroll.getViewport().setBackground(Color.WHITE); panel.add(scroll, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout()); footer.setOpaque(false); JLabel count = new JLabel("Kelola stok bahan yang digunakan untuk produksi kue"); count.setForeground(new Color(125,122,140)); footer.add(count, BorderLayout.WEST);
        JButton edit = button("Edit", Color.WHITE, TEXT); edit.addActionListener(e -> editBahanSelected()); JButton delete = button("Hapus", new Color(255,235,235), new Color(185,65,65)); delete.addActionListener(e -> deleteBahanSelected()); JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); right.setOpaque(false); right.add(edit); right.add(delete); footer.add(right, BorderLayout.EAST); panel.add(footer, BorderLayout.SOUTH); return panel;
    }

    private JPanel buildRecipes() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false); top.add(label("Resep & Produksi", 22, TEXT, true), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 5)); form.setBackground(Color.WHITE); form.setBorder(new EmptyBorder(10, 12, 10, 12));
        form.add(label("Produk", 11, TEXT, true)); form.add(label("Bahan Baku", 11, TEXT, true)); form.add(label("Kebutuhan bahan / 1 produk", 11, TEXT, true)); form.add(new JLabel());
        form.add(recipeProductBox); form.add(recipeMaterialBox); form.add(recipeAmount); JButton save = button("Simpan Resep", PURPLE, Color.WHITE); save.addActionListener(e -> createRecipe()); form.add(save); top.add(form, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH); styleTable(resepTable); JScrollPane scroll = new JScrollPane(resepTable); scroll.setBorder(BorderFactory.createLineBorder(new Color(232,229,235))); panel.add(scroll, BorderLayout.CENTER);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); footer.setOpaque(false); footer.add(label("Produksi akan mengurangi bahan dan menambah stok produk.", 12, new Color(125,122,140), false)); footer.add(productionProductBox); productionAmount.setPreferredSize(new Dimension(80, 32)); footer.add(productionAmount); JButton produce = button("▶  Produksi", new Color(224,244,235), new Color(47,145,88)); produce.addActionListener(e -> produceProduct()); footer.add(produce); JButton delete = button("Hapus Resep", new Color(255,235,235), new Color(170,55,55)); delete.addActionListener(e -> deleteRecipe()); footer.add(delete); panel.add(footer, BorderLayout.SOUTH); return panel;
    }

    private JPanel buildOrders() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false); top.add(label("Pesanan / Stok Keluar", 22, TEXT, true), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 5)); form.setBackground(Color.WHITE); form.setBorder(new EmptyBorder(10, 12, 10, 12)); form.add(label("Nama Pelanggan", 11, TEXT, true)); form.add(label("Produk", 11, TEXT, true)); form.add(label("Jumlah Keluar", 11, TEXT, true)); form.add(new JLabel()); form.add(orderCustomer); form.add(orderProductBox); form.add(orderAmount); JButton save = button("Simpan Pesanan", PURPLE, Color.WHITE); save.addActionListener(e -> createOrder()); form.add(save); top.add(form, BorderLayout.CENTER); panel.add(top, BorderLayout.NORTH);
        styleTable(orderTable); JScrollPane scroll = new JScrollPane(orderTable); scroll.setBorder(BorderFactory.createLineBorder(new Color(232,229,235))); panel.add(scroll, BorderLayout.CENTER); JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT)); footer.setOpaque(false); JButton cancel = button("Batalkan Pesanan", new Color(255,235,235), new Color(170,55,55)); cancel.addActionListener(e -> deleteOrder()); footer.add(cancel); panel.add(footer, BorderLayout.SOUTH); return panel;
    }

    private void styleTable(JTable table) { table.setRowHeight(42); table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0)); table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12)); table.getTableHeader().setBackground(new Color(245,243,249)); table.getTableHeader().setForeground(new Color(95,90,105)); }

    private JPanel buildProductCreateForm() {
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE); form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(235,232,238)), new EmptyBorder(8, 10, 8, 10)));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(2, 4, 2, 4); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        c.gridx = 0; c.gridy = 0; form.add(label("Nama Produk", 11, TEXT, true), c); c.gridx = 1; form.add(label("Harga Jual (Rp)", 11, TEXT, true), c); c.gridx = 2; form.add(label("Stok Awal", 11, TEXT, true), c); c.gridx = 3; c.weightx = 0; form.add(new JLabel(), c);
        c.gridy = 1; c.gridx = 0; c.weightx = 1; form.add(createProductName, c); c.gridx = 1; form.add(createProductPrice, c); c.gridx = 2; form.add(createProductStock, c); c.gridx = 3; c.weightx = 0; JButton save = button("Simpan Produk", PURPLE, Color.WHITE); save.addActionListener(e -> createProductFromForm()); form.add(save, c); return form;
    }

    private JPanel buildBahanCreateForm() {
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE); form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(235,232,238)), new EmptyBorder(8, 10, 8, 10)));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(2, 4, 2, 4); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        String[] headers = {"Nama Bahan", "Satuan", "Harga Beli (Rp)", "Stok Awal", "Min. Stok"}; JTextField[] fields = {createBahanName, createBahanUnit, createBahanPrice, createBahanStock, createBahanMinimum};
        for (int i = 0; i < headers.length; i++) { c.gridx = i; c.gridy = 0; form.add(label(headers[i], 11, TEXT, true), c); c.gridy = 1; form.add(fields[i], c); }
        c.gridx = 5; c.gridy = 1; c.weightx = 0; JButton save = button("Simpan Bahan", PURPLE, Color.WHITE); save.addActionListener(e -> createBahanFromForm()); form.add(save, c); return form;
    }

    private void createProductFromForm() { try { produkService.create(createProductName.getText(), Integer.parseInt(createProductPrice.getText().trim()), Integer.parseInt(createProductStock.getText().trim())); createProductName.setText(""); createProductPrice.setText(""); createProductStock.setText(""); loadProducts(); JOptionPane.showMessageDialog(this, "Produk berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Harga dan stok awal harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }
    private void createBahanFromForm() { try { bahanBakuService.create(createBahanName.getText(), createBahanUnit.getText(), Integer.parseInt(createBahanPrice.getText().trim()), Integer.parseInt(createBahanStock.getText().trim()), Integer.parseInt(createBahanMinimum.getText().trim())); createBahanName.setText(""); createBahanUnit.setText(""); createBahanPrice.setText(""); createBahanStock.setText(""); createBahanMinimum.setText("5"); loadBahanBaku(); JOptionPane.showMessageDialog(this, "Bahan baku berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Harga, stok, dan minimum stok harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }

    private void refreshRecipeCombos() { try { recipeProductBox.removeAllItems(); productionProductBox.removeAllItems(); orderProductBox.removeAllItems(); for (Produk p : produkService.findAll()) { recipeProductBox.addItem(p.getId() + " - " + p.getName()); productionProductBox.addItem(p.getId() + " - " + p.getName()); orderProductBox.addItem(p.getId() + " - " + p.getName()); } recipeMaterialBox.removeAllItems(); for (BahanBaku b : bahanBakuService.findAll()) recipeMaterialBox.addItem(b.getId() + " - " + b.getName() + " (" + b.getSatuan() + ")"); } catch (RuntimeException ex) { showError(ex); } }
    private Integer selectedId(JComboBox<String> box) { Object value = box.getSelectedItem(); if (value == null) throw new IllegalArgumentException("Pilih data terlebih dahulu."); return Integer.parseInt(value.toString().split(" - ")[0]); }
    private void createRecipe() { try { resepService.create(selectedId(recipeProductBox), selectedId(recipeMaterialBox), Integer.parseInt(recipeAmount.getText().trim())); recipeAmount.setText(""); loadResep(); JOptionPane.showMessageDialog(this, "Resep bahan berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Kebutuhan bahan harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }
    private void produceProduct() { try { resepService.produksi(selectedId(productionProductBox), Integer.parseInt(productionAmount.getText().trim())); productionAmount.setText(""); loadResep(); loadProducts(); JOptionPane.showMessageDialog(this, "Produksi berhasil. Stok bahan berkurang dan stok produk bertambah.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Jumlah produksi harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }
    private void loadResep() { try { refreshRecipeCombos(); resepTableModel.setRowCount(0); for (Resep r : resepService.findAll()) resepTableModel.addRow(new Object[]{r.getId(), r.getProduk().getName(), r.getBahanBaku().getName(), r.getJumlahBahan() + " " + r.getBahanBaku().getSatuan()}); } catch (RuntimeException ex) { showError(ex); } }
    private void deleteRecipe() { int row = resepTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih resep yang ingin dihapus.", "Informasi", JOptionPane.INFORMATION_MESSAGE); return; } int id = (Integer) resepTableModel.getValueAt(resepTable.convertRowIndexToModel(row), 0); if (JOptionPane.showConfirmDialog(this, "Hapus resep ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { try { resepService.delete(id); loadResep(); } catch (RuntimeException ex) { showError(ex); } } }
    private void createOrder() { try { pesananService.create(selectedId(orderProductBox), orderCustomer.getText(), Integer.parseInt(orderAmount.getText().trim())); orderCustomer.setText(""); orderAmount.setText(""); loadOrders(); loadProducts(); JOptionPane.showMessageDialog(this, "Pesanan berhasil disimpan dan stok produk berkurang.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Jumlah pesanan harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }
    private void loadOrders() { try { refreshRecipeCombos(); orderTableModel.setRowCount(0); for (Pesanan p : pesananService.findAll()) orderTableModel.addRow(new Object[]{p.getId(), p.getTanggalPesanan(), p.getNamaPelanggan(), p.getProduk().getName(), p.getJumlah(), p.getTotalHarga()}); } catch (RuntimeException ex) { showError(ex); } }
    private void deleteOrder() { int row = orderTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih pesanan yang ingin dibatalkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE); return; } int id = (Integer) orderTableModel.getValueAt(orderTable.convertRowIndexToModel(row), 0); if (JOptionPane.showConfirmDialog(this, "Batalkan pesanan dan kembalikan stok?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { try { pesananService.delete(id); loadOrders(); loadProducts(); } catch (RuntimeException ex) { showError(ex); } } }

    private void loadProducts() { try { fillTable(produkService.findAll()); } catch (RuntimeException ex) { showError(ex); } }
    private void filterProducts() { String q = searchField.getText().trim().toLowerCase(); try { List<Produk> all = produkService.findAll(); fillTable(all.stream().filter(p -> p.getName().toLowerCase().contains(q)).toList()); } catch (RuntimeException ex) { showError(ex); } }
    private void fillTable(List<Produk> products) { tableModel.setRowCount(0); int stock=0, low=0; long value=0; for (Produk p: products) { String status=p.getJumlah() <= 5 ? "Menipis" : "Tersedia"; tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getHarga(), p.getJumlah(), status}); stock += p.getJumlah(); if (p.getJumlah() <= 5) low++; value += (long)p.getHarga()*p.getJumlah(); } productCount.setText(products.size()+" produk"); stockCount.setText(String.valueOf(stock)); lowStockCount.setText(String.valueOf(low)); inventoryValue.setText(formatCurrency(value)); chartPanel.setValues(stock, low); }

    private void productDialog(Produk selected) { JTextField name=new JTextField(selected==null?"":selected.getName()); JTextField price=new JTextField(selected==null?"":String.valueOf(selected.getHarga())); JTextField stock=new JTextField(selected==null?"":String.valueOf(selected.getJumlah())); JPanel form=new JPanel(new GridLayout(0,1,5,5)); form.add(new JLabel("Nama produk")); form.add(name); form.add(new JLabel("Harga (Rp)")); form.add(price); form.add(new JLabel("Jumlah stok")); form.add(stock); int result=JOptionPane.showConfirmDialog(this, form, selected==null?"Tambah Produk":"Edit Produk", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); if(result!=JOptionPane.OK_OPTION)return; try { int h=Integer.parseInt(price.getText().trim()); int s=Integer.parseInt(stock.getText().trim()); if(selected==null) produkService.create(name.getText(),h,s); else produkService.update(selected.getId(),name.getText(),h,s); loadProducts(); } catch(NumberFormatException ex) { JOptionPane.showMessageDialog(this,"Harga dan stok harus berupa angka.","Input tidak valid",JOptionPane.WARNING_MESSAGE); } catch(RuntimeException ex) { showError(ex); } }
    private void editSelected() { int row=productTable.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this,"Pilih produk yang ingin diedit.","Informasi",JOptionPane.INFORMATION_MESSAGE); return;} int id=(Integer)tableModel.getValueAt(productTable.convertRowIndexToModel(row),0); try { productDialog(produkService.findAll().stream().filter(p->p.getId().equals(id)).findFirst().orElse(null)); } catch(RuntimeException ex){showError(ex);} }
    private void deleteSelected() { int row=productTable.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this,"Pilih produk yang ingin dihapus.","Informasi",JOptionPane.INFORMATION_MESSAGE); return;} int id=(Integer)tableModel.getValueAt(productTable.convertRowIndexToModel(row),0); if(JOptionPane.showConfirmDialog(this,"Hapus produk ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){try{produkService.delete(id);loadProducts();}catch(RuntimeException ex){showError(ex);}} }
    private void tambahStokProdukSelected() { int row = productTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih produk yang ingin ditambah stoknya.", "Informasi", JOptionPane.INFORMATION_MESSAGE); return; } int modelRow = productTable.convertRowIndexToModel(row); int id = (Integer) tableModel.getValueAt(modelRow, 0); String nama = String.valueOf(tableModel.getValueAt(modelRow, 1)); String input = JOptionPane.showInputDialog(this, "Tambahan stok untuk " + nama + ":", "Tambah Stok Produk", JOptionPane.PLAIN_MESSAGE); if (input == null) return; try { produkService.tambahStok(id, Integer.parseInt(input.trim())); loadProducts(); JOptionPane.showMessageDialog(this, "Stok produk berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Jumlah stok harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }
    private void loadBahanBaku() { try { fillBahanTable(bahanBakuService.findAll()); } catch (RuntimeException ex) { showError(ex); } }
    private void filterBahanBaku() { String q = bahanSearchField.getText().trim().toLowerCase(); try { fillBahanTable(bahanBakuService.findAll().stream().filter(b -> b.getName().toLowerCase().contains(q)).toList()); } catch (RuntimeException ex) { showError(ex); } }
    private void fillBahanTable(List<BahanBaku> bahanList) { bahanTableModel.setRowCount(0); for (BahanBaku b : bahanList) { String status = b.getJumlah() <= b.getStokMinimum() ? "Menipis" : "Tersedia"; bahanTableModel.addRow(new Object[]{b.getId(), b.getName(), b.getSatuan(), b.getHargaBeli(), b.getJumlah() + " " + b.getSatuan(), status}); } }
    private void bahanDialog(BahanBaku selected) {
        JTextField name = new JTextField(selected == null ? "" : selected.getName()); JTextField unit = new JTextField(selected == null ? "" : selected.getSatuan()); JTextField price = new JTextField(selected == null ? "" : String.valueOf(selected.getHargaBeli())); JTextField stock = new JTextField(selected == null ? "" : String.valueOf(selected.getJumlah())); JTextField minimum = new JTextField(selected == null ? "5" : String.valueOf(selected.getStokMinimum()));
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5)); form.add(new JLabel("Nama bahan baku")); form.add(name); form.add(new JLabel("Satuan (kg, liter, pcs, dll.)")); form.add(unit); form.add(new JLabel("Harga beli (Rp)")); form.add(price); form.add(new JLabel("Jumlah stok")); form.add(stock); form.add(new JLabel("Batas minimum stok")); form.add(minimum);
        int result = JOptionPane.showConfirmDialog(this, form, selected == null ? "Tambah Bahan Baku" : "Edit Bahan Baku", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); if (result != JOptionPane.OK_OPTION) return;
        try { int h = Integer.parseInt(price.getText().trim()); int s = Integer.parseInt(stock.getText().trim()); int m = Integer.parseInt(minimum.getText().trim()); if (selected == null) bahanBakuService.create(name.getText(), unit.getText(), h, s, m); else bahanBakuService.update(selected.getId(), name.getText(), unit.getText(), h, s, m); loadBahanBaku(); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Harga, stok, dan batas minimum harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); }
    }
    private void editBahanSelected() { int row = bahanTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih bahan baku yang ingin diedit.", "Informasi", JOptionPane.INFORMATION_MESSAGE); return; } int id = (Integer) bahanTableModel.getValueAt(bahanTable.convertRowIndexToModel(row), 0); try { bahanDialog(bahanBakuService.findAll().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null)); } catch (RuntimeException ex) { showError(ex); } }
    private void deleteBahanSelected() { int row = bahanTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih bahan baku yang ingin dihapus.", "Informasi", JOptionPane.INFORMATION_MESSAGE); return; } int id = (Integer) bahanTableModel.getValueAt(bahanTable.convertRowIndexToModel(row), 0); if (JOptionPane.showConfirmDialog(this, "Hapus bahan baku ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { try { bahanBakuService.delete(id); loadBahanBaku(); } catch (RuntimeException ex) { showError(ex); } } }
    private void tambahStokBahanSelected() { int row = bahanTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih bahan baku yang ingin ditambah stoknya.", "Informasi", JOptionPane.INFORMATION_MESSAGE); return; } int modelRow = bahanTable.convertRowIndexToModel(row); int id = (Integer) bahanTableModel.getValueAt(modelRow, 0); String nama = String.valueOf(bahanTableModel.getValueAt(modelRow, 1)); JTextField jumlah = new JTextField(); JTextField harga = new JTextField(); JPanel form = new JPanel(new GridLayout(0, 1, 5, 5)); form.add(new JLabel("Bahan: " + nama)); form.add(new JLabel("Tambahan jumlah")); form.add(jumlah); form.add(new JLabel("Tambahan harga beli (Rp)")); form.add(harga); int result = JOptionPane.showConfirmDialog(this, form, "Tambah Stok Bahan Baku", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); if (result != JOptionPane.OK_OPTION) return; try { bahanBakuService.tambahStok(id, Integer.parseInt(jumlah.getText().trim()), Integer.parseInt(harga.getText().trim())); loadBahanBaku(); JOptionPane.showMessageDialog(this, "Stok bahan baku berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka.", "Input tidak valid", JOptionPane.WARNING_MESSAGE); } catch (RuntimeException ex) { showError(ex); } }
    private void showReportInfo() { try { reportService.preview(); } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Laporan belum dapat dibuka:\n"+ex.getMessage(),"Laporan",JOptionPane.WARNING_MESSAGE); } }
    private void showError(RuntimeException ex) { JOptionPane.showMessageDialog(this,"Tidak dapat memuat data:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    private static String formatCurrency(long value) { return RUPIAH.format(value).replace(",00", ""); }
    private static JLabel label(String text,int size,Color color,boolean bold){ JLabel l=new JLabel(text); l.setFont(new Font("SansSerif",bold?Font.BOLD:Font.PLAIN,size)); l.setForeground(color); return l; }
    private static JPanel cardPanel(){ JPanel p=new JPanel(); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createLineBorder(new Color(235,232,238))); return p; }
    private static JButton button(String text,Color bg,Color fg){ JButton b=new JButton(text); b.setFont(new Font("SansSerif",Font.BOLD,12)); b.setBackground(bg); b.setForeground(fg); b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false); b.setFocusPainted(false); b.setRolloverEnabled(false); b.setBorder(new EmptyBorder(10,15,10,15)); return b; }
    private static JLabel section(String text){ JLabel l=label(text,10,new Color(166,169,194),true); l.setBorder(new EmptyBorder(10,12,10,8)); return l; }
    private JButton navButton(String icon,String text,boolean active,Runnable action){ JButton b=new JButton(icon+"   "+text); b.setHorizontalAlignment(SwingConstants.LEFT); b.setFont(new Font("SansSerif",Font.BOLD,13)); b.setForeground(new Color(42,45,72)); b.setBackground(active?new Color(220,211,252):new Color(244,243,248)); b.setBorder(new EmptyBorder(11,14,11,8)); b.setFocusPainted(false); b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false); b.setRolloverEnabled(false); sidebarButtons.add(b); b.addActionListener(e->{ setActiveNav(b); action.run(); }); return b; }
    private void setActiveNav(JButton active) { for (JButton button : sidebarButtons) button.setBackground(button == active ? new Color(220,211,252) : new Color(244,243,248)); }
    private JPanel statCard(String title,JLabel value,String sub,Color tint){ JPanel p=cardPanel(); p.setBorder(new EmptyBorder(17,20,17,20)); p.setLayout(new BorderLayout()); JPanel left=new JPanel();left.setOpaque(false);left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));left.add(label(title,11,new Color(125,122,140),true)); left.add(Box.createVerticalStrut(8)); value.setFont(new Font("SansSerif",Font.BOLD,27)); value.setForeground(TEXT); left.add(value); left.add(Box.createVerticalStrut(4)); left.add(label(sub,12,new Color(145,142,155),false)); JLabel circle=new JLabel("●",SwingConstants.CENTER);circle.setOpaque(true);circle.setBackground(tint);circle.setForeground(PURPLE);circle.setFont(new Font("SansSerif",Font.BOLD,20));circle.setPreferredSize(new Dimension(48,48));p.add(left,BorderLayout.CENTER);p.add(circle,BorderLayout.EAST);return p; }

    private class ChartPanel extends JPanel { int stock,low; ChartPanel(){setBackground(Color.WHITE);setBorder(new EmptyBorder(20,20,15,20));} void setValues(int s,int l){stock=s;low=l;repaint();} protected void paintComponent(Graphics g){super.paintComponent(g);Graphics2D x=(Graphics2D)g.create();x.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);x.setColor(TEXT);x.setFont(new Font("SansSerif",Font.BOLD,17));x.drawString("Status Stok",20,28);x.setColor(new Color(135,135,145));x.setFont(new Font("SansSerif",Font.PLAIN,12));x.drawString("Perbandingan stok tersedia dan menipis",20,48);int base=getHeight()-42;int max=Math.max(stock,low);int h1=max==0?0:(getHeight()-105)*stock/max;int h2=max==0?0:(getHeight()-105)*low/max;x.setColor(new Color(223,215,255));x.fillRoundRect(70,base-h1,70,h1,14,14);x.setColor(new Color(255,209,143));x.fillRoundRect(180,base-h2,70,h2,14,14);x.setColor(new Color(110,108,120));x.drawString("Tersedia",65,base+22);x.drawString("Menipis",182,base+22);x.dispose();} }
    private static class CurrencyRenderer extends DefaultTableCellRenderer { public void setValue(Object v){setText(formatCurrency(((Number)v).longValue()));setHorizontalAlignment(RIGHT);} }
    private static class CenterRenderer extends DefaultTableCellRenderer { CenterRenderer(){setHorizontalAlignment(CENTER);} }
    private static class StatusRenderer extends DefaultTableCellRenderer { public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);l.setHorizontalAlignment(CENTER);l.setForeground("Menipis".equals(v)?new Color(191,125,35):new Color(47,145,88));return l;} }
}
