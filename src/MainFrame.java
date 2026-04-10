import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainFrame extends JFrame {
    private static final Dimension DESKTOP_VIEW = new Dimension(1366, 768);
    private static final String HOME = "home";
    private static final String REPORT = "report";
    private static final String TRACK = "track";
    private static final String ADMIN_LOGIN = "admin_login";
    private static final String ADMIN_DASHBOARD = "admin_dashboard";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Deque<String> history = new ArrayDeque<>();

    private final JButton backButton = new JButton("Back");
    private final JLabel topTitle = new JLabel("Civic Reporting System");
    private final IntroOverlay introOverlay = new IntroOverlay();

    private HomePanel homePanel;
    private ReportComplaintFrame reportPanel;
    private TrackStatusFrame trackPanel;
    private AdminLoginFrame adminLoginPanel;
    private AdminFrame adminDashboardPanel;

    private String currentScreen = HOME;

    public MainFrame() {
        setTitle("Civic Reporting System");
        Dimension windowSize = resolveDesktopWindowSize();
        setSize(windowSize);
        setMinimumSize(windowSize);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout());

        buildTopBar();
        buildContent();
        buildIntroLayer();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                introOverlay.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());
            }
        });

        setVisible(true);
        setLocationRelativeTo(null);
        introOverlay.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());
    }

    private void buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(250, 252, 255));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        UIStyle.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> navigateBack());
        backButton.setVisible(false);

        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topTitle.setForeground(UIStyle.TEXT);

        JLabel icon = new JLabel(UIManager.getIcon("FileView.computerIcon"));

        topBar.add(backButton, BorderLayout.WEST);
        topBar.add(topTitle, BorderLayout.CENTER);
        topBar.add(icon, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    private void buildContent() {
        homePanel = new HomePanel(this);
        reportPanel = new ReportComplaintFrame(this);
        trackPanel = new TrackStatusFrame(this);
        adminLoginPanel = new AdminLoginFrame(this);
        adminDashboardPanel = new AdminFrame(this, null);

        contentPanel.setOpaque(false);
        contentPanel.add(wrapPage(homePanel), HOME);
        contentPanel.add(wrapPage(reportPanel), REPORT);
        contentPanel.add(wrapPage(trackPanel), TRACK);
        contentPanel.add(wrapPage(adminLoginPanel), ADMIN_LOGIN);
        contentPanel.add(wrapPage(adminDashboardPanel), ADMIN_DASHBOARD);

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, HOME);
    }

    private Dimension resolveDesktopWindowSize() {
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = bounds.width < 1180 ? bounds.width : Math.min(DESKTOP_VIEW.width, bounds.width);
        int height = bounds.height < 700 ? bounds.height : Math.min(DESKTOP_VIEW.height, bounds.height);
        return new Dimension(width, height);
    }

    private JScrollPane wrapPage(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIStyle.BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private void buildIntroLayer() {
        getLayeredPane().add(introOverlay, JLayeredPane.DRAG_LAYER);
    }

    public void showHome() {
        showScreen(HOME, "Civic Reporting System", false);
    }

    public void showReport() {
        reportPanel.resetForm();
        showScreen(REPORT, "Report Issue", true);
    }

    public void showTrack() {
        trackPanel.resetView();
        showScreen(TRACK, "Track Issue", true);
    }

    public void showAdminLogin() {
        adminLoginPanel.resetForm();
        showScreen(ADMIN_LOGIN, "Admin Login", true);
    }

    public void showAdminDashboard(AdminAccount account) {
        adminDashboardPanel.setAccount(account);
        showScreen(ADMIN_DASHBOARD, "Admin Dashboard", true);
    }

    private void showScreen(String screen, String title, boolean trackHistory) {
        if (trackHistory && !currentScreen.equals(screen)) {
            history.push(currentScreen);
        }

        currentScreen = screen;
        topTitle.setText(title);
        backButton.setVisible(!HOME.equals(screen));
        cardLayout.show(contentPanel, screen);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void navigateBack() {
        if (history.isEmpty()) {
            showHome();
            return;
        }

        String previous = history.pop();
        currentScreen = previous;
        backButton.setVisible(!HOME.equals(previous));

        switch (previous) {
            case HOME -> topTitle.setText("Civic Reporting System");
            case REPORT -> topTitle.setText("Report Issue");
            case TRACK -> topTitle.setText("Track Issue");
            case ADMIN_LOGIN -> topTitle.setText("Admin Login");
            case ADMIN_DASHBOARD -> topTitle.setText("Admin Dashboard");
            default -> topTitle.setText("Civic Reporting System");
        }

        cardLayout.show(contentPanel, previous);
    }

    private static class IntroOverlay extends JPanel {
        private final String title = "Civic Reporting System";
        private float revealProgress;
        private float dockProgress;
        private float fadeProgress;
        private int visibleCharacters;

        IntroOverlay() {
            setOpaque(false);
            startAnimation();
        }

        private void startAnimation() {
            Timer timer = new Timer(18, null);
            timer.addActionListener(e -> {
                if (revealProgress < 1f) {
                    revealProgress = Math.min(1f, revealProgress + 0.075f);
                    visibleCharacters = Math.min(title.length(), (int) Math.ceil(title.length() * revealProgress));
                } else if (dockProgress < 1f) {
                    dockProgress = Math.min(1f, dockProgress + 0.08f);
                } else if (fadeProgress < 1f) {
                    fadeProgress = Math.min(1f, fadeProgress + 0.12f);
                } else {
                    timer.stop();
                    setVisible(false);
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float overlayAlpha = 1f - fadeProgress;
            g2.setColor(withAlpha(new Color(251, 253, 255), overlayAlpha));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(withAlpha(new Color(232, 242, 255), overlayAlpha));
            g2.fill(new Ellipse2D.Double(100, 90, 240, 240));
            g2.setColor(withAlpha(new Color(240, 247, 236), overlayAlpha));
            g2.fill(new Ellipse2D.Double(getWidth() - 340, 120, 220, 220));

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int topY = 58;
            int currentY = (int) (centerY - (dockProgress * (centerY - topY)));
            int fontSize = (int) (50 - (dockProgress * 24));

            String visibleText = title.substring(0, Math.max(0, visibleCharacters));
            g2.setFont(new Font("Georgia", Font.BOLD, fontSize));
            int textWidth = g2.getFontMetrics().stringWidth(visibleText);

            GradientPaint paint = new GradientPaint(
                    centerX - textWidth / 2f, currentY - 20, new Color(26, 60, 119),
                    centerX + textWidth / 2f, currentY + 20, new Color(49, 133, 230)
            );
            g2.setPaint(paint);
            g2.drawString(visibleText, centerX - textWidth / 2, currentY);

            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(withAlpha(new Color(218, 228, 239), overlayAlpha * 0.8f));
            for (int i = 0; i < 4; i++) {
                int y = 180 + (i * 90);
                g2.draw(new RoundRectangle2D.Double(80, y, getWidth() - 160, 1, 1, 1));
            }
            g2.dispose();
        }

        private Color withAlpha(Color base, float alpha) {
            int value = Math.max(0, Math.min(255, (int) (alpha * 255)));
            return new Color(base.getRed(), base.getGreen(), base.getBlue(), value);
        }
    }
}

class HomePanel extends JPanel {
    private final MainFrame mainFrame;

    HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(buildHero(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHero() {
        JPanel hero = UIStyle.createCard();
        hero.setLayout(new BorderLayout(24, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("<html><div style='width:560px'>Fast civic reporting, simple navigation, and same-window workflows.</div></html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(UIStyle.TEXT);

        JLabel subtitle = new JLabel("<html><div style='width:590px'>Every action opens in the same workspace with a back button, quicker transitions, cleaner cards, and flexible scrolling.</div></html>");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(UIStyle.MUTED_TEXT);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        JButton report = new JButton("Report Issue");
        UIStyle.stylePrimaryButton(report);
        report.addActionListener(e -> mainFrame.showReport());

        JButton track = new JButton("Track Issue");
        UIStyle.styleSecondaryButton(track);
        track.addActionListener(e -> mainFrame.showTrack());

        JButton admin = new JButton("Admin Login");
        UIStyle.styleSecondaryButton(admin);
        admin.addActionListener(e -> mainFrame.showAdminLogin());

        buttons.add(report);
        buttons.add(Box.createHorizontalStrut(12));
        buttons.add(track);
        buttons.add(Box.createHorizontalStrut(12));
        buttons.add(admin);

        left.add(title);
        left.add(Box.createVerticalStrut(14));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(20));
        left.add(buttons);

        JPanel right = new JPanel(new GridLayout(1, 3, 12, 12));
        right.setOpaque(false);
        right.add(infoCard("Faster Flow", "Click response and intro motion are now shorter and more direct.", new Color(231, 242, 255)));
        right.add(infoCard("Single Window", "Actions open in the same application window instead of separate windows.", new Color(237, 251, 246)));
        right.add(infoCard("Desktop Fit", "Panels are sized to stay visible in a desktop-only workspace.", new Color(255, 246, 232)));

        hero.add(left, BorderLayout.NORTH);
        hero.add(right, BorderLayout.CENTER);
        return hero;
    }

    private JPanel infoCard(String titleText, String bodyText, Color bg) {
        JPanel card = new JPanel();
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UIStyle.TEXT);

        JLabel body = new JLabel("<html><div style='width:220px'>" + bodyText + "</div></html>");
        body.setFont(UIStyle.BODY_FONT);
        body.setForeground(UIStyle.MUTED_TEXT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(body);
        return card;
    }

    private JPanel buildBody() {
        JPanel body = new AnimatedCanvas();
        body.setLayout(new BorderLayout(18, 18));
        body.setOpaque(false);

        JPanel actions = new JPanel(new GridLayout(1, 4, 14, 14));
        actions.setOpaque(false);
        actions.add(actionCard("Report Issue", "Submit complaint details and image evidence.", "Open", UIStyle.PRIMARY, () -> mainFrame.showReport()));
        actions.add(actionCard("Track Issue", "Check ID, department, status and priority.", "Track", UIStyle.PRIMARY_ALT, () -> mainFrame.showTrack()));
        actions.add(actionCard("Admin Login", "Open the zonal control room inside the same window.", "Login", UIStyle.SUCCESS, () -> mainFrame.showAdminLogin()));
        actions.add(actionCard("Overview", "Priority routing ensures high priority complaints are handled first.", "View", UIStyle.WARNING, () -> {
        }));

        JPanel workflow = UIStyle.createCard();
        workflow.setLayout(new GridLayout(1, 3, 14, 14));
        workflow.add(step("1. Capture", "Citizen provides area, location, description and image."));
        workflow.add(step("2. Route", "Admin assigns department, priority and status."));
        workflow.add(step("3. Resolve", "Queue remains ordered by urgency."));

        body.add(actions, BorderLayout.NORTH);
        body.add(workflow, BorderLayout.CENTER);
        return body;
    }

    private JPanel actionCard(String titleText, String bodyText, String buttonText, Color accent, Runnable action) {
        JPanel card = UIStyle.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel accentBar = new JPanel();
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        accentBar.setBackground(accent);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UIStyle.TEXT);

        JLabel body = new JLabel("<html><div style='width:220px'>" + bodyText + "</div></html>");
        body.setFont(UIStyle.BODY_FONT);
        body.setForeground(UIStyle.MUTED_TEXT);

        JButton button = new JButton(buttonText);
        UIStyle.stylePrimaryButton(button);
        button.setBackground(accent);
        button.addActionListener(e -> action.run());

        card.add(accentBar);
        card.add(Box.createVerticalStrut(18));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(body);
        card.add(Box.createVerticalGlue());
        card.add(button);
        return card;
    }

    private JPanel step(String titleText, String bodyText) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UIStyle.TEXT);

        JLabel body = new JLabel("<html><div style='width:250px'>" + bodyText + "</div></html>");
        body.setFont(UIStyle.BODY_FONT);
        body.setForeground(UIStyle.MUTED_TEXT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(body);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = UIStyle.createCard();
        footer.setLayout(new BorderLayout());

        JLabel label = new JLabel("Everything now stays inside one desktop-sized window with back navigation and fixed-screen layouts.");
        label.setFont(UIStyle.SUBTITLE_FONT);
        label.setForeground(UIStyle.MUTED_TEXT);
        footer.add(label, BorderLayout.WEST);
        return footer;
    }
}

class AnimatedCanvas extends JPanel {
    private float phase;

    AnimatedCanvas() {
        Timer timer = new Timer(35, e -> {
            phase += 0.025f;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(250, 252, 255),
                getWidth(), getHeight(), new Color(241, 247, 252)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(76, 145, 255, 28));
        g2.fill(new Ellipse2D.Double(60 + Math.sin(phase) * 18, 70, 220, 220));
        g2.setColor(new Color(20, 148, 132, 24));
        g2.fill(new Ellipse2D.Double(getWidth() - 260 + Math.cos(phase * 0.9f) * 15, 40, 190, 190));
        g2.dispose();
    }
}
