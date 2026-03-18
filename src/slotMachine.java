import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class slotMachine extends JFrame implements ActionListener {

    // --- UI ---
    private final JTextField[] slots = new JTextField[3];
    private final JButton[] stopBtns = new JButton[3];
    private final JButton startBtn = new JButton("Start");

    // --- Threads ---
    private SlotThread[] workers = new SlotThread[3];

    slotMachine(String title) {
        super(title);

        // Main layout
        setLayout(new BorderLayout(10, 10));

        // Panel for 3 slots (text fields)
        JPanel slotPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        Font bigFont = new Font("SanSerif", Font.ITALIC, 75);

        for (int i = 0; i < 3; i++) {
            slots[i] = new JTextField("0", 2);
            slots[i].setHorizontalAlignment(JTextField.CENTER);
            slots[i].setEditable(false);
            slots[i].setFont(bigFont);
            slotPanel.add(slots[i]);
        }

        // Panel for stop buttons under each slot
        JPanel stopPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        for (int i = 0; i < 3; i++) {
            stopBtns[i] = new JButton("stop" + i);
            stopBtns[i].addActionListener(this);
            stopPanel.add(stopBtns[i]);
        }

        // Start button panel
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startBtn.addActionListener(this);
        startPanel.add(startBtn);

        // Compose
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(slotPanel, BorderLayout.CENTER);
        center.add(stopPanel, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        add(startPanel, BorderLayout.SOUTH);

        // Initial enable/disable state 
        setRunningUI(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Update button states
     * running=false: Start enabled, Stop0-2 disabled
     * running=true : Start disabled, Stop0-2 enabled
     */
    private void setRunningUI(boolean running) {
        startBtn.setEnabled(!running);
        for (int i = 0; i < 3; i++) {
            stopBtns[i].setEnabled(running);
        }
    }

    /** Start all 3 threads */
    private void startAll() {
        // Create fresh threads every time Start is pressed
        for (int i = 0; i < 3; i++) {
            workers[i] = new SlotThread(slots[i]);
            workers[i].start();
        }
        setRunningUI(true);
    }

    /** Stop a specific thread and disable that stop button. */
    private void stopOne(int idx) {
        if (workers[idx] != null) {
            workers[idx].requestStop();
        }
        stopBtns[idx].setEnabled(false);

        // If all stop buttons are disabled, enable Start again
        boolean allStopped = true;
        for (int i = 0; i < 3; i++) {
            if (stopBtns[i].isEnabled()) {
                allStopped = false;
                break;
            }
        }
        if (allStopped) {
            startBtn.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == startBtn) {
            startAll();
            return;
        }

        // Check which stop button
        for (int i = 0; i < 3; i++) {
            if (src == stopBtns[i]) {
                stopOne(i);
                return;
            }
        }
    }

    public static void main(String[] args) {
        // Start GUI on EDT (Event Dispatch Thread) 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new slotMachine("Slot Machine");
            }
        });
    }
}

/**
 * One rotating slot.
 * - Updates its JTextField with digits 0..9 repeatedly.
 * - Uses SwingUtilities.invokeLater to update UI safely.
 */
class SlotThread extends Thread {
    private final JTextField field;
    private volatile boolean running = true;

    SlotThread(JTextField field) {
        this.field = field;
    }

    public void requestStop() {
        running = false;
    }

    @Override
    public void run() {
        int n = 0;

        while (running) {
            final String text = Integer.toString(n);

            // Update Swing component safely from background thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    field.setText(text);
                }
            });

            n = (n + 1) % 10;

            try {
                Thread.sleep(80); // speed of rotation 
            } catch (InterruptedException ex) {
                // If interrupted, stop 
                running = false;
            }
        }
    }
}
