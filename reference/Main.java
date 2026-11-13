import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;


/** Desktop Swing interface for the EzPark campus parking system. */
public class Main extends JFrame {
    private static final Color NAVY = new Color(24, 43, 73);
    private static final Color BLUE = new Color(43, 104, 178);
    private static final Color LIGHT = new Color(244, 247, 251);
    private static final Color GREEN = new Color(38, 135, 95);

    private final ParkingLotManager manager = new ParkingLotManager();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    private final JLabel occupiedValue = new JLabel("0", SwingConstants.CENTER);
    private final JLabel availableValue = new JLabel("0", SwingConstants.CENTER);
    private final JLabel capacityValue = new JLabel("0", SwingConstants.CENTER);
    private final JLabel twoWheelValue = new JLabel("0", SwingConstants.CENTER);
    private final JLabel fourWheelValue = new JLabel("0", SwingConstants.CENTER);
    private final DefaultTableModel zoneModel = new DefaultTableModel(
            new Object[]{"PARKING ZONE", "OCCUPIED", "AVAILABLE", "CAPACITY", "STATUS"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel ticketModel = new DefaultTableModel(
            new Object[]{"TICKET ID", "PLATE NUMBER", "DRIVER ROLE", "VEHICLE", "PARKING SLOT", "ENTRY TIME"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private JTable ticketTable;

    private final JTextField idField = new JTextField();
    private final JTextField plateField = new JTextField();
    private final JComboBox<DriverRole> roleBox = new JComboBox<>(DriverRole.values());
    private final JComboBox<VehicleType> typeBox = new JComboBox<>(VehicleType.values());

    public Main() {
        super("EzPark | Campus Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        content.setBackground(LIGHT);
        content.add(buildDashboard(), "dashboard");
        content.add(buildEntryPage(), "entry");
        content.add(buildExitPage(), "exit");
        add(content, BorderLayout.CENTER);
        refreshData();
        showPage("dashboard");
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(NAVY);
        side.setPreferredSize(new Dimension(220, 0));
        side.setLayout(new GridLayout(9, 1, 0, 8));
        side.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));

        JLabel brand = new JLabel("EZPARK", SwingConstants.CENTER);
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 27));
        side.add(brand);
        JLabel subtitle = new JLabel("CAMPUS PARKING", SwingConstants.CENTER);
        subtitle.setForeground(new Color(190, 205, 225));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        side.add(subtitle);
        side.add(navButton("Dashboard", "dashboard", new Color(15, 30, 110)));
        side.add(navButton("Vehicle Entry", "entry", new Color(15, 30, 110)));
        side.add(navButton("Vehicle Exit", "exit", new Color(15, 30, 110)));
        //JLabel note = new JLabel("Live parking monitor", SwingConstants.CENTER);
        //note.setForeground(new Color(190, 205, 225));
        //note.setFont(new Font("SansSerif", Font.PLAIN, 11));
        //side.add(note);
        return side;
    }

    private JButton navButton(String label, String page, Color color) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(13, 12, 13, 12));
        button.addActionListener(e -> {
            refreshData();
            showPage(page);
        });
        return button;
    }

    private JPanel buildDashboard() {
        JPanel page = pagePanel();
        page.add(pageTitle("Campus Occupancy Dashboard", "Real-time overview of parking availability."), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        JPanel metrics = new JPanel(new GridLayout(1, 5, 12, 0));
        metrics.setOpaque(false);
        metrics.add(metricCard("OCCUPIED", occupiedValue, BLUE));
        metrics.add(metricCard("AVAILABLE", availableValue, GREEN));
        metrics.add(metricCard("TOTAL CAPACITY", capacityValue, NAVY));
        metrics.add(metricCard("2-WHEELERS", twoWheelValue, new Color(130, 92, 170)));
        metrics.add(metricCard("4-WHEELERS", fourWheelValue, new Color(190, 125, 55)));
        center.add(metrics, BorderLayout.NORTH);

        JPanel tablePanel = panelCard();
        tablePanel.setLayout(new BorderLayout(0, 10));
        tablePanel.add(sectionTitle("Parking Zone Status"), BorderLayout.NORTH);
        JTable zoneTable = new JTable(zoneModel);
        styleTable(zoneTable);
        tablePanel.add(new JScrollPane(zoneTable), BorderLayout.CENTER);
        center.add(tablePanel, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildEntryPage() {
        JPanel page = pagePanel();
        page.add(pageTitle("Vehicle Entry", "Register an incoming vehicle and assign an available slot."), BorderLayout.NORTH);
        JPanel form = panelCard();
        form.setLayout(new BorderLayout(0, 18));
        form.add(sectionTitle("Driver and Vehicle Information"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(4, 2, 16, 16));
        fields.setOpaque(false);
        fields.add(fieldLabel("School ID / Visitor Permit"));
        fields.add(idField);
        fields.add(fieldLabel("Driver Role"));
        fields.add(roleBox);
        fields.add(fieldLabel("Vehicle Type"));
        fields.add(typeBox);
        fields.add(fieldLabel("Plate Number"));
        fields.add(plateField);
        form.add(fields, BorderLayout.CENTER);

        JButton submit = new JButton("REGISTER VEHICLE / PARK");
        submit.setBackground(BLUE);
        submit.setForeground(Color.WHITE);
        submit.setFont(new Font("SansSerif", Font.BOLD, 14));
        submit.setFocusPainted(false);
        submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submit.setMargin(new Insets(13, 18, 13, 18));
        submit.addActionListener(this::registerVehicle);
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.add(submit);
        form.add(actions, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(form, BorderLayout.NORTH);
        page.add(wrapper, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildExitPage() {
        JPanel page = pagePanel();
        page.add(pageTitle("Vehicle Exit", "Select a parked vehicle to release its parking slot."), BorderLayout.NORTH);
        JPanel panel = panelCard();
        panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionTitle("Currently Parked Vehicles"), BorderLayout.NORTH);
        ticketTable = new JTable(ticketModel);
        styleTable(ticketTable);
        panel.add(new JScrollPane(ticketTable), BorderLayout.CENTER);
        JButton release = new JButton("RELEASE SELECTED VEHICLE");
        release.setBackground(GREEN);
        release.setForeground(Color.WHITE);
        release.setFont(new Font("SansSerif", Font.BOLD, 13));
        release.setFocusPainted(false);
        release.setCursor(new Cursor(Cursor.HAND_CURSOR));
        release.setMargin(new Insets(12, 16, 12, 16));
        release.addActionListener(e -> releaseSelectedVehicle());
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.add(release);
        panel.add(actions, BorderLayout.SOUTH);
        page.add(panel, BorderLayout.CENTER);
        return page;
    }

    private JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBackground(LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
        return panel;
    }

    private JPanel pageTitle(String title, String subtitle) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 5));
        panel.setOpaque(false);
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.BOLD, 30));
        heading.setForeground(NAVY);
        JLabel sub = new JLabel(subtitle);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(100, 110, 125));
        panel.add(heading);
        panel.add(sub);
        return panel;
    }

    private JPanel metricCard(String title, JLabel value, Color accent) {
        JPanel card = panelCard();
        card.setLayout(new GridLayout(2, 1, 0, 6));
        JLabel name = new JLabel(title, SwingConstants.CENTER);
        name.setFont(new Font("SansSerif", Font.BOLD, 10));
        name.setForeground(new Color(100, 110, 125));
        value.setFont(new Font("SansSerif", Font.BOLD, 27));
        value.setForeground(accent);
        card.add(name);
        card.add(value);
        return card;
    }

    private JPanel panelCard() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(NAVY);
        return label;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(NAVY);
        return label;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(232, 238, 247));
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(232, 235, 240));
        table.setSelectionBackground(new Color(215, 230, 248));
    }

    private void registerVehicle(ActionEvent event) {
        String id = idField.getText().trim();
        String plate = plateField.getText().trim().toUpperCase();
        DriverRole role = (DriverRole) roleBox.getSelectedItem();
        VehicleType type = (VehicleType) typeBox.getSelectedItem();

        if (id.isEmpty() || plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete the ID/permit and plate number fields.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle vehicle;
        if (type == VehicleType.MOTORCYCLE) vehicle = new Motorcycle(plate, id, role);
        else if (type == VehicleType.TRUCK) vehicle = new Truck(plate, id, role);
        else vehicle = new Car(plate, id, role);

        try {
            Ticket ticket = manager.parkVehicle(vehicle);
            JOptionPane.showMessageDialog(this,
                    "ACCESS GRANTED\n\nTicket: " + ticket.getTicketId()
                    + "\nVehicle: " + vehicle
                    + "\nParking Slot: " + ticket.getSpot().getSpotId()
                    + "\nEntry Time: " + ticket.getEntryTimeText(),
                    "Vehicle Successfully Parked", JOptionPane.INFORMATION_MESSAGE);
            idField.setText("");
            plateField.setText("");
            refreshData();
            showPage("dashboard");
        } catch (InvalidPermitException | ZoneFullException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ACCESS DENIED", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void releaseSelectedVehicle() {
        int selected = ticketTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table first.",
                    "No Vehicle Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String plate = String.valueOf(ticketModel.getValueAt(selected, 1));
        Ticket released = manager.releaseSpot(plate);
        if (released == null) {
            JOptionPane.showMessageDialog(this, "The selected vehicle could not be found.",
                    "Exit Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "EXIT CONFIRMED\n\nTicket: " + released.getTicketId()
                    + "\nPlate: " + plate
                    + "\nReleased Slot: " + released.getSpot().getSpotId(),
                    "Vehicle Exit Complete", JOptionPane.INFORMATION_MESSAGE);
        }
        refreshData();
    }

    private void refreshData() {
        int capacity = 0;
        int available = 0;
        int motorcycles = 0;
        int otherVehicles = 0;
        zoneModel.setRowCount(0);

        for (ParkingZone zone : manager.getZones()) {
            int open = zone.getAvailableCount();
            int used = zone.getCapacity() - open;
            capacity += zone.getCapacity();
            available += open;
            zoneModel.addRow(new Object[]{zone.getZoneName(), used, open, zone.getCapacity(),
                    zone.isZoneFull() ? "FULL" : "AVAILABLE"});
        }

        for (Ticket ticket : manager.getActiveTickets()) {
            if (ticket.getVehicle().getVehicleType() == VehicleType.MOTORCYCLE) motorcycles++;
            else otherVehicles++;
        }

        occupiedValue.setText(String.valueOf(capacity - available));
        availableValue.setText(String.valueOf(available));
        capacityValue.setText(String.valueOf(capacity));
        twoWheelValue.setText(String.valueOf(motorcycles));
        fourWheelValue.setText(String.valueOf(otherVehicles));

        ticketModel.setRowCount(0);
        List<Ticket> tickets = manager.getActiveTickets();
        for (Ticket ticket : tickets) {
            Vehicle v = ticket.getVehicle();
            ticketModel.addRow(new Object[]{ticket.getTicketId(), v.getPlateNumber(),
                    v.getDriverRole(), v.getVehicleType(), ticket.getSpot().getSpotId(),
                    ticket.getEntryTimeText()});
        }
    }

    private void showPage(String page) {
        cardLayout.show(content, page);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
