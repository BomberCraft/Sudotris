/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose Tools | Templates and open the
 * template in the editor.
 */
package com.bomber.testing.sudotris.frame;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

/**
 * @author Bomber
 */
public class MazeDrawer implements GLEventListener, KeyListener {

	private int WIDTH = 1024;
	private int HEIGHT = 768;

	private float X = 1;
	private float Y = 1;
	private float SCALE = 1f;

	private int N; // dimension of maze
	private boolean[][] north; // is there a wall to north of cell i, j
	private boolean[][] east;
	private boolean[][] south;
	private boolean[][] west;
	private boolean[][] visited;
	private boolean done = false;
	int interN = N;
	JLabel label = new JLabel("Size : 30");

	Frame frame;
	GLCanvas canvas;
	Animator animator;

	Thread solve;
	boolean first;

	public static void main(String[] args) {
		MazeDrawer mazeDrawer = new MazeDrawer(30);

		mazeDrawer.draw();
		// mazeDrawer.solve();
	}

	public MazeDrawer(int N) {
		this.N = N;

		// ~ StdDraw.setScale(0, N + 2);
		init();
		generate();
	}

	private void init() {
		initMaze();
		initFrame();

	}

	private void initMaze() {
		// initialize border cells as already visited
		visited = new boolean[N + 2][N + 2];
		for (int x = 0; x < N + 2; x++) {
			visited[x][0] = visited[x][N + 1] = true;
		}
		for (int y = 0; y < N + 2; y++) {
			visited[0][y] = visited[N + 1][y] = true;
		}

		// initialze all walls as present
		north = new boolean[N + 2][N + 2];
		east = new boolean[N + 2][N + 2];
		south = new boolean[N + 2][N + 2];
		west = new boolean[N + 2][N + 2];
		for (int x = 0; x < N + 2; x++) {
			for (int y = 0; y < N + 2; y++) {
				north[x][y] = east[x][y] = south[x][y] = west[x][y] = true;
			}
		}

		X = 1;
		Y = 1;

		done = false;
	}

	private void initFrame() {
		this.frame = new Frame("Simple Maze Drawer");
		this.canvas = new GLCanvas();
		this.animator = new Animator(canvas);

		// Menu
		MenuBar menuBar = new MenuBar();
		Menu fichier = new Menu("Menu");
		MenuItem restartMenu = new MenuItem("Restart");
		MenuItem resizeMenu = new MenuItem("Size");
		MenuItem solveMenu = new MenuItem("Solve");
		MenuItem closeMenu = new MenuItem("Close");
		menuBar.add(fichier);

		fichier.add(restartMenu);
		fichier.add(resizeMenu);
		fichier.add(solveMenu);
		fichier.addSeparator();
		fichier.add(closeMenu);

		resizeMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				final JFrame thframe = new JFrame();
				thframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				thframe.setSize(250, 150);
				thframe.setTitle("Size ?");
				thframe.setLocationRelativeTo(null);

				JSlider slide = new JSlider();
				slide.setMaximum(100);
				slide.setMinimum(10);
				slide.setValue(30);
				slide.setPaintTicks(true);
				slide.setPaintLabels(true);
				slide.setMinorTickSpacing(10);
				slide.setMajorTickSpacing(20);
				slide.setVisible(true);
				slide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent event) {

						label.setText("Size : " + ((JSlider) event.getSource()).getValue());
						interN = ((JSlider) event.getSource()).getValue();
					}
				});

				JButton buttonOK = new JButton();
				buttonOK.setText("OK");
				buttonOK.setVisible(true);
				buttonOK.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent ae) {
						N = interN;
						frame.setSize(WIDTH, HEIGHT);
						restart();
						thframe.dispose();
					}

				});
				thframe.getContentPane().add(buttonOK, BorderLayout.EAST);
				thframe.getContentPane().add(slide, BorderLayout.CENTER);
				thframe.getContentPane().add(label, BorderLayout.SOUTH);
				thframe.setVisible(true);
			}
		});
		restartMenu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				int option;
				String[] test = new String[] { "Yes", "No" };
				option = JOptionPane.showOptionDialog(null, "Do you want to restart ?", "Hi", 0,
						JOptionPane.INFORMATION_MESSAGE, null, test, null);
				if (option == JOptionPane.OK_OPTION) {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							restart();
						}
					});
				}
			}
		});

		solveMenu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						solve();
					}
				});
			}
		});

		closeMenu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						System.exit(0);
					}
				});
			}
		});

		frame.setMenuBar(menuBar);

		canvas.addGLEventListener(this);
		frame.addKeyListener(this);
		canvas.addKeyListener(this);

		frame.add(canvas);

		frame.setSize(WIDTH, HEIGHT);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable() {

					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});

		// Center frame
		frame.setLocationRelativeTo(null);
	}

	// generate the maze
	private void generate(int x, int y) {
		visited[x][y] = true;

		// while there is an unvisited neighbor
		while (!visited[x][y + 1] || !visited[x + 1][y] || !visited[x][y - 1] || !visited[x - 1][y]) {

			// pick random neighbor (could use Knuth's trick instead)
			while (true) {
				double r = Math.random();
				if (r < 0.25 && !visited[x][y + 1]) {
					north[x][y] = south[x][y + 1] = false;
					generate(x, y + 1);
					break;
				} else if (r >= 0.25 && r < 0.50 && !visited[x + 1][y]) {
					east[x][y] = west[x + 1][y] = false;
					generate(x + 1, y);
					break;
				} else if (r >= 0.5 && r < 0.75 && !visited[x][y - 1]) {
					south[x][y] = north[x][y - 1] = false;
					generate(x, y - 1);
					break;
				} else if (r >= 0.75 && r < 1.00 && !visited[x - 1][y]) {
					west[x][y] = east[x - 1][y] = false;
					generate(x - 1, y);
					break;
				}
			}
		}

	}

	// generate the maze starting from lower left
	private void generate() {
		generate(1, 1);

		for (int x = 1; x <= N; x++) {
			for (int y = 1; y <= N; y++) {
				visited[x][y] = false;
			}
		}
	}

	// Show a message
	private void messageYesNo(String yes, String no, String message, String title) {
		int option;
		String[] text = new String[2];
		text[0] = "Sure !";
		text[1] = "No I'm tired...";
		option = JOptionPane.showOptionDialog(null, "Congratulations !" + "\nDo you want to restart ?", "The End", 0,
				JOptionPane.INFORMATION_MESSAGE, null, text, null);
		if (option == JOptionPane.OK_OPTION) {
			restart();
		} else {
			frame.dispose();
			System.exit(0);
		}
	}

	// solve the maze using depth-first search
	private void solve(int x, int y) {
		long speed = 3;
		if (x == 0 || y == 0 || x == N + 1 || y == N + 1) {
			return;
		}
		if (done || visited[x][y]) {
			return;
		}

		// ~ StdDraw.setPenColor(StdDraw.BLUE);
		// ~ StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
		X = x;
		Y = y;
		first = true;

		visited[x][y] = true;
		try {
			// ~ StdDraw.show(3);
			solve.sleep(speed);

		} catch (InterruptedException ex) {
			Logger.getLogger(MazeDrawer.class.getName()).log(Level.SEVERE, null, ex);
		}
		// reached middle
		if (x == N / 2 && y == N / 2) {
			done = true;
			messageYesNo("Sure !", "No I'm tired...", "Congratulations !" + "\nDo you want to restart ?", "The End");
		}

		if (!north[x][y]) {
			solve(x, y + 1);
		}
		if (!east[x][y]) {
			solve(x + 1, y);
		}
		if (!south[x][y]) {
			solve(x, y - 1);
		}
		if (!west[x][y]) {
			solve(x - 1, y);
		}

		if (done) {
			return;
		}
		// ~ StdDraw.setPenColor(StdDraw.ORANGE);
		// ~ StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
		X = x;
		Y = y;
		first = false;
		try {
			// ~ StdDraw.show(3);
			solve.sleep(speed);

		} catch (InterruptedException ex) {
			Logger.getLogger(MazeDrawer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// solve the maze starting from the start state
	public void solve() {
		if (solve != null) {
			return;
		}

		for (int x = 1; x <= N; x++) {
			for (int y = 1; y <= N; y++) {
				visited[x][y] = false;
			}
		}

		done = false;
		solve = new Thread(new Runnable() {

			public void run() {
				solve(1, 1);
			}
		});

		solve.start();
	}

	// draw the maze
	public void draw() {
		frame.setVisible(true);
		animator.start();

		// ~ StdDraw.show(100);
	}

	private void restart() {
		if (solve != null) {
			solve.stop();
			solve = null;
		}

		initMaze();
		generate();

	}

	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.glClearColor(1, 1, 1, 1);
		// gl.glClearColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		WIDTH = width;
		HEIGHT = height;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, N + 2, 0, N + 2, -1, 1);

		// toutes les transformations suivantes s�appliquent au mod�le de vue
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		render_scene(drawable);

		// trace la sc�ne graphique qui vient juste d'�tre d�finie
		gl.glFlush();
	}

	private void render_scene(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// gl.glColor3i(200, 50, 50);
		// StdDraw.setPenColor(StdDraw.RED);
		// ~ StdDraw.filledCircle(N / 2.0 + 0.5, N / 2.0 + 0.5, 0.375);
		// ~ StdDraw.filledCircle(1.5, 1.5, 0.375);

		gl.glLineWidth(2f);

		// StdDraw.setPenColor(StdDraw.BLACK);
		for (int x = 1; x <= N; x++) {
			for (int y = 1; y <= N; y++) {
				gl.glLoadIdentity();
				gl.glColor3d(0, 0, 0);
				gl.glTranslatef(x, y, 0);
				if (south[x][y]) {
					// StdDraw.line(x, y, x + 1, y);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex2i(0, 0); // Top
					gl.glVertex2i(1, 0); // Bottom Left
					gl.glEnd();
				}
				if (north[x][y]) {
					// StdDraw.line(x, y + 1, x + 1, y + 1);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex2i(0, 1); // Top
					gl.glVertex2i(1, 1); // Bottom Left
					gl.glEnd();
				}
				if (west[x][y]) {
					// StdDraw.line(x, y, x, y + 1);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex2i(0, 0); // Top
					gl.glVertex2i(0, 1); // Bottom Left
					gl.glEnd();
				}
				if (east[x][y]) {
					// StdDraw.line(x + 1, y, x + 1, y + 1);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex2i(1, 0); // Top
					gl.glVertex2i(1, 1); // Bottom Left
					gl.glEnd();
				}
				if (visited[x][y]) {
					gl.glColor4f(.7f, .8f, .9f, .1f);

					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex2f(.05f, .05f);
					gl.glVertex2f(.05f, .95f);
					gl.glVertex2f(.95f, .95f);
					gl.glVertex2f(.95f, .05f);
					gl.glEnd();
				}
			}
		}

		gl.glColor3f(1.0f, .1f, .1f);
		gl.glLoadIdentity();

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(X + .1f, Y + .1f);
		gl.glVertex2f(X + .1f, Y + .9f);
		gl.glVertex2f(X + .9f, Y + .9f);
		gl.glVertex2f(X + .9f, Y + .1f);
		gl.glEnd();

		gl.glColor3f(.1f, 1.0f, .2f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(N / 2 + .1f, N / 2 + .1f);
		gl.glVertex2f(N / 2 + .1f, N / 2 + .9f);
		gl.glVertex2f(N / 2 + .9f, N / 2 + .9f);
		gl.glVertex2f(N / 2 + .9f, N / 2 + .1f);
		gl.glEnd();

	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int x = (int) X;
		int y = (int) Y;

		if (done) {
			e.consume();
			return;
		}

		switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				X += east[x][y] ? 0 : SCALE;
				break;
			case KeyEvent.VK_LEFT:
				X -= west[x][y] ? 0 : SCALE;
				break;
			case KeyEvent.VK_UP:
				Y += north[x][y] ? 0 : SCALE;
				break;
			case KeyEvent.VK_DOWN:
				Y -= south[x][y] ? 0 : SCALE;
				break;
		}

		visited[x][y] = true;
		// System.out.println("[keyPressed]: X: " + X + ", Y: " + Y + ", KeyEvent: " + KeyEvent.getKeyText(e.getKeyCode()));

	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_P:
				solve();
				break;
			case KeyEvent.VK_R:
				restart();
				break;
		}

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

}