import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PIDTuner {
	public static PIDTuner tuner = new PIDTuner();
	public static int setpoint = 1000;
	public static List<Double> input;

	public static void main(String[] args) {
		JFrame frame = new JFrame("PID Tuner");

		JPanel panel = new JPanel();
		JPanel editor = new JPanel();
		// panel.setSize(1080, 960);

		JSlider Pslider = new CustomSlider(0, 1000, 0, "P");
		JSlider Islider = new CustomSlider(0, 1000, 0, "I");
		JSlider Dslider = new CustomSlider(0, 1000, 0, "D");
		JSlider Fslider = new CustomSlider(0, 1000, 0, "F");
		JSlider Sslider = new CustomSlider(0, 5000, 650, "S");

		JPanel graph = new Graph(PIDLoop(0, 0, 0, 0));

		panel.add(Pslider);
		panel.add(Islider, 1);
		panel.add(Dslider, 2);
		panel.add(Fslider, 3);
		panel.add(Sslider, 4);
		panel.setSize(1080, 100);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		frame.add(panel);
		frame.setSize(1080, 960);
		frame.setVisible(true);

		graph = new Graph(PIDLoop(Pslider.getValue() * 0.001, Islider.getValue() * 0.001, Dslider.getValue() * 0.001,
				Fslider.getValue() * 0.1));
		double PVal = -1, IVal = 0, DVal = 0;

		while (true) {
			setpoint = Sslider.getValue();
			input = PIDLoop(Pslider.getValue() * 0.001, Islider.getValue() * 0.001, Dslider.getValue() * 0.001,
					Fslider.getValue() * 0.1);
			graph.repaint();
			panel.remove(graph);
			panel.add(graph);

		}

	}

	static double acceptableError = 0.5;

	public static List<Double> PIDLoop(double kP, double kI, double kD, double kF) {
		double current = kF;
		double P = 0, I = 0, D = 0, prevE = 0;
		int count = 0;
		List<Double> outputs = new ArrayList<>();
		
		double friction = 1.3;

		outputs.add(kF);
		while (Math.abs(setpoint - current) > acceptableError && count < 1000) {
			double error = setpoint - current;
			P = error * kP;
			I += error * kI;
			D = (error - prevE) * kD;
			prevE = error;
			// System.out.println(P + I + D);
			current += (P + I + D + kF) * friction;
			outputs.add(current);

			count++;
		}
		return outputs;
	}

}

class CustomSlider extends JSlider {

	private static final long serialVersionUID = 1L;

	public CustomSlider(int a, int b, int c, String name) {
		super(a, b, c);
		setMajorTickSpacing(100);
		setMinorTickSpacing(50);
		setPaintTicks(true);

		setPaintLabels(true);
		Hashtable<Integer, JLabel> position = new Hashtable<Integer, JLabel>();
		position.put(-25, new JLabel(name));
		position.put(0, new JLabel("0"));
		position.put((int) (super.getMaximum() * 0.25), new JLabel("0.25"));
		position.put((int) (super.getMaximum() * 0.5), new JLabel("0.5"));
		position.put((int) (super.getMaximum() * 0.75), new JLabel("0.75"));
		position.put(super.getMaximum(), new JLabel("1"));

		boolean isReleased = false;
		setLabelTable(position);
	}
}

class Graph extends JPanel {
	int tick = 100;
	int height = 480;
	int width = 1080;
	int xoffset = 20;
	int yoffset = 50;

	public Graph(List<Double> input) {
		// this.setpoint = setpoint;
		this.repaint();
	}

	public void drawBackground(Graphics2D g, int time, double max) {
		double val = (max > PIDTuner.setpoint) ? max : PIDTuner.setpoint;
		int lines = ((int) Math.ceil(PIDTuner.setpoint / tick) + 1);
		for (int i = 0; i < lines + 1; i++) {
			g.drawString(Integer.toString(tick * i), 0, height / lines * i + yoffset);
			g.drawLine(xoffset, height / lines * i + yoffset, width + xoffset, height / lines * i + yoffset);
		}
		
		g.setColor(Color.GRAY);
		for (int i = 1; i < time - 1; i++){
			g.drawLine(i * width / time + xoffset, yoffset, i * width / time + xoffset, height + yoffset);
		}
		
		int ysetpoint = (int) (height / lines * lines * (PIDTuner.setpoint / (tick * lines * 1.0)));
		g.drawString(Integer.toString(PIDTuner.setpoint), 0, ysetpoint + yoffset);
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(3));
		g.drawLine(xoffset, ysetpoint + yoffset, width + xoffset, ysetpoint + yoffset);
	}

	public void PIDgraph(Graphics2D g, List<Double> input) {
		int lines = (int) Math.ceil(PIDTuner.setpoint / tick) + 1;
		int step = input.size();
		double max = 0;
		for (double i : input){
			if (i > max){
				max = i;
			}
		}
		drawBackground(g, step, max);

		g.setStroke(new BasicStroke(2));
		for (int i = 1; i < step - 1; i++) {
			int pos1 = (int) (height / lines * lines * (input.get(i - 1) / (tick * lines * 1.0)));
			int pos2 = (int) (height / lines * lines * (input.get(i) / (tick * lines * 1.0)));

			g.setColor(Color.BLUE);
			g.drawLine((i - 1) * width / step + xoffset, pos1 + yoffset, i * width / step + xoffset, pos2 + yoffset);
			g.setColor(Color.BLACK);
			g.drawOval((i - 1) * width / step + xoffset, pos1 + yoffset, 3, 3);
			g.drawOval(i * width / step + xoffset, pos2 + yoffset, 3, 3);
		}
		g.drawString("fluctuation: " + Integer.toString(PIDStats(input)), 0, height + yoffset + 32);
		g.drawString("loops: " + Integer.toString(step), 0, height + yoffset + 48);
		g.drawString("error: " + Double.toString(PIDTuner.setpoint - input.get(step - 1)), 0, height + yoffset + 64);
		
	}
	
	public int PIDStats(List<Double> input){
		int fluctuations = 0;
		boolean over = false;
		for (int i = 0; i < input.size() - 1; i++){
			if (!over && input.get(i) > PIDTuner.setpoint){
				fluctuations ++;
				over = true;
			} 
			if (over && input.get(i) < PIDTuner.setpoint){
				over = false;
			}
		}
		return fluctuations;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		drawBackground(g2d);
		PIDgraph(g2d, PIDTuner.input);
	}
}