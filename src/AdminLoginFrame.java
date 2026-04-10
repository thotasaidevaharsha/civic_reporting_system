import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class AdminLoginFrame extends JPanel {
    private final MainFrame mainFrame;
    private final JComboBox<String> zoneBox = new JComboBox<>(DataStore.ZONES.toArray(new String[0]));
    private final JPasswordField passwordField = new JPasswordField();

    public AdminLoginFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        UIStyle.styleInput(zoneBox);
        UIStyle.styleInput(passwordField);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    public void resetForm() {
        zoneBox.setSelectedIndex(0);
        passwordField.setText("");
    }

    private JPanel buildHeader() {
        JPanel header = UIStyle.createCard();
        header.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admin Control Room Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(UIStyle.TEXT);

        JLabel subtitle = new JLabel("Open the admin workspace in the same window and manage the complaint queue.");
        subtitle.setFont(UIStyle.SUBTITLE_FONT);
        subtitle.setForeground(UIStyle.MUTED_TEXT);

        text.add(title);
        text.add(Box.createVerticalStrut(8));
        text.add(subtitle);

        JButton backHome = new JButton("Back To Home");
        UIStyle.styleSecondaryButton(backHome);
        backHome.addActionListener(e -> mainFrame.showHome());

        header.add(text, BorderLayout.WEST);
        header.add(backHome, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel root = new JPanel(new GridLayout(1, 2, 18, 18));
        root.setOpaque(false);

        JPanel loginCard = UIStyle.createCard();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));

        JButton loginButton = new JButton("Open Admin Panel");
        UIStyle.stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> login());

        loginCard.add(sectionTitle("Login Credentials"));
        loginCard.add(Box.createVerticalStrut(16));
        loginCard.add(label("Area / Zone"));
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(zoneBox);
        loginCard.add(Box.createVerticalStrut(14));
        loginCard.add(label("Password"));
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(18));
        loginCard.add(loginButton);

        JPanel side = new JPanel(new GridLayout(3, 1, 14, 14));
        side.setOpaque(false);
        side.add(infoCard("Zone Access", "Admins authenticate by area or zone before using the queue.", new Color(231, 242, 255)));
        side.add(infoCard("Demo Passwords", "north@123, south@123, east@123, west@123, central@123", new Color(237, 251, 246)));
        side.add(infoCard("Desktop View", "Login is arranged for a fixed desktop workspace.", new Color(255, 246, 232)));

        root.add(loginCard);
        root.add(side);
        return root;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.SECTION_FONT);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.BODY_FONT);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private JPanel infoCard(String titleText, String bodyText, Color bg) {
        JPanel card = new JPanel();
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(UIStyle.TEXT);

        JLabel body = new JLabel("<html><div style='width:220px'>" + bodyText + "</div></html>");
        body.setFont(UIStyle.BODY_FONT);
        body.setForeground(UIStyle.MUTED_TEXT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(body);
        return card;
    }

    private void login() {
        AdminAccount account = DataStore.authenticateAdmin(zoneBox.getSelectedItem().toString(), new String(passwordField.getPassword()));
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Invalid zone or password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainFrame.showAdminDashboard(account);
    }
}
