import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ReportComplaintFrame extends JPanel {
    private final MainFrame mainFrame;
    private File selectedImageFile;
    private final JLabel previewLabel = new JLabel("No issue image selected", JLabel.CENTER);
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> zoneBox = new JComboBox<>(DataStore.ZONES.toArray(new String[0]));
    private final JTextField locationField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(6, 20);

    public ReportComplaintFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        buildFields();
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private void buildFields() {
        UIStyle.styleInput(nameField);
        UIStyle.styleInput(zoneBox);
        UIStyle.styleInput(locationField);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        UIStyle.styleInput(descriptionArea);

        previewLabel.setPreferredSize(new Dimension(360, 220));
        previewLabel.setOpaque(true);
        previewLabel.setBackground(UIStyle.BACKGROUND_ALT);
        previewLabel.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        previewLabel.setForeground(UIStyle.MUTED_TEXT);
    }

    public void resetForm() {
        nameField.setText("");
        zoneBox.setSelectedIndex(0);
        locationField.setText("");
        descriptionArea.setText("");
        selectedImageFile = null;
        previewLabel.setIcon(null);
        previewLabel.setText("No issue image selected");
    }

    private JPanel buildHeader() {
        JPanel header = UIStyle.createCard();
        header.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Citizen Complaint Submission");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(UIStyle.TEXT);

        JLabel subtitle = new JLabel("Fill in the complaint details. Submission stays in the same workspace and returns quickly.");
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

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridLayout(1, 2, 18, 18));
        root.setOpaque(false);

        JPanel formCard = UIStyle.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        formCard.add(sectionTitle("Complaint Form"));
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(formLabel("Citizen Name"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(nameField);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(formLabel("Area / Zone"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(zoneBox);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(formLabel("Issue Location"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(locationField);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(formLabel("Issue Description"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(new JScrollPane(descriptionArea));
        formCard.add(Box.createVerticalStrut(16));

        JButton chooseImageButton = new JButton("Insert Issue Image");
        UIStyle.styleSecondaryButton(chooseImageButton);
        chooseImageButton.addActionListener(e -> chooseImage());

        JButton submitButton = new JButton("Submit Complaint");
        UIStyle.stylePrimaryButton(submitButton);
        submitButton.addActionListener(e -> submitComplaint());

        formCard.add(formLabel("Evidence Image"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(chooseImageButton);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(previewLabel);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(submitButton);

        JPanel side = new JPanel(new GridLayout(3, 1, 14, 14));
        side.setOpaque(false);
        side.add(infoCard("Better Evidence", "Use a clear photo that shows the exact issue.", new Color(231, 242, 255)));
        side.add(infoCard("Faster Routing", "Accurate details help admins assign the correct department quickly.", new Color(237, 251, 246)));
        side.add(infoCard("Desktop Layout", "This form is sized to fit cleanly in the desktop window.", new Color(255, 246, 232)));

        root.add(formCard);
        root.add(side);
        return root;
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

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.SECTION_FONT);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.BODY_FONT);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(360, 220, Image.SCALE_SMOOTH);
            previewLabel.setText("");
            previewLabel.setIcon(new ImageIcon(scaled));
        }
    }

    private void submitComplaint() {
        String name = nameField.getText().trim();
        String zone = zoneBox.getSelectedItem().toString();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || location.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Citizen name, zone, location, and issue description are required.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = DataStore.generateId();
        Complaint complaint = new Complaint(id, name, zone, location, description);

        if (selectedImageFile != null && selectedImageFile.exists()) {
            try {
                Path uploadsDir = Path.of(System.getProperty("user.dir"), "uploads");
                Files.createDirectories(uploadsDir);
                String safeName = selectedImageFile.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
                Path destination = uploadsDir.resolve("COMPLAINT-" + id + "_" + safeName);
                Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                complaint.setImagePath(destination.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "The complaint was saved, but the image could not be copied.", "Image Upload Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        DataStore.addComplaint(complaint);
        JOptionPane.showMessageDialog(this, "Complaint submitted successfully.\nComplaint ID: " + complaint.getComplaintId(), "Submission Complete", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.showHome();
    }
}
