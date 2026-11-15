package tr.quester;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CatalogTest {
    @Test
    void catalogLoadsAndContainsExpectedGames() throws Exception {
        List<Game> games = Catalog.load();
        assertFalse(games.isEmpty());
        assertTrue(games.stream().anyMatch(g -> g.name().equals("Minecraft")));
    }

    @Test
    void searchFindsGameWithoutCaseSensitivity() {
        Game game = new Game("Cyberpunk 2077", "Aksiyon", "Steam", "https://example.com/game");
        assertTrue(game.matches("cyberpunk"));
        assertTrue(game.matches("AKSİYON"));
        assertFalse(game.matches("olmayan oyun"));
    }

    @Test
    void rejectsNonHttpsLinks() {
        assertThrows(IllegalArgumentException.class,
                () -> new Game("Test", "Test", "Test", "http://example.com"));
    }
}
