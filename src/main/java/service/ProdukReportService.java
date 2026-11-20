package service;

import entity.Pesanan;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapter laporan terpisah sehingga UI tidak bergantung pada detail Jasper. */
@Service
public class ProdukReportService {
    private final ProdukService produkService;
    private final BahanBakuService bahanBakuService;
    private final PesananService pesananService;

    public ProdukReportService(ProdukService produkService,
                               BahanBakuService bahanBakuService,
                               PesananService pesananService) {
        this.produkService = produkService;
        this.bahanBakuService = bahanBakuService;
        this.pesananService = pesananService;
    }

    // ==================== 1. LAPORAN PRODUK ====================
    public void preview() throws JRException {
        JasperViewer.viewReport(fillGabungan(), false);
    }

    public Path exportPdf() throws Exception {
        return exportToFile(fillGabungan(), "laporan-produk.pdf");
    }

    // ==================== 2. LAPORAN GABUNGAN (BAHAN BAKU & PESANAN) ====================
    public void previewGabungan() throws JRException {
        JasperViewer.viewReport(fillGabungan(), false);
    }

    public Path exportGabunganPdf() throws Exception {
        return exportToFile(fillGabungan(), "laporan-bahan-baku-dan-pesanan.pdf");
    }

    // ==================== HELPER INTERNAL ====================
    private JasperPrint fillGabungan() throws JRException {
        try (InputStream template = getClass().getResourceAsStream("/report/laporan_gabungan.jrxml")) {
            if (template == null) {
                throw new JRException("Template laporan gabungan belum tersedia.");
            }
            JasperReport report = JasperCompileManager.compileReport(template);

            Collection<?> bahanBakuList = bahanBakuService.findAll();
            Collection<Pesanan> pesananList = pesananService.findAll();

            List<Map<String, ?>> pesananRows = new ArrayList<>();
            for (Pesanan p : pesananList) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", p.getId());
                row.put("namaPelanggan", p.getNamaPelanggan());
                row.put("produk", p.getProduk() != null ? p.getProduk().getName() : "-");
                row.put("jumlah", p.getJumlah());
                row.put("totalHarga", p.getTotalHarga());
                row.put("tanggalPesanan", p.getTanggalPesanan());
                pesananRows.add(row);
            }

            Map<String, Object> params = new HashMap<>();
            params.put("DATASET_BAHAN_BAKU", new JRBeanCollectionDataSource(bahanBakuList));
            params.put("DATASET_PESANAN", new JRMapCollectionDataSource(pesananRows));

            return JasperFillManager.fillReport(report, params, new JREmptyDataSource(1));
        } catch (IOException ex) {
            throw new JRException("Template laporan tidak dapat dibaca.", ex);
        }
    }

    private Path exportToFile(JasperPrint print, String fileName) throws Exception {
        Path outputDir = Paths.get("reports");
        Files.createDirectories(outputDir);
        Path output = outputDir.resolve(fileName);
        JasperExportManager.exportReportToPdfFile(print, output.toString());
        return output.toAbsolutePath();
    }
}
