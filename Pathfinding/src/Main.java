import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel {

	static int tileSize = 18, width = 7, height = 7;
	static int scale = 5;
	static float alpha = 0.5f;
	static final int RED = 0, GREEN = 1, BLUE = 2, YELLOW = 3, SPECIAL = 4;
	static int grid[][][] = new int[width][height][3]; // points - color - tile
	static BufferedImage image;
	static BufferedImage[][] imageGrid = new BufferedImage[width][height];

	static JFrame frame = new JFrame("LOLO");
	static Main game = new Main();
	
	public static void main(String[] args) throws IOException {
		frame.add(game);
		frame.setSize(tileSize * (width + 1) * scale, tileSize * (height + 1) * scale);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addMouseListener(new MouseListener() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    	try {
					game.collapseOnClick(e);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		game.setup();
		game.drawGrid();
		game.repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				g2d.drawImage(imageGrid[i][j], i * tileSize * scale, j * tileSize * scale, tileSize * scale, tileSize * scale, null);
				g2d.drawString(Integer.toString(grid[i][j][1]), (int) ((i + 0.5) * tileSize * scale), (int) ((j + 0.5) * tileSize * scale));
			}
		}
	}
	
	public static void drawGrid() throws IOException{
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(1f, 0f, 0f, alpha);
				switch (grid[i][j][1]) {
				case RED:
					c = new Color(1f, 0f, 0f, alpha);
					break;
				case GREEN:
					c = new Color(0f, 1f, 0f, alpha);
					break;
				case BLUE:
					c = new Color(0f, 0f, 1f, alpha);
					break;
				case YELLOW:
					c = new Color(1f, 1f, 0f, alpha);
					break;
				case -1:
					c = new Color(1f, 1f, 1f, alpha);
				}
				imageGrid[i][j] = dye(tiling(i, j), c);
			}
		}
	}
	
	public static BufferedImage dye(BufferedImage image, Color color) {
	    int w = image.getWidth();
	    int h = image.getHeight();
	    BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = dyed.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.setComposite(AlphaComposite.SrcAtop);
	    g.setColor(color);
	    g.fillRect(0, 0, w, h);
	    g.dispose();
	    return dyed;
	  }
	
	public static BufferedImage tiling(int i, int j) throws IOException{
		File file = new File("D:/GameAssets/Lolo/tiles.png");
		int color = grid[i][j][1];
		int tile = 0;
		tile += (j > 0) ? ((grid[i][j - 1][1] == color) ? 1 : 0) : 0;
		tile += (i < width - 1) ? ((grid[i + 1][j][1] == color) ? 2 : 0) : 0;
		tile += (j < height - 1) ? ((grid[i][j + 1][1] == color) ? 4 : 0) : 0;
		tile += (i > 0) ? ((grid[i - 1][j][1] == color) ? 8 : 0) : 0;
		return ImageIO.read(file).getSubimage(tile * tileSize, 0, tileSize, tileSize);
	}
	
	
	public void setup() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j][0] = 1;
				grid[i][j][1] = ((int) (Math.random() * 100)) % 4;
			}
		}
	}
	
	public void collapseOnClick(MouseEvent e) throws IOException{
		boolean[][] flagGrid = new boolean[width][height];
		int x = (int) Math.floor((e.getX() - 11)/(tileSize * scale));
		int y = (int) Math.floor((e.getY() - 45)/(tileSize * scale));
		flagGrid[x][y] = true;
		System.out.println((e.getX() - 11) + ", " + (e.getY() - 45));
		grid[x][y][1] = -1;
		game.drawGrid();
		game.repaint();
	}

}
