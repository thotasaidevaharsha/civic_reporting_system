import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public final class UIStyle {
    public static final Color BACKGROUND = new Color(247, 250, 252);
    public static final Color BACKGROUND_ALT = new Color(238, 244, 248);
    public static final Color SURFACE = new Color(255, 255, 255);
    public static final Color SURFACE_ELEVATED = new Color(250, 252, 255);
    public static final Color PRIMARY = new Color(31, 111, 235);
    public static final Color PRIMARY_ALT = new Color(20, 148, 132);
    public static final Color SUCCESS = new Color(29, 179, 123);
    public static final Color WARNING = new Color(227, 157, 54);
    public static final Color DANGER = new Color(220, 85, 85);
    public static final Color TEXT = new Color(26, 38, 52);
    public static final Color MUTED_TEXT = new Color(102, 119, 135);
    public static final Color BORDER = new Color(217, 226, 236);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 19);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private UIStyle() {
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(141, 184, 248)),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(SURFACE_ELEVATED);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );
    }

    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(SURFACE);
        panel.setBorder(cardBorder());
        return panel;
    }

    public static void setBodyFont(JComponent component) {
        component.setFont(BODY_FONT);
        component.setForeground(TEXT);
    }

    public static void styleInput(JComponent component) {
        component.setFont(BODY_FONT);
        component.setForeground(TEXT);
        component.setBackground(SURFACE_ELEVATED);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }
}
