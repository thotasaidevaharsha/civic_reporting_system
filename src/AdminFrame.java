import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdminFrame extends JPanel {
    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> departmentBox;
    private JComboBox<String> priorityBox;
    private JComboBox<String> statusBox;
    private JLabel accountLabel;
    private JLabel totalCountLabel;
    private JLabel highCountLabel;
    private JLabel resolvedCountLabel;

    public AdminFrame(MainFrame mainFrame, AdminAccount account)
     {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
    }

    public void setAccount(AdminAccount account) {
        accountLabel.setText(account == null ? "No admin selected" : "Logged in as " + account.getDisplayName() + " | Zone: " + account.getZone());
        loadComplaints();
    }

    private JPanel buildHeader() {
        JPanel header = UIStyle.createCard();
        header.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Operational Complaint Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(UIStyle.TEXT);

        accountLabel = new JLabel("No admin selected");
        accountLabel.setFont(UIStyle.SUBTITLE_FONT);
        accountLabel.setForeground(UIStyle.MUTED_TEXT);

        text.add(title);
        text.add(Box.createVerticalStrut(8));
        text.add(accountLabel);

        JButton backButton = new JButton("Back To Login");
        UIStyle.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> mainFrame.showAdminLogin());

        header.add(text, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMainContent() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setOpaque(false);

        JPanel summary = new JPanel(new GridLayout(1, 3, 14, 14));
        summary.setOpaque(false);
        totalCountLabel = metricCard("Total Complaints", new Color(231, 242, 255));
        highCountLabel = metricCard("High Priority Queue", new Color(255, 240, 240));
        resolvedCountLabel = metricCard("Resolved", new Color(237, 251, 246));

        summary.add((JPanel) totalCountLabel.getParent());
        summary.add((JPanel) highCountLabel.getParent());
        summary.add((JPanel) resolvedCountLabel.getParent());

        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setOpaque(false);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        center.add(buildActionPanel(), BorderLayout.EAST);

        root.add(summary, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JLabel metricCard(String titleText, Color bg) {
        JPanel card = new JPanel();
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(UIStyle.SUBTITLE_FONT);
        title.setForeground(UIStyle.MUTED_TEXT);

        JLabel value = new JLabel("0");
        value.setFont(new Font("Segoe UI", Font.BOLD, 28));
        value.setForeground(UIStyle.TEXT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(value);
        return value;
    }

    private JScrollPane buildTablePanel() {
        model = new DefaultTableModel(new String[]{
                "Complaint ID", "Citizen", "Zone", "Location", "Department", "Priority", "Status", "Submitted", "Image"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(60);
        table.setFont(UIStyle.BODY_FONT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getColumnModel().getColumn(8).setCellRenderer(new ImageRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (row < 0) {
                    return;
                }
                populateEditor(row);
                if (e.getClickCount() == 2 && column == 8) {
                    String imagePath = String.valueOf(model.getValueAt(row, column));
                    if (!imagePath.isEmpty()) {
                        openImage(imagePath);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        return scrollPane;
    }

    private JPanel buildActionPanel() {
        JPanel card = UIStyle.createCard();
        card.setPreferredSize(new Dimension(290, 0));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Complaint Assignment");
        title.setFont(UIStyle.SECTION_FONT);
        title.setForeground(UIStyle.TEXT);

        JLabel body = new JLabel("<html><div style='width:250px'>Select a complaint and update department, priority, and status. High priority complaints remain first in the queue.</div></html>");
        body.setFont(UIStyle.BODY_FONT);
        body.setForeground(UIStyle.MUTED_TEXT);

        departmentBox = new JComboBox<>(DataStore.DEPARTMENTS.toArray(new String[0]));
        priorityBox = new JComboBox<>(DataStore.PRIORITIES.toArray(new String[0]));
        statusBox = new JComboBox<>(DataStore.STATUSES.toArray(new String[0]));
        UIStyle.styleInput(departmentBox);
        UIStyle.styleInput(priorityBox);
        UIStyle.styleInput(statusBox);

        JButton updateButton = new JButton("Update Selected Complaint");
        UIStyle.stylePrimaryButton(updateButton);
        updateButton.addActionListener(e -> updateSelectedComplaint());

        JButton refreshButton = new JButton("Refresh Queue");
        UIStyle.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> loadComplaints());

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(body);
        card.add(Box.createVerticalStrut(16));
        card.add(label("Department"));
        card.add(Box.createVerticalStrut(6));
        card.add(departmentBox);
        card.add(Box.createVerticalStrut(14));
        card.add(label("Priority"));
        card.add(Box.createVerticalStrut(6));
        card.add(priorityBox);
        card.add(Box.createVerticalStrut(14));
        card.add(label("Status"));
        card.add(Box.createVerticalStrut(6));
        card.add(statusBox);
        card.add(Box.createVerticalStrut(18));
        card.add(updateButton);
        card.add(Box.createVerticalStrut(10));
        card.add(refreshButton);
        return card;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.BODY_FONT);
        label.setForeground(UIStyle.TEXT);
        return label;
    }

    private void loadComplaints() {
        if (model == null) {
            return;
        }

        model.setRowCount(0);
        List<Complaint> complaints = DataStore.getSortedComplaints();
        int highCount = 0;
        int resolvedCount = 0;

        for (Complaint complaint : complaints) {
            if ("High".equalsIgnoreCase(complaint.getPriority())) {
                highCount++;
            }
            if ("Resolved".equalsIgnoreCase(complaint.getStatus())) {
                resolvedCount++;
            }

            model.addRow(new Object[]{
                    complaint.getComplaintId(),
                    complaint.getName(),
                    complaint.getZone(),
                    complaint.getLocation(),
                    complaint.getDepartment(),
                    complaint.getPriority(),
                    complaint.getStatus(),
                    complaint.getCreatedAtDisplay(),
                    complaint.getImagePath()
            });
        }

        totalCountLabel.setText(String.valueOf(complaints.size()));
        highCountLabel.setText(String.valueOf(highCount));
        resolvedCountLabel.setText(String.valueOf(resolvedCount));
    }

    private void populateEditor(int row) {
        departmentBox.setSelectedItem(model.getValueAt(row, 4).toString());
        priorityBox.setSelectedItem(model.getValueAt(row, 5).toString());
        statusBox.setSelectedItem(model.getValueAt(row, 6).toString());
    }

    private void updateSelectedComplaint() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a complaint from the dashboard first.", "No Complaint Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String digits = model.getValueAt(selectedRow, 0).toString().replaceAll("[^0-9]", "");
        Complaint complaint = DataStore.findComplaint(Integer.parseInt(digits));
        if (complaint == null) {
            JOptionPane.showMessageDialog(this, "The selected complaint could not be found.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        complaint.setDepartment(departmentBox.getSelectedItem().toString());
        complaint.setPriority(priorityBox.getSelectedItem().toString());
        complaint.setStatus(statusBox.getSelectedItem().toString());
        loadComplaints();
        JOptionPane.showMessageDialog(this, "Complaint " + complaint.getComplaintId() + " updated successfully.", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openImage(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "Image not found.", "Missing File", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                JOptionPane.showMessageDialog(this, "Unsupported image file.", "Image Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JLabel label = new JLabel(new ImageIcon(image));
            JScrollPane scrollPane = new JScrollPane(label);
            scrollPane.setPreferredSize(new Dimension(Math.min(920, image.getWidth() + 20), Math.min(700, image.getHeight() + 20)));

            JDialog dialog = new JDialog();
            dialog.setTitle("Issue Image Preview");
            dialog.getContentPane().add(scrollPane);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setModal(true);
            dialog.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to open the image.", "Image Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(CENTER);
            label.setIcon(null);
            label.setText("");

            String path = value == null ? "" : value.toString();
            if (!path.isEmpty()) {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(80, 48, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
            } else {
                label.setText("No Image");
            }
            return label;
        }
    }
}
