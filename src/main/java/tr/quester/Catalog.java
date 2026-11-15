package tr.quester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Catalog {
    private Catalog() {}

    public static List<Game> load() throws IOException {
        InputStream stream = Catalog.class.getResourceAsStream("/games.csv");
        if (stream == null) throw new IOException("Oyun kataloğu (games.csv) bulunamadı.");
        List<Game> games = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.startsWith("#") || lineNumber == 1) continue;
                String[] parts = line.split(";", -1);
                if (parts.length != 4) throw new IOException("Katalog satırı hatalı: " + lineNumber);
                try {
                    games.add(new Game(parts[0], parts[1], parts[2], parts[3]));
                } catch (RuntimeException ex) {
                    throw new IOException("Katalog satırı geçersiz: " + lineNumber, ex);
                }
            }
        }
        games.sort(Comparator.comparing(Game::name, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(games);
    }
}
