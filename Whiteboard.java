import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Whiteboard extends JFrame {
    private JButton penButton;
    private JButton eraserButton;
    private JButton highlighterButton;
    private JButton rectangleButton;
    private JButton circleButton;
    private JButton lineButton;
    private JButton textButton;
    private JComboBox<String> colorComboBox;
    private JComboBox<Integer> strokeComboBox;
    private JComboBox<Integer> textSizeComboBox;
    private JPanel drawingPanel;

    private Color currentColor;
    private int currentStroke;
    private int currentTextSize;
    private Tool currentTool;

    private ArrayList<Shape> shapes;

    private enum Tool {
        PEN, ERASER, HIGHLIGHTER, RECTANGLE, CIRCLE, LINE, TEXT
    }

    public Whiteboard() {
        shapes = new ArrayList<>();

        // Create GUI components
        penButton = new JButton("Pen");
        eraserButton = new JButton("Eraser");
        highlighterButton = new JButton("Highlighter");
        rectangleButton = new JButton("Rectangle");
        circleButton = new JButton("Circle");
        lineButton = new JButton("Line");
        textButton = new JButton("Text");

        String[] colors = {"Black", "Red", "Blue", "Green"};
        colorComboBox = new JComboBox<>(colors);

        Integer[] strokes = {1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
        strokeComboBox = new JComboBox<>(strokes);

        Integer[] textSizes = {1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
        textSizeComboBox = new JComboBox<>(textSizes);

        drawingPanel = new JPanel();
        drawingPanel.setPreferredSize(new Dimension(800, 600));
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new DrawingMouseListener());
        drawingPanel.addMouseMotionListener(new DrawingMouseMotionListener());

        // Set default tool, color, and text size
        currentTool = Tool.PEN;
        currentColor = Color.BLACK;
        currentStroke = 1;
        currentTextSize = 12;

        // Add event listeners
        penButton.addActionListener(new ToolButtonListener(Tool.PEN));
        eraserButton.addActionListener(new ToolButtonListener(Tool.ERASER));
        highlighterButton.addActionListener(new ToolButtonListener(Tool.HIGHLIGHTER));
        rectangleButton.addActionListener(new ToolButtonListener(Tool.RECTANGLE));
        circleButton.addActionListener(new ToolButtonListener(Tool.CIRCLE));
        lineButton.addActionListener(new ToolButtonListener(Tool.LINE));
        textButton.addActionListener(new ToolButtonListener(Tool.TEXT));

        colorComboBox.addActionListener(new ColorComboBoxListener());
        strokeComboBox.addActionListener(new StrokeComboBoxListener());
        textSizeComboBox.addActionListener(new TextSizeComboBoxListener());

        // Set up the layout
        JPanel toolPanel = new JPanel();
        toolPanel.add(penButton);
        toolPanel.add(eraserButton);
        toolPanel.add(highlighterButton);
        toolPanel.add(rectangleButton);
        toolPanel.add(circleButton);
        toolPanel.add(lineButton);
        toolPanel.add(textButton);
        toolPanel.add(colorComboBox);
        toolPanel.add(strokeComboBox);
        toolPanel.add(textSizeComboBox);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolPanel, BorderLayout.NORTH);
        getContentPane().add(drawingPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Whiteboard");
        pack();
        setVisible(true);
    }

    private class ToolButtonListener implements ActionListener {
        private Tool tool;

        public ToolButtonListener(Tool tool) {
            this.tool = tool;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            currentTool = tool;
        }
    }

    private class ColorComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedColor = (String) colorComboBox.getSelectedItem();
            switch (selectedColor) {
                case "Black":
                    currentColor = Color.BLACK;
                    break;
                case "Red":
                    currentColor = Color.RED;
                    break;
                case "Blue":
                    currentColor = Color.BLUE;
                    break;
                case "Green":
                    currentColor = Color.GREEN;
                    break;
                default:
                    currentColor = Color.BLACK;
            }
        }
    }

    private class StrokeComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentStroke = (int) strokeComboBox.getSelectedItem();
        }
    }

    private class TextSizeComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentTextSize = (int) textSizeComboBox.getSelectedItem();
        }
    }

    private class DrawingMouseListener extends MouseAdapter {
        private int startX, startY;

        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();

            if (currentTool == Tool.TEXT) {
                String text = JOptionPane.showInputDialog(Whiteboard.this, "Enter text:");
                if (text != null) {
                    Graphics2D g = (Graphics2D) drawingPanel.getGraphics();
                    g.setColor(currentColor);
                    g.setFont(new Font("Arial", Font.PLAIN, currentTextSize));
                    g.drawString(text, startX, startY);
                    g.dispose();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();

            Graphics2D g = (Graphics2D) drawingPanel.getGraphics();
            g.setColor(currentColor);
            g.setStroke(new BasicStroke(currentStroke));

            switch (currentTool) {
                case PEN:
                case ERASER:
                case HIGHLIGHTER:
                    g.drawLine(startX, startY, endX, endY);
                    break;
                case RECTANGLE:
                    g.drawRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(endX - startX), Math.abs(endY - startY));
                    break;
                case CIRCLE:
                    g.drawOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(endX - startX), Math.abs(endY - startY));
                    break;
                case LINE:
                    g.drawLine(startX, startY, endX, endY);
                    break;
                default:
                    break;
            }

            shapes.add(new Shape(currentTool, startX, startY, endX, endY, currentColor, currentStroke));
            g.dispose();
        }
    }

    private class DrawingMouseMotionListener extends MouseMotionAdapter {
        private int startX, startY;

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (currentTool == Tool.PEN || currentTool == Tool.ERASER || currentTool == Tool.HIGHLIGHTER) {
                Graphics2D g = (Graphics2D) drawingPanel.getGraphics();
                g.setColor(currentTool == Tool.ERASER ? Color.WHITE : currentColor);
                g.setStroke(new BasicStroke(currentTool == Tool.HIGHLIGHTER ? currentStroke + 5 : currentStroke));
                g.drawLine(startX, startY, x, y);
                g.dispose();
            }

            startX = x;
            startY = y;
        }
    }

    private class Shape {
        private Tool tool;
        private int startX, startY;
        private int endX, endY;
        private Color color;
        private int stroke;

        public Shape(Tool tool, int startX, int startY, int endX, int endY, Color color, int stroke) {
            this.tool = tool;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = color;
            this.stroke = stroke;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Whiteboard::new);
    }
}
