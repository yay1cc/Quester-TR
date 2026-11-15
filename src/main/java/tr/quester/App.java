package tr.quester;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class App {
    private static final Color BACKGROUND = new Color(18, 20, 27);
    private static final Color PANEL = new Color(29, 32, 42);
    private static final Color ACCENT = new Color(90, 170, 255);
    private final JFrame frame = new JFrame("Quester-TR");
    private final DefaultListModel<Game> listModel = new DefaultListModel<>();
    private final JList<Game> gameList = new JList<>(listModel);
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JLabel countLabel = new JLabel("Katalog yükleniyor...");
    private List<Game> allGames = List.of();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().show());
    }

    private void show() {
        try {
            allGames = Catalog.load();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Oyun kataloğu yüklenemedi:\n" + ex.getMessage(),
                    "Quester-TR - Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(760, 520));
        frame.setSize(900, 620);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND);
        frame.setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel("QUESTER-TR");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Hafif oyun kütüphanen");
        subtitle.setForeground(new Color(175, 185, 200));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBorder(new EmptyBorder(18, 20, 4, 20));
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        JPanel controls = new JPanel(new BorderLayout(10, 0));
        controls.setOpaque(false);
        controls.setBorder(new EmptyBorder(0, 20, 8, 20));
        searchField.setToolTipText("Oyun adı veya kategori ara");
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(65, 72, 88)),
                new EmptyBorder(9, 10, 9, 10)));
        categoryBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        categoryBox.addItem("Tüm kategoriler");
        allGames.stream().map(Game::category).distinct().sorted().forEach(categoryBox::addItem);
        controls.add(searchField, BorderLayout.CENTER);
        controls.add(categoryBox, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(controls, BorderLayout.SOUTH);
        frame.add(top, BorderLayout.NORTH);

        gameList.setBackground(PANEL);
        gameList.setForeground(Color.WHITE);
        gameList.setSelectionBackground(new Color(49, 83, 120));
        gameList.setSelectionForeground(Color.WHITE);
        gameList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gameList.setFixedCellHeight(36);
        gameList.setBorder(new EmptyBorder(6, 8, 6, 8));
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gameList.setCellRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value.name());
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(4, 10, 4, 10));
            label.setBackground(selected ? list.getSelectionBackground() : PANEL);
            label.setForeground(Color.WHITE);
            label.setFont(list.getFont());
            return label;
        });

        JScrollPane scroll = new JScrollPane(gameList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(48, 53, 66)));
        scroll.getViewport().setBackground(PANEL);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 20, 0, 20));
        center.add(scroll, BorderLayout.CENTER);
        frame.add(center, BorderLayout.CENTER);

        JButton openButton = new JButton("Resmî sayfayı aç");
        openButton.setBackground(ACCENT);
        openButton.setForeground(Color.BLACK);
        openButton.setFocusPainted(false);
        openButton.addActionListener(this::openSelectedGame);
        countLabel.setForeground(new Color(190, 200, 215));
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 20, 16, 20));
        footer.add(countLabel, BorderLayout.CENTER);
        footer.add(openButton, BorderLayout.EAST);
        frame.add(footer, BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterGames(); }
            public void removeUpdate(DocumentEvent e) { filterGames(); }
            public void changedUpdate(DocumentEvent e) { filterGames(); }
        });
        categoryBox.addActionListener(e -> filterGames());
        gameList.addListSelectionListener(e -> updateCount());
        filterGames();
        frame.setVisible(true);
    }

    private void filterGames() {
        String query = searchField.getText();
        Object selected = categoryBox.getSelectedItem();
        String category = Objects.toString(selected, "Tüm kategoriler");
        listModel.clear();
        for (Game game : allGames) {
            boolean categoryMatches = category.equals("Tüm kategoriler") || game.category().equals(category);
            if (categoryMatches && game.matches(query)) listModel.addElement(game);
        }
        updateCount();
    }

    private void updateCount() {
        countLabel.setText("Gösterilen: " + listModel.size() + " / Katalog: " + allGames.size()
                + " oyun  •  Görsel kullanılmaz, az yer kaplar.");
    }

    private void openSelectedGame(ActionEvent event) {
        Game selected = gameList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(frame, "Önce listeden bir oyun seç.",
                    "Oyun seçilmedi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            JOptionPane.showMessageDialog(frame, "Bu bilgisayarda bağlantı açma özelliği desteklenmiyor.",
                    "Desteklenmiyor", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Desktop.getDesktop().browse(URI.create(selected.url()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Resmî sayfa açılamadı: " + ex.getMessage(),
                    "Bağlantı hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
}
