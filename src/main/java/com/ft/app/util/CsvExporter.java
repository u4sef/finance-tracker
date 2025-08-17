package com.ft.app.util;

import com.ft.app.data.dao.TxDao;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvExporter {
    private CsvExporter() {}

    public static void exportTransactions(Path path, List<TxDao.TxRow> rows) throws Exception {
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // header
            w.write("id,date,payee,account,category,amount_cad,note");
            w.newLine();

            for (var r : rows) {
                String amount = String.format(java.util.Locale.CANADA, "%.2f", r.amountCents() / 100.0);
                writeRow(w, new String[] {
                        String.valueOf(r.id()),
                        safe(r.dt()),
                        safe(r.payee()),
                        safe(r.accountName()),
                        safe(r.categoryName()),
                        amount,
                        safe(r.note())
                });
            }
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static void writeRow(BufferedWriter w, String[] cols) throws Exception {
        for (int i = 0; i < cols.length; i++) {
            String v = cols[i];
            boolean needsQuotes = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
            if (needsQuotes) v = "\"" + v.replace("\"", "\"\"") + "\"";
            w.write(v);
            if (i < cols.length - 1) w.write(',');
        }
        w.newLine();
    }
}
