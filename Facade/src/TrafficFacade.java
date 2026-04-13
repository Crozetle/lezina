import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TrafficFacade {

    private static final int LIGHT_X = 600;
    private static final int ROAD_Y  = 200;

    public void start() {
        SwingUtilities.invokeLater(() -> {
            TrafficLight light = new TrafficLight();
            Car car = new Car(light);

            JPanel panel = createPanel(light, car);

            JFrame frame = new JFrame("Светофор и автомобиль");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            light.start();
            car.start();

            new Timer(50, e -> panel.repaint()).start();
        });
    }

    private JPanel createPanel(TrafficLight light, Car car) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(new Color(80, 80, 80));
                g.fillRect(0, ROAD_Y, getWidth(), 40);

                int cx = car.getX();

                g.setColor(Color.BLUE);
                g.fillRect(cx, ROAD_Y - 30, 70, 30);

                g.setColor(Color.DARK_GRAY);
                g.fillRect(LIGHT_X + 5, ROAD_Y - 110, 40, 110);

                TrafficLight.Signal sig = light.getSignal();
                Color[] inactive = { new Color(80, 0, 0), new Color(80, 80, 0), new Color(0, 80, 0) };
                Color[] active   = { Color.RED, Color.YELLOW, Color.GREEN };
                TrafficLight.Signal[] order = {
                    TrafficLight.Signal.RED,
                    TrafficLight.Signal.YELLOW,
                    TrafficLight.Signal.GREEN
                };
                int[] oy = { ROAD_Y - 108, ROAD_Y - 80, ROAD_Y - 52 };

                for (int i = 0; i < 3; i++) {
                    g.setColor(sig == order[i] ? active[i] : inactive[i]);
                    g.fillOval(LIGHT_X + 12, oy[i], 24, 24);
                }
            }
        };

        panel.setPreferredSize(new Dimension(800, 280));
        panel.setBackground(new Color(135, 206, 235));
        return panel;
    }
}
