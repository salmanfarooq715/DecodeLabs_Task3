import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaFX_P3 extends JFrame {

    // ---- Commercial Dark Banking Palette ----
    private static final Color NAVY_DARK = new Color(10, 23, 36);      // Premium deep background
    private static final Color TEAL_MID = new Color(18, 38, 58);       // Container panels
    private static final Color TEAL_LIGHT = new Color(28, 55, 82);     // Field backgrounds
    private static final Color GOLD_ACCENT = new Color(212, 163, 89);  // Corporate gold
    private static final Color GOLD_HOVER = new Color(235, 196, 135);  // Interacted gold
    private static final Color TEXT_WHITE = new Color(240, 244, 248);  // Primary typography
    private static final Color TEXT_MUTED = new Color(148, 163, 184);  // Subtitles
    private static final Color ERROR_RED = new Color(239, 68, 68);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final Map<String, BankAccount> accounts = new HashMap<>();
    private BankAccount currentAccount;
    private boolean isBalanceVisible = false; // Toggle state for hidden balance

    // Login Components
    private JPasswordField pinField;
    private JTextField accField;
    private JLabel statusLabel;

    // Menu / Dashboard Components
    private JLabel balanceValueLabel;
    private JLabel whoLabel;
    private JButton toggleBalanceBtn;
    private JTextArea historyArea;

    // Sign Up Components
    private JTextField newAccField;
    private JTextField newNameField;
    private JPasswordField newPinField;
    private JTextField initialDepositField;
    private JLabel signupStatusLabel;

    public JavaFX_P3() {
        setTitle("DecodeLabs • Commercial Virtual Kiosk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 680);
        setMinimumSize(new Dimension(480, 620));
        setLocationRelativeTo(null);

        // Pre-loaded Demo accounts
        accounts.put("1001", new BankAccount("1001", "Ali Raza", "1234", 5000.0));
        accounts.put("1002", new BankAccount("1002", "Salman Farooq", "5678", 25000.0));

        container.add(buildLoginPanel(), "LOGIN");
        container.add(buildSignupPanel(), "SIGNUP");
        container.add(buildMenuPanel(), "MENU");
        container.add(buildDepositPanel(), "DEPOSIT");
        container.add(buildWithdrawPanel(), "WITHDRAW");
        container.add(buildHistoryPanel(), "HISTORY");

        add(container);
        cardLayout.show(container, "LOGIN");
    }

    // ---------- Gradient background panel ----------
    private GradientPanel gradientPanel(Color c1, Color c2) {
        GradientPanel p = new GradientPanel(c1, c2);
        p.setLayout(new GridBagLayout());
        return p;
    }

    private static class GradientPanel extends JPanel {
        private final Color c1, c2;
        GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ---------- ATM Grid Style Interface Button ----------
    private static class RoundButton extends JButton {
        RoundButton(String text, Color base, Color hover) {
            super(text);
            setForeground(NAVY_DARK);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(210, 48));
            setBackground(base);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hover); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(base); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JLabel titleLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(GOLD_ACCENT);
        return l;
    }

    private JLabel subLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("Consolas", Font.PLAIN, 16));
        f.setBackground(TEAL_LIGHT);
        f.setForeground(TEXT_WHITE);
        f.setCaretColor(TEXT_WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_ACCENT, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        return f;
    }

    private JPasswordField styledPasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(new Font("Consolas", Font.PLAIN, 16));
        f.setBackground(TEAL_LIGHT);
        f.setForeground(TEXT_WHITE);
        f.setCaretColor(TEXT_WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_ACCENT, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        return f;
    }

    private JPanel labelWrap(String label, JComponent field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(TEXT_WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        wrap.add(l, BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    // ---------- LOGIN SCREEN ----------
    private JPanel buildLoginPanel() {
        GradientPanel p = gradientPanel(NAVY_DARK, TEAL_MID);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.insets = new Insets(10, 20, 10, 20);

        gc.gridy = 0; p.add(titleLabel("🏦 DECODELABS BANKING"), gc);
        gc.gridy = 1; p.add(subLabel("Secure Terminal Access Port"), gc);

        accField = styledField(16);
        // Removed pre-filled text for accurate commercial compliance
        gc.gridy = 2; gc.insets = new Insets(20, 20, 10, 20); p.add(labelWrap("ACCOUNT TERMINAL ID", accField), gc);

        pinField = styledPasswordField(16);
        gc.gridy = 3; gc.insets = new Insets(10, 20, 20, 20); p.add(labelWrap("SECURE PIN", pinField), gc);

        ActionListener loginAction = e -> handleLogin();
        accField.addActionListener(loginAction);
        pinField.addActionListener(loginAction);

        RoundButton loginBtn = new RoundButton("AUTHORIZE ACCESS →", GOLD_ACCENT, GOLD_HOVER);
        loginBtn.addActionListener(loginAction);
        gc.gridy = 4; gc.insets = new Insets(15, 20, 5, 20); p.add(loginBtn, gc);

        RoundButton gotoSignupBtn = new RoundButton("Open New Account", TEAL_LIGHT, TEAL_MID);
        gotoSignupBtn.setForeground(TEXT_WHITE);
        gotoSignupBtn.addActionListener(e -> {
            signupStatusLabel.setText(" ");
            cardLayout.show(container, "SIGNUP");
        });
        gc.gridy = 5; p.add(gotoSignupBtn, gc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(ERROR_RED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridy = 6; p.add(statusLabel, gc);

        return p;
    }

    private void handleLogin() {
        String acc = accField.getText().trim();
        String pin = new String(pinField.getPassword());
        BankAccount account = accounts.get(acc);
        if (account != null && account.validatePin(pin)) {
            currentAccount = account;
            statusLabel.setText(" ");
            pinField.setText("");
            accField.setText("");
            isBalanceVisible = false; // Reset mask state
            updateBalanceDisplay();
            refreshMenu();
            cardLayout.show(container, "MENU");
        } else {
            statusLabel.setText("❌ Terminal authorization failed.");
        }
    }

    // ---------- SIGN UP SCREEN ----------
    private JPanel buildSignupPanel() {
        GradientPanel p = gradientPanel(NAVY_DARK, TEAL_MID);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.insets = new Insets(6, 20, 6, 20);

        gc.gridy = 0; p.add(titleLabel("✨ PROVISION ACCOUNT"), gc);
        gc.gridy = 1; p.add(subLabel("Register digital ledger identification keys"), gc);

        newAccField = styledField(16);
        gc.gridy = 2; p.add(labelWrap("Designate Account Number", newAccField), gc);

        newNameField = styledField(16);
        gc.gridy = 3; p.add(labelWrap("Holder Legal Name", newNameField), gc);

        newPinField = styledPasswordField(16);
        gc.gridy = 4; p.add(labelWrap("Assign 4-Digit Secure PIN", newPinField), gc);

        initialDepositField = styledField(16);
        initialDepositField.setText("1000");
        gc.gridy = 5; p.add(labelWrap("Initial Collateral Reserve (Rs.)", initialDepositField), gc);

        ActionListener signupAction = e -> handleSignup();
        newAccField.addActionListener(signupAction);
        newNameField.addActionListener(signupAction);
        newPinField.addActionListener(signupAction);
        initialDepositField.addActionListener(signupAction);

        RoundButton registerBtn = new RoundButton("Commit Registry", GOLD_ACCENT, GOLD_HOVER);
        registerBtn.addActionListener(signupAction);
        gc.gridy = 6; gc.insets = new Insets(20, 20, 5, 20); p.add(registerBtn, gc);

        RoundButton backBtn = new RoundButton("← Cancel Request", TEAL_LIGHT, TEAL_MID);
        backBtn.setForeground(TEXT_WHITE);
        backBtn.addActionListener(e -> cardLayout.show(container, "LOGIN"));
        gc.gridy = 7; p.add(backBtn, gc);

        signupStatusLabel = new JLabel(" ", SwingConstants.CENTER);
        signupStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridy = 8; p.add(signupStatusLabel, gc);

        return p;
    }

    private void handleSignup() {
        String acc = newAccField.getText().trim();
        String name = newNameField.getText().trim();
        String pin = new String(newPinField.getPassword()).trim();
        String initDepStr = initialDepositField.getText().trim();

        if(acc.isEmpty() || name.isEmpty() || pin.isEmpty() || initDepStr.isEmpty()) {
            signupStatusLabel.setForeground(ERROR_RED);
            signupStatusLabel.setText("❌ All data checkpoints are required.");
            return;
        }

        if(accounts.containsKey(acc)) {
            signupStatusLabel.setForeground(ERROR_RED);
            signupStatusLabel.setText("❌ Allocation failed: Identifier exists.");
            return;
        }

        try {
            double initialDeposit = Double.parseDouble(initDepStr);
            if (initialDeposit < 0) throw new NumberFormatException();

            BankAccount newAcc = new BankAccount(acc, name, pin, initialDeposit);
            accounts.put(acc, newAcc);

            signupStatusLabel.setForeground(SUCCESS_GREEN);
            signupStatusLabel.setText("✅ System synchronized. Proceed to authorization.");
            
            newAccField.setText("");
            newNameField.setText("");
            newPinField.setText("");
            
        } catch (NumberFormatException ex) {
            signupStatusLabel.setForeground(ERROR_RED);
            signupStatusLabel.setText("❌ Invalid core balance allocation.");
        }
    }

    // ---------- COMMERCIAL DASHBOARD MENU SCREEN ----------
    private JPanel buildMenuPanel() {
        GradientPanel mainPanel = gradientPanel(NAVY_DARK, TEAL_MID);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top Corporate Header Strip
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        JLabel systemTitle = new JLabel("DECODELABS SYSTEM CONSOLE", SwingConstants.LEFT);
        systemTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        systemTitle.setForeground(GOLD_ACCENT);
        whoLabel = new JLabel("Loading secure workspace...", SwingConstants.LEFT);
        whoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        whoLabel.setForeground(TEXT_WHITE);
        headerPanel.add(systemTitle);
        headerPanel.add(whoLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel: Modern ATM Metric Screen Container
        JPanel metricCard = new JPanel(new GridBagLayout());
        metricCard.setBackground(TEAL_MID);
        metricCard.setBorder(BorderFactory.createLineBorder(TEAL_LIGHT, 2, true));
        GridBagConstraints mG = new GridBagConstraints();
        mG.gridx = 0; mG.insets = new Insets(6, 10, 6, 10);

        JLabel balanceTitle = new JLabel("AVAILABLE CLEARING BALANCE", SwingConstants.CENTER);
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        balanceTitle.setForeground(TEXT_MUTED);
        mG.gridy = 0; metricCard.add(balanceTitle, mG);

        balanceValueLabel = new JLabel("••••••", SwingConstants.CENTER);
        balanceValueLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        balanceValueLabel.setForeground(SUCCESS_GREEN);
        mG.gridy = 1; mG.insets = new Insets(10, 10, 15, 10); metricCard.add(balanceValueLabel, mG);

        // Interactive Hide/Show Balance Toggle Button
        toggleBalanceBtn = new JButton("👁️ Show Balance");
        toggleBalanceBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        toggleBalanceBtn.setForeground(TEXT_WHITE);
        toggleBalanceBtn.setBackground(TEAL_LIGHT);
        toggleBalanceBtn.setFocusPainted(false);
        toggleBalanceBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_ACCENT, 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        toggleBalanceBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBalanceBtn.addActionListener(e -> {
            isBalanceVisible = !isBalanceVisible;
            updateBalanceDisplay();
        });
        mG.gridy = 2; metricCard.add(toggleBalanceBtn, mG);
        mainPanel.add(metricCard, BorderLayout.CENTER);

        // Bottom Side-by-Side Split Action Array (Commercial ATM Style Layout)
        JPanel actionGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        actionGrid.setOpaque(false);

        RoundButton depositBtn = new RoundButton("💰 DEPOSIT FUNDS", GOLD_ACCENT, GOLD_HOVER);
        depositBtn.addActionListener(e -> cardLayout.show(container, "DEPOSIT"));

        RoundButton withdrawBtn = new RoundButton("💸 WITHDRAW CAPITAL", GOLD_ACCENT, GOLD_HOVER);
        withdrawBtn.addActionListener(e -> cardLayout.show(container, "WITHDRAW"));

        RoundButton historyBtn = new RoundButton("📜 LEDGER REVIEWS", GOLD_ACCENT, GOLD_HOVER);
        historyBtn.addActionListener(e -> { refreshHistory(); cardLayout.show(container, "HISTORY"); });

        RoundButton logoutBtn = new RoundButton("🔒 TERMINATE SESSION", new Color(185, 28, 28), ERROR_RED);
        logoutBtn.setForeground(TEXT_WHITE);
        logoutBtn.addActionListener(e -> {
            currentAccount = null;
            cardLayout.show(container, "LOGIN");
        });

        actionGrid.add(depositBtn);
        actionGrid.add(withdrawBtn);
        actionGrid.add(historyBtn);
        actionGrid.add(logoutBtn);

        mainPanel.add(actionGrid, BorderLayout.SOUTH);
        return mainPanel;
    }

    private void updateBalanceDisplay() {
        if (currentAccount == null) return;
        if (isBalanceVisible) {
            balanceValueLabel.setText("Rs. " + String.format("%.2f", currentAccount.getBalance()));
            toggleBalanceBtn.setText("🔒 Hide Balance");
        } else {
            balanceValueLabel.setText("••••••");
            toggleBalanceBtn.setText("👁️ Show Balance");
        }
    }

    private void refreshMenu() {
        if (currentAccount == null) return;
        whoLabel.setText(currentAccount.getAccountHolder() + "  [ID: " + currentAccount.getAccountNumber() + "]");
    }

    // ---------- DEPOSIT / WITHDRAWAL TERMINALS ----------
    private JPanel buildDepositPanel() {
        return buildAmountPanel("Deposit Protocol", "💰", true);
    }

    private JPanel buildWithdrawPanel() {
        return buildAmountPanel("Asset Liquidation", "💸", false);
    }

    private JPanel buildAmountPanel(String title, String icon, boolean isDeposit) {
        GradientPanel p = gradientPanel(NAVY_DARK, TEAL_MID);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.insets = new Insets(15, 20, 15, 20);

        gc.gridy = 0; p.add(titleLabel(icon + " " + title), gc);

        JTextField amountField = styledField(14);
        gc.gridy = 1; p.add(labelWrap("Enter Financial Metric Quantities (Rs.)", amountField), gc);

        JLabel resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Runnable confirmAction = () -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                String msg = isDeposit ? currentAccount.deposit(amt) : currentAccount.withdraw(amt);
                amountField.setText("");

                if (msg.startsWith("✅")) {
                    if (!isDeposit) {
                        JOptionPane.showMessageDialog(this, msg, "Transaction Success", JOptionPane.INFORMATION_MESSAGE);
                        isBalanceVisible = false; 
                        updateBalanceDisplay();
                        refreshMenu();
                        cardLayout.show(container, "MENU");
                    } else {
                        resultLabel.setForeground(SUCCESS_GREEN);
                        resultLabel.setText(msg);
                        if(isBalanceVisible) updateBalanceDisplay();
                    }
                } else {
                    resultLabel.setForeground(ERROR_RED);
                    resultLabel.setText(msg);
                }
            } catch (NumberFormatException ex) {
                resultLabel.setForeground(ERROR_RED);
                resultLabel.setText("❌ Input error: Numeric metrics only.");
            }
        };

        amountField.addActionListener(e -> confirmAction.run());

        RoundButton confirmBtn = new RoundButton("Execute Protocol", GOLD_ACCENT, GOLD_HOVER);
        confirmBtn.addActionListener(e -> confirmAction.run());
        gc.gridy = 2; gc.insets = new Insets(25, 20, 10, 20); p.add(confirmBtn, gc);

        gc.gridy = 3; gc.insets = new Insets(6, 20, 6, 20); p.add(resultLabel, gc);

        RoundButton backBtn = new RoundButton("← Master Console", TEAL_LIGHT, TEAL_MID);
        backBtn.setForeground(TEXT_WHITE);
        backBtn.addActionListener(e -> { 
            isBalanceVisible = false;
            updateBalanceDisplay();
            refreshMenu(); 
            cardLayout.show(container, "MENU"); 
        });
        gc.gridy = 4; gc.insets = new Insets(20, 20, 8, 20); p.add(backBtn, gc);

        return p;
    }

    // ---------- LEDGER TRANSACTION SYSTEM ----------
    private JPanel buildHistoryPanel() {
        GradientPanel p = gradientPanel(NAVY_DARK, TEAL_MID);
        p.setLayout(new BorderLayout(15, 15));
        p.setBorder(new EmptyBorder(25, 25, 25, 25));

        p.add(titleLabel("📜 Ledger Transaction History"), BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        historyArea.setBackground(TEAL_MID);
        historyArea.setForeground(TEXT_WHITE);
        historyArea.setCaretColor(TEXT_WHITE);
        historyArea.setBorder(BorderFactory.createLineBorder(GOLD_ACCENT, 1));
        
        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);

        RoundButton backBtn = new RoundButton("← Return to Console Screen", GOLD_ACCENT, GOLD_HOVER);
        backBtn.addActionListener(e -> { refreshMenu(); cardLayout.show(container, "MENU"); });
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(backBtn);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    private void refreshHistory() {
        if (currentAccount == null) return;
        StringBuilder sb = new StringBuilder();
        for (Transaction t : currentAccount.getHistory()) {
            sb.append(" ").append(t).append("\n\n");
        }
        historyArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JavaFX_P3().setVisible(true));
    }
}

// ------------------------------------------------------------
// Core Banking Infrastructure Entities
// ------------------------------------------------------------

class BankAccount {
    private final String accountNumber;
    private final String accountHolder;
    private final String pin;
    private double balance;
    private final List<Transaction> history;

    public BankAccount(String accountNumber, String accountHolder, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.pin = pin;
        this.balance = initialBalance;
        this.history = new ArrayList<>();
        history.add(new Transaction("INITIAL_DEP", initialBalance, initialBalance));
    }

    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public List<Transaction> getHistory() { return history; }

    public String deposit(double amount) {
        if (amount <= 0) return "❌ Settlement denied: Must be greater than zero.";
        balance += amount;
        history.add(new Transaction("CREDIT", amount, balance));
        return "✅ Funds integrated. Core Reserve: Rs. " + String.format("%.2f", balance);
    }

    public String withdraw(double amount) {
        if (amount <= 0) return "❌ Settlement denied: Invalid metrics.";
        if (amount > balance) return "❌ Dispensation failed: Liquidity bounds reached.";
        balance -= amount;
        history.add(new Transaction("DEBIT", amount, balance));
        return "✅ Capital Dispensed. Core Reserve: Rs. " + String.format("%.2f", balance);
    }
}

class Transaction {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final String timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now().format(FORMAT);
    }

    @Override
    public String toString() {
        return String.format("[%s]  %-12s  Amt: Rs.%-10.2f  Ledger: Rs.%-10.2f",
                timestamp, type, amount, balanceAfter);
    }
}