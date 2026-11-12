import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {
    private final ParkingLotManager manager = new ParkingLotManager();
    private final Color navy = new Color(25, 42, 72);
    private final Color blue = new Color(45, 105, 190);
    private final Color bg = new Color(244, 247, 251);
    private final CardLayout cards = new CardLayout();
    private final JPanel views = new JPanel(cards);
    private JLabel totalValue, occupiedValue, availableValue;
    private DefaultTableModel zoneModel, ticketModel;
    private JTextField idField, plateField;
    private JComboBox<DriverRole> roleBox;
    private JComboBox<VehicleType> typeBox;

    public Main() {
        setTitle("EzPark | Campus Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);
        add(sidebar(), BorderLayout.WEST);
        add(mainArea(), BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel sidebar() {
        JPanel p = new JPanel();
        p.setBackground(navy);
        p.setPreferredSize(new Dimension(205, 0));
        p.setBorder(new EmptyBorder(25, 15, 20, 15));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("EZPARK");
        logo.setFont(new Font("SansSerif", Font.BOLD, 27));
        logo.setForeground(Color.WHITE);
        p.add(logo);
        JLabel sub = new JLabel("CAMPUS PARKING");
        sub.setForeground(new Color(190, 205, 225));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        p.add(sub);
        p.add(Box.createVerticalStrut(40));
        nav(p, "Dashboard", "dashboard");
        nav(p, "Vehicle Entry", "entry");
        nav(p, "Vehicle Exit", "exit");
        p.add(Box.createVerticalGlue());
        JLabel foot = new JLabel("Java Swing Application");
        foot.setForeground(new Color(190, 205, 225));
        p.add(foot);
        return p;
    }

    private void nav(JPanel p, String label, String key) {
        JButton b = new JButton(label);
        b.setMaximumSize(new Dimension(180, 43));
        b.setPreferredSize(new Dimension(180, 43));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(39, 61, 96));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(0, 14, 0, 8));
        b.addActionListener(e -> { cards.show(views, key); refreshAll(); });
        p.add(b);
        p.add(Box.createVerticalStrut(9));
    }

    private JPanel mainArea() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        JLabel title = new JLabel("Campus Parking Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 21));
        title.setForeground(navy);
        header.add(title, BorderLayout.WEST);
        JLabel tag = new JLabel("LIVE PARKING SYSTEM");
        tag.setForeground(blue);
        tag.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.add(tag, BorderLayout.EAST);
        views.setBackground(bg);
        views.add(dashboard(), "dashboard");
        views.add(entry(), "entry");
        views.add(exit(), "exit");
        p.add(header, BorderLayout.NORTH);
        p.add(views, BorderLayout.CENTER);
        return p;
    }

    private JPanel dashboard() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(24, 25, 22, 25));
        JLabel heading = heading("Live Parking Dashboard");
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        totalValue = new JLabel("28"); occupiedValue = new JLabel("0"); availableValue = new JLabel("28");
        stats.add(stat("TOTAL CAPACITY", totalValue, blue));
        stats.add(stat("OCCUPIED SPACES", occupiedValue, new Color(190, 65, 65)));
        stats.add(stat("AVAILABLE SPACES", availableValue, new Color(36, 145, 95)));
        zoneModel = new DefaultTableModel(new Object[]{"Parking Zone", "Available", "Occupied", "Capacity", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = table(zoneModel);
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(heading("Parking Zone Availability"), BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton refresh = primary("Refresh Dashboard");
        refresh.addActionListener(e -> refreshDashboard());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setOpaque(false); bottom.add(refresh);
        JPanel north = new JPanel(new BorderLayout(0, 15)); north.setOpaque(false);
        north.add(heading, BorderLayout.NORTH); north.add(stats, BorderLayout.CENTER);
        p.add(north, BorderLayout.NORTH); p.add(center, BorderLayout.CENTER); p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel stat(String label, JLabel value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 8)); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accent), new EmptyBorder(14, 16, 14, 16)));
        JLabel l = new JLabel(label); l.setFont(new Font("SansSerif", Font.BOLD, 10)); l.setForeground(new Color(105, 116, 132));
        value.setFont(new Font("SansSerif", Font.BOLD, 29)); value.setForeground(navy);
        p.add(l, BorderLayout.NORTH); p.add(value, BorderLayout.CENTER); return p;
    }

    private JPanel entry() {
        JPanel outer = new JPanel(new GridBagLayout()); outer.setBackground(bg); outer.setBorder(new EmptyBorder(24, 25, 24, 25));
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE); form.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(9, 8, 9, 8); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; form.add(heading("Vehicle Entry"), c); c.gridwidth = 1;
        idField = new JTextField(22); plateField = new JTextField(22); roleBox = new JComboBox<>(DriverRole.values()); typeBox = new JComboBox<>(VehicleType.values());
        row(form,c,1,"School ID / Permit Number",idField); row(form,c,2,"Driver Role",roleBox); row(form,c,3,"Vehicle Type",typeBox); row(form,c,4,"Plate Number",plateField);
        JButton submit = primary("Register Vehicle"); submit.addActionListener(e -> registerVehicle());
        c.gridx=0; c.gridy=5; c.gridwidth=2; c.insets=new Insets(18,8,8,8); form.add(submit,c);
        JLabel note = new JLabel("School ID: 25-06359  |  Visitor permit: V-1234"); note.setForeground(Color.GRAY); note.setFont(new Font("SansSerif",Font.PLAIN,11));
        c.gridy=6; form.add(note,c); outer.add(form); return outer;
    }

    private void row(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridy=y; c.gridx=0; c.weightx=.4; JLabel l=new JLabel(label); l.setForeground(navy); l.setFont(new Font("SansSerif",Font.BOLD,12)); p.add(l,c);
        c.gridx=1; c.weightx=.6; field.setFont(new Font("SansSerif",Font.PLAIN,13)); p.add(field,c);
    }

    private JPanel exit() {
        JPanel p = new JPanel(new BorderLayout(0,14)); p.setBackground(bg); p.setBorder(new EmptyBorder(24,25,24,25));
        p.add(heading("Currently Parked Vehicles"),BorderLayout.NORTH);
        ticketModel = new DefaultTableModel(new Object[]{"Ticket ID","Plate Number","Role","Vehicle Type","Spot ID","Entry Time"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable t=table(ticketModel); p.add(new JScrollPane(t),BorderLayout.CENTER);
        JButton release=primary("Release Selected Vehicle"); release.addActionListener(e->releaseVehicle(t));
        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setOpaque(false); bottom.add(release); p.add(bottom,BorderLayout.SOUTH);
        return p;
    }

    private JLabel heading(String text) { JLabel l=new JLabel(text); l.setFont(new Font("SansSerif",Font.BOLD,18)); l.setForeground(navy); return l; }
    private JTable table(DefaultTableModel model) {
        JTable t=new JTable(model); t.setRowHeight(29); t.setFont(new Font("SansSerif",Font.PLAIN,12));
        t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12)); t.getTableHeader().setBackground(new Color(231,237,246));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); return t;
    }
    private JButton primary(String text) {
        JButton b=new JButton(text); b.setFont(new Font("SansSerif",Font.BOLD,12)); b.setForeground(Color.WHITE); b.setBackground(blue);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(10,16,10,16)); return b;
    }

    private void registerVehicle() {
        String id=idField.getText().trim(); String plate=plateField.getText().trim().toUpperCase();
        DriverRole role=(DriverRole)roleBox.getSelectedItem(); VehicleType type=(VehicleType)typeBox.getSelectedItem();
        Vehicle v;
        switch(type) { case MOTORCYCLE: v=new Motorcycle(plate,id,role); break; case TRUCK: v=new Truck(plate,id,role); break; default: v=new Car(plate,id,role); }
        try {
            Ticket t=manager.parkVehicle(v);
            JOptionPane.showMessageDialog(this,"ACCESS GRANTED\n\nTicket: "+t.getTicketId()+"\nPlate: "+plate+"\nSpot: "+t.getSpot().getSpotId()+"\nEntry time: "+t.getEntryTimeText(),"Vehicle Registered",JOptionPane.INFORMATION_MESSAGE);
            idField.setText(""); plateField.setText(""); refreshAll(); cards.show(views,"dashboard");
        } catch(InvalidPermitException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Access Denied",JOptionPane.ERROR_MESSAGE); }
          catch(ZoneFullException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Zone Full",JOptionPane.WARNING_MESSAGE); }
    }

    private void releaseVehicle(JTable table) {
        int row=table.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a parked vehicle first.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        String plate=String.valueOf(ticketModel.getValueAt(row,1)); Ticket t=manager.releaseSpot(plate);
        if(t==null) JOptionPane.showMessageDialog(this,"Vehicle not found.","Exit Error",JOptionPane.ERROR_MESSAGE);
        else JOptionPane.showMessageDialog(this,"EXIT CONFIRMED\nSpot "+t.getSpot().getSpotId()+" is now available.","Vehicle Exit",JOptionPane.INFORMATION_MESSAGE);
        refreshAll();
    }

    private void refreshAll(){refreshDashboard();refreshTickets();}
    private void refreshDashboard(){
        if(zoneModel==null)return; zoneModel.setRowCount(0); int total=0,open=0;
        for(ParkingZone z:manager.getZones()){int a=z.getAvailableCount();int o=z.getCapacity()-a;total+=z.getCapacity();open+=a;zoneModel.addRow(new Object[]{z.getZoneName(),a,o,z.getCapacity(),z.isZoneFull()?"FULL":"Available"});}
        totalValue.setText(String.valueOf(total));availableValue.setText(String.valueOf(open));occupiedValue.setText(String.valueOf(total-open));
    }
    private void refreshTickets(){
        if(ticketModel==null)return;ticketModel.setRowCount(0);
        List<Ticket> list=manager.getActiveTickets();
        for(Ticket t:list){Vehicle v=t.getVehicle();ticketModel.addRow(new Object[]{t.getTicketId(),v.getPlateNumber(),v.getDriverRole(),v.getVehicleType(),t.getSpot().getSpotId(),t.getEntryTimeText()});}
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->{try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ignored){}new Main().setVisible(true);});}
}
