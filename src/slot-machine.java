import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;


class NewCanvasDB extends Canvas {
    private Dimension size;
    private Image back;
    private Graphics buffer;

    private int radius = 50;     // orbit radius (px)
    private int speed = 50;      // slider value (controls angular speed)
    private double theta = 0.0;  // angle (radians)

    // center of orbit
    private int cx, cy;

    public void init() {
        size = getSize();
        back = createImage(size.width, size.height);
        buffer = back.getGraphics();

        // orbit center 
        cx = size.width / 2;
        cy = size.height / 2;
    }

    public void setRadius(int r) {
        if (r < 0) r = 0; // only positive values allowed
        radius = r;
    }

    public void setSpeed(int s) {
        if (s < 0) s = 0; // only positive values allowed
        speed = s;
    }

    // advance the animation by one tick
    public void step() {
        // map speed slider to angular velocity 
        double omega = speed * 0.02; // radians per tick
        theta += omega;

        // keep theta in the range 0 - 2pi 
        if (theta >= Math.PI * 2) theta -= Math.PI * 2;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (size == null) return;

        // If the canvas was resized, recreate buffer
        Dimension now = getSize();
        if (back == null || now.width != size.width || now.height != size.height) {
            size = now;
            back = createImage(size.width, size.height);
            buffer = back.getGraphics();
            cx = size.width / 2;
            cy = size.height / 2;
        }

        // ---------- Background color changes with angle (0->255->0) ----------
        // Triangle wave based on theta (0 - 2pi):
        // 0 -> pi gives 0 - 255, pi -> 2pi gives 255 - 0
        double t = theta / (2.0 * Math.PI); // 0 - 1
        int c; // color
        if (t < 0.5) c = (int) Math.round(510.0 * t);       // 0 - 255
        else         c = (int) Math.round(510.0 * (1.0 - t)); // 255 - 0
        if (c < 0) c = 0;
        if (c > 255) c = 255;

        buffer.setColor(new Color(c, c, c));
        for (int i = 0; i < size.width; i++) {
            buffer.drawLine(i, 0, i, size.height - 1);
        }

        // ---------- Draw orbit circle ----------
        buffer.setColor(Color.RED);
        buffer.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // ---------- Draw moving point ----------
        int px = (int) Math.round(cx + radius * Math.cos(theta));
        int py = (int) Math.round(cy + radius * Math.sin(theta));

        buffer.setColor(Color.BLUE);
        int dot = 18;               // the diameter
        buffer.fillOval(px - (dot / 2), py - (dot / 2), dot, dot);

        // Show coordinates
        buffer.setColor(Color.YELLOW);
        buffer.drawString("x=" + px + "  y=" + py, 10, size.height - 10);

        // Copy back buffer to screen 
        g.drawImage(back, 0, 0, this);
    }
}

public class J14_1 extends JFrame implements ChangeListener {
    private NewCanvasDB myCanvas;

    private JSlider sSpeed;
    private JSlider sRadius;
    private JLabel lSpeed;
    private JLabel lRadius;

    private Timer timer;

    public J14_1(String title) {
        super(title);

        JPanel p = (JPanel) getContentPane();
        p.setLayout(new FlowLayout());

        myCanvas = new NewCanvasDB();
        myCanvas.setPreferredSize(new Dimension(360, 360));
        p.add(myCanvas);

        // Sliders 
        sSpeed = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        sRadius = new JSlider(JSlider.HORIZONTAL, 0, 150, 50);

        sSpeed.addChangeListener(this);
        sRadius.addChangeListener(this);

        lSpeed = new JLabel("Speed: " + sSpeed.getValue());
        lRadius = new JLabel("Radius: " + sRadius.getValue());

        // Align labels
        lSpeed.setHorizontalAlignment(JLabel.CENTER);
        lRadius.setHorizontalAlignment(JLabel.CENTER);

        // Add to UI 
        //p.add(new JLabel("Speed"));
        p.add(sSpeed);
        p.add(lSpeed);

        //p.add(new JLabel("Radius"));
        p.add(sRadius);
        p.add(lRadius);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 450); 
        setVisible(true);

        myCanvas.init();

        // Apply initial slider values
        myCanvas.setSpeed(sSpeed.getValue());
        myCanvas.setRadius(sRadius.getValue());

        // Animation timer
        while(true) {
        		myCanvas.step(); 
        		myCanvas.repaint(); 
        		try {
    				Thread.sleep(16); // larger sleep value, slower motion 
    			}
    			catch(InterruptedException e) {
    				e.printStackTrace();
    			}
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object src = e.getSource();
        if (src == sSpeed) {
            int v = sSpeed.getValue();
            lSpeed.setText("Velocity: " + v);
            myCanvas.setSpeed(v);
        } else if (src == sRadius) {
            int r = sRadius.getValue();
            lRadius.setText("Radius: " + r);
            myCanvas.setRadius(r);
        }

        // repaint so changes appear immediately
        myCanvas.repaint();
    }

    public static void main(String[] args) {
        new J14_1("Circular Motion");
    }
}
