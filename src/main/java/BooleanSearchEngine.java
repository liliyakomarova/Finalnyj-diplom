import java.io.File;
import java.io.IOException;
import java.util.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> result = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        for (var listOfFiles : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(listOfFiles));
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                String word = "";
                Map<String, Integer> freqs = new HashMap<>();
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");

                for (int j = 0; j < words.length; j++) {
                    word = words[j];
                    if (word.isEmpty()) {
                        continue;
                    } else {
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }
                }
                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    String key = entry.getKey();
                    String nameOfFile = String.valueOf(listOfFiles.getName());
                    if (result.containsKey(key)) {
                        List<PageEntry> search = new ArrayList<>();
                        search = result.get(key);
                        search.add(new PageEntry(nameOfFile, i, freqs.get(key)));
                        Collections.sort(search, Comparator.comparing(PageEntry::getCount).reversed());
                        result.put(key, search);
                    } else {
                        List<PageEntry> search = new ArrayList<>();
                        search.add(new PageEntry(nameOfFile, i, freqs.get(key)));
                        Collections.sort(search, Comparator.comparing(PageEntry::getCount).reversed());
                        result.put(key, search);
                    }
                }
            }
        }
    }
    @Override
    public List<PageEntry> search(String word) {
        return result.get(word);
    }
}
