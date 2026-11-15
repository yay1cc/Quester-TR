package tr.quester;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;

public record Game(String name, String category, String platform, String url) {
    private static final Locale TURKISH = Locale.forLanguageTag("tr-TR");

    public Game {
        name = Objects.requireNonNull(name, "Oyun adı").strip();
        category = Objects.requireNonNull(category, "Kategori").strip();
        platform = Objects.requireNonNull(platform, "Platform").strip();
        url = Objects.requireNonNull(url, "Bağlantı").strip();
        if (name.isEmpty()) throw new IllegalArgumentException("Oyun adı boş olamaz.");
        URI parsed = URI.create(url);
        if (!"https".equalsIgnoreCase(parsed.getScheme()) || parsed.getHost() == null) {
            throw new IllegalArgumentException("Yalnızca güvenli HTTPS bağlantıları kabul edilir.");
        }
    }

    public boolean matches(String query) {
        String q = query == null ? "" : query.strip().toLowerCase(TURKISH);
        return name.toLowerCase(TURKISH).contains(q)
                || category.toLowerCase(TURKISH).contains(q)
                || platform.toLowerCase(TURKISH).contains(q);
    }
}
