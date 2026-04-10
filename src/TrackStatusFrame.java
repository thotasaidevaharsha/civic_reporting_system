import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class TrackStatusFrame extends JPanel {
    private final MainFrame mainFrame;
    private final JTextField idField = new JTextField();
    private final JTextArea resultArea = new JTextArea(11, 24);

    public TrackStatusFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        UIStyle.styleInput(idField);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        UIStyle.styleInput(resultArea);
        resultArea.setBackground(UIStyle.BACKGROUND_ALT);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    public void resetView() {
        idField.setText("");
        resultArea.setText("");
    }

    private JPanel buildHeader() {
        JPanel header = UIStyle.createCard();
        header.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Track a Registered Complaint");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(UIStyle.TEXT);

        JLabel subtitle = new JLabel("Search by complaint ID and review status without leaving the main window.");
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

        JPanel lookupCard = UIStyle.createCard();
        lookupCard.setLayout(new BoxLayout(lookupCard, BoxLayout.Y_AXIS));

        JLabel section = new JLabel("Find Complaint");
        section.setFont(UIStyle.SECTION_FONT);
        section.setForeground(UIStyle.TEXT);

        JButton checkButton = new JButton("Check Status");
        UIStyle.stylePrimaryButton(checkButton);
        checkButton.addActionListener(e -> lookupComplaint());

        lookupCard.add(section);
        lookupCard.add(Box.createVerticalStrut(10));
        lookupCard.add(label("Complaint ID"));
        lookupCard.add(Box.createVerticalStrut(6));
        lookupCard.add(idField);
        lookupCard.add(Box.createVerticalStrut(14));
        lookupCard.add(checkButton);
        lookupCard.add(Box.createVerticalStrut(18));
        lookupCard.add(resultArea);

        JPanel side = new JPanel(new GridLayout(3, 1, 14, 14));
        side.setOpaque(false);
        side.add(infoCard("Fast Lookup", "Use either 1001 or COMPLAINT-1001.", new Color(231, 242, 255)));
        side.add(infoCard("Same Window", "Tracking stays in the current workspace with back navigation.", new Color(237, 251, 246)));
        side.add(infoCard("Desktop Fit", "The lookup screen stays visible without needing a full-screen layout.", new Color(255, 246, 232)));

        root.add(lookupCard);
        root.add(side);
        return root;
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

    private void lookupComplaint() {
        try {
            String digitsOnly = idField.getText().trim().replaceAll("[^0-9]", "");
            if (digitsOnly.isEmpty()) {
                throw new NumberFormatException();
            }

            Complaint complaint = DataStore.findComplaint(Integer.parseInt(digitsOnly));
            if (complaint == null) {
                JOptionPane.showMessageDialog(this, "Complaint ID not found.", "Lookup Failed", JOptionPane.ERROR_MESSAGE);
                resultArea.setText("");
                return;
            }

            resultArea.setText(
                    "Complaint ID: " + complaint.getComplaintId()
                            + "\nCitizen: " + complaint.getName()
                            + "\nZone: " + complaint.getZone()
                            + "\nLocation: " + complaint.getLocation()
                            + "\nDepartment: " + complaint.getDepartment()
                            + "\nPriority: " + complaint.getPriority()
                            + "\nStatus: " + complaint.getStatus()
                            + "\nSubmitted: " + complaint.getCreatedAtDisplay()
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid complaint ID, for example COMPLAINT-1001.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
}
