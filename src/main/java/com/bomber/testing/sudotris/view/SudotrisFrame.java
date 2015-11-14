package com.bomber.testing.sudotris.view;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.bomber.testing.sudotris.model.Sudotris;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.gl2.GLUT;

public class SudotrisFrame implements GLEventListener, KeyListener {

	private Frame frame;
	private GLCanvas canvas;
	private AnimatorBase animator;

	private GLU glu;
	private GLUT glut;

	/** Largeur de la Frame */
	private int WIDTH = 512;
	/** Hauteur de la Frame */
	private int HEIGHT = 512;

	/** Valeur de position sur l'axe X */
	private float posX;
	/** Valeur de position sur l'axe Y */
	private float posY;

	/** Valeur de rotation sur l'axe X */
	private float rotX;
	/** Valeur de rotation sur l'axe Y */
	private float rotY;
	/** Valeur de rotation sur l'axe Z */
	private float rotZ;

	/** Valeur du zoom */
	private float zoom;

	/** Facteur de translation */
	private float transScale;
	/** Facteur de rotation */
	private float rotScale;
	/** Facteur de zoom */
	private float zoomScale;

	/** Taille de la grille */
	private int gridSize;
	/** Le Sudotris */
	private Sudotris sudotris;

	private float[] curXY;
	/** Curseur sur l'axe X par rapport à la frame */
	private float curX;
	/** Curseur sur l'axe Y par rapport à la frame */
	private float curY;
	/** Curseur sur l'axe X par rapport à la matrice */
	private int gridX;
	/** Curseur sur l'axe Y par rapport à la matrice */
	private int gridY;

	private Thread game;
	private float gameSpeed;
	private boolean hardMode;
	private int nextNumber;
	private float nextNumberZ;
	private float nextNumberZOriginal;
	private float nextNumberScale;
	private Integer[] allNumbersToPlaces;
	private boolean suspend;
	private boolean solving;

	public static void main(String[] args) {
		SudotrisFrame sudotris = new SudotrisFrame();
		sudotris.start();
	}

	private void setViewDefaults() {
		posX = 0f;
		posY = 0f;

		rotX = 0f;
		rotY = 0f;
		rotZ = 0f;

		zoom = 1f;

		transScale = .5f;
		rotScale = 1f;
		zoomScale = 10f;

		gameSpeed = 80;
		hardMode = true;

		nextNumberZOriginal = -30;
		nextNumberScale = .05f;
	}

	private void setRuntimeDefaults() {
		nextNumberZ = nextNumberZOriginal;
		suspend = false;
		solving = false;
	}

	public SudotrisFrame() {
		setViewDefaults();
		setRuntimeDefaults();

		this.frame = new Frame("Simple OpenGL frame");
		this.canvas = new GLCanvas();

		this.animator = new Animator(canvas);

		this.glu = new GLU();
		this.glut = new GLUT();

		canvas.addGLEventListener(this);
		frame.addKeyListener(this);
		canvas.addKeyListener(this);

		frame.add(canvas);

		frame.setSize(WIDTH, HEIGHT);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				new Thread(new Runnable() {

					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});

		gridSize = 9;

		sudotris = new Sudotris(gridSize);

		game = new Thread(new Runnable() {

			@Override
			@SuppressWarnings("static-access")
			public void run() {
				while (0 != 1) {
					try {
						game.sleep((long) (1000 * (1 / gameSpeed)));
					} catch (InterruptedException e) {
						return;
					}

					if (suspend) {
						try {
							game.sleep(100);
							continue;
						} catch (InterruptedException e) {
							return;
						}
					}

					allNumbersToPlaces = sudotris.getAllNumbersToPlace();
					int length = allNumbersToPlaces.length;
					if (length == 0) {
						nextNumber = 0;
						return;
					}

					if (nextNumberZ > -.3f) {
						if (sudotris.isMoveValid(gridX, gridY, nextNumber)
								|| (hardMode && sudotris.getCell(gridX, gridY) == 0)) {
							sudotris.setCell(gridX, gridY, nextNumber);
						}

						allNumbersToPlaces = sudotris.getAllNumbersToPlace();
						length = allNumbersToPlaces.length;
						nextNumber = allNumbersToPlaces[(int) (Math.random() * 1000 % length)];
						nextNumberZ = nextNumberZOriginal;
					} else {
						nextNumberZ += nextNumberScale;
					}
				}

			}
		});

		allNumbersToPlaces = sudotris.getAllNumbersToPlace();
		int length = allNumbersToPlaces.length;
		nextNumber = allNumbersToPlaces[(int) (Math.random() * 1000 % length)];

		System.err.println("NextNumber: " + nextNumber);
	}

	public void start() {
		frame.setVisible(true);
		animator.start();
		game.start();

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
		System.err.println("INIT GL IS: " + gl.getClass().getName());
		System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));

		// float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		// float mat_shininess[] = { 25.0f };
		// float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };

		drawable.getAnimator().setUpdateFPSFrames(3, null);

		gl.glShadeModel(GL2.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Testing To Do

		/* Really Nice Perspective Calculations */
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_TEXTURE_2D);

		/* Texture filter */
		// gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NONE);
		// gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NONE);

		/* Light and material */
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
		// gl.glEnable(GL2.GL_LIGHTING);
		// gl.glEnable(GL2.GL_LIGHT0);

		initDraw(drawable);

		gl.glEnable(GL2.GL_NORMALIZE);
	}

	private void initDraw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(1, 1, 1, 1);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		/* Special handling for the case where the GLJPanel is translucent */
		/* and wants to be composited with other Java 2D content */
		if (GLProfile.isAWTAvailable() && (drawable instanceof com.jogamp.opengl.awt.GLJPanel)
				&& !((com.jogamp.opengl.awt.GLJPanel) drawable).isOpaque()
				&& ((com.jogamp.opengl.awt.GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent()) {
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		} else {
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		}

		setCamera(drawable);

		draw(drawable);

		gl.glFlush();
	}

	private void draw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glLoadIdentity();
		drawFPS(drawable);

		gl.glTranslatef(posX, posY, 0);

		gl.glRotatef(rotX, 1, 0, 0);
		gl.glRotatef(rotY, 0, 1, 0);
		gl.glRotatef(rotZ, 0, 0, 1);

		drawCore(drawable);

		// drawRepair(drawable);

		drawCoordinate(drawable);
	}

	private void drawCore(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		/* fond de la grille */
		gl.glColor4f(.9f, .9f, .9f, .9f);
		drawWall(drawable, 0f, 0f, .15f, gridSize, gridSize, .2f);

		/* grille */
		gl.glColor4f(.7f, .7f, .7f, .9f);
		for (int i = 0; i <= gridSize; i++) {
			float[] curXY = getCurFromGrid(0, i);
			float t = curXY[0];

			t = t - .5f;

			drawWall(drawable, t, 0, 0, .1f, gridSize, .5f);
			drawWall(drawable, 0, t, 0, gridSize, .1f, .5f);
		}

		/* cellule sélectionnée */
		curXY = getCurFromGrid(gridX, gridY);
		curX = curXY[0];
		curY = curXY[1];
		gl.glColor4f(.9f, .9f, .2f, .2f);
		drawWall(drawable, curX, curY, 0, 1, 1, .1f);

		/* valeur des cellules */
		for (int cellX = 0; cellX < gridSize; cellX++) {
			for (int cellY = 0; cellY < gridSize; cellY++) {
				int cell = sudotris.getCell(cellX, cellY);

				if (cell == 0) {
					continue;
				}

				if (sudotris.isCellOriginal(cellX, cellY)) {
					gl.glColor3f(0, 0, 0);
				} else {
					if (sudotris.isCellCorrect(cellX, cellY)) {
						gl.glColor3f(.2f, .8f, .2f);
					} else {
						gl.glColor3f(.9f, .2f, .2f);
					}
				}

				curXY = getCurFromGrid(cellX, cellY);
				curX = curXY[0];
				curY = curXY[1];

				String cellValue = Integer.toString(cell);
				gl.glRasterPos3d(curX, curY, -.15f);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, cellValue);
			}
		}

		/* Draw nextNumber */
		curXY = getCurFromGrid(gridX, gridY);
		curX = curXY[0];
		curY = curXY[1];

		if (nextNumber != 0) {
			String cellValue = Integer.toString(nextNumber);
			gl.glColor3f(.2f, .2f, .7f);
			gl.glRasterPos3d(curX, curY, nextNumberZ);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, cellValue);

			if (sudotris.getCell(gridX, gridY) == 0) {
				gl.glColor3f(.4f, .4f, .4f);
				gl.glRasterPos3d(curX, curY, -.3f);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, cellValue);
			}
		}
	}

	private void setCamera(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		/* Change to projection matrix. */
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		/* Perspective. */
		float h = (float) WIDTH / (float) HEIGHT;

		glu.gluPerspective(45, -h, 1, 10000);
		glu.gluLookAt(posX, posY, zoom - 45, 0, 0, 0, 0, 1, 0);

		/* Change back to model view matrix. */
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	private void drawFPS(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		float time = drawable.getAnimator().getLastFPS();

		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		int[] viewport = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		glu.gluOrtho2D(0, viewport[2], viewport[3], 0);
		gl.glDepthFunc(GL2.GL_ALWAYS);
		gl.glColor4f(1, .2f, .2f, 1);
		gl.glRasterPos2d(15, 30);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "FPS: " + time);
		gl.glDepthFunc(GL2.GL_LESS);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
	}

	// Debug
	@SuppressWarnings("unused")
	private void drawRepair(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glColor4f(0, .4f, .9f, 1f);

		gl.glBegin(GL2.GL_LINES);
		/* axe X- */
		gl.glColor3f(1, 0, 1);
		gl.glVertex3f(-10, 0, 0);
		/* axe X+ */
		gl.glColor3f(1, 0, 0);
		gl.glVertex3f(10, 0, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		/* axe Y- */
		gl.glColor3f(0, 1, 1);
		gl.glVertex3f(0, -10, 0);
		/* axe Y+ */
		gl.glColor3f(1, 0, 0);
		gl.glVertex3f(0, 10, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		/* axe Z- */
		gl.glColor3f(1, 1, 0);
		gl.glVertex3f(0, 0, -10);
		/* axe Z+ */
		gl.glColor3f(1, 0, 0);
		gl.glVertex3f(0, 0, 10);
		gl.glEnd();
	}

	private void drawCoordinate(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		int[] viewport = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		glu.gluOrtho2D(0, viewport[2], 0, viewport[3]);
		gl.glDepthFunc(GL2.GL_ALWAYS);
		gl.glColor4f(1, .2f, .2f, 1);
		gl.glRasterPos2d(15, 30);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "posX: " + posX + " - posY: " + posY + " | rotX: " + rotX
				+ " - rotY: " + rotY + " - rotZ: " + rotZ + " | zoom: " + zoom);
		gl.glDepthFunc(GL2.GL_LESS);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	/**
	 * <pre>
	 * Dessine un pavé: <br/>
	 * 		- de centre ({@code centreX}, {@code centreY}, {@code centreZ})
	 * 		- de longeur {@code sizeY} sur l'axe Y
	 * 		- de largeur {@code sizeX} sur l'axe X
	 * 		- de hauteur {@code sizeZ} sur l'axe Z
	 * </pre>
	 * 
	 * @param drawable
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param sizeX
	 * @param sizeY
	 * @param sizeZ
	 */
	private void drawWall(GLAutoDrawable drawable, float centerX, float centerY, float centerZ, float sizeX,
			float sizeY, float sizeZ) {
		GL2 gl = drawable.getGL().getGL2();

		sizeX /= 2;
		sizeY /= 2;
		sizeZ /= 2;

		gl.glTranslatef(centerX, centerY, centerZ);

		gl.glBegin(GL2.GL_QUADS); // Begin drawing the color cube with 6 quads
		// Top face
		// Define vertices in counter-clockwise (CCW) order with normal pointing out
		gl.glVertex3f(sizeX, sizeY, sizeZ);
		gl.glVertex3f(-sizeX, sizeY, sizeZ);
		gl.glVertex3f(-sizeX, -sizeY, sizeZ);
		gl.glVertex3f(sizeX, -sizeY, sizeZ);

		// Bottom face
		gl.glVertex3f(sizeX, sizeY, -sizeZ);
		gl.glVertex3f(-sizeX, sizeY, -sizeZ);
		gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
		gl.glVertex3f(sizeX, -sizeY, -sizeZ);

		// Front face
		gl.glVertex3f(sizeX, -sizeY, sizeZ);
		gl.glVertex3f(-sizeX, -sizeY, sizeZ);
		gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
		gl.glVertex3f(sizeX, -sizeY, -sizeZ);

		// Back face
		gl.glVertex3f(sizeX, sizeY, sizeZ);
		gl.glVertex3f(-sizeX, sizeY, sizeZ);
		gl.glVertex3f(-sizeX, sizeY, -sizeZ);
		gl.glVertex3f(sizeX, sizeY, -sizeZ);

		// Left face
		gl.glVertex3f(-sizeX, -sizeY, sizeZ);
		gl.glVertex3f(-sizeX, sizeY, sizeZ);
		gl.glVertex3f(-sizeX, sizeY, -sizeZ);
		gl.glVertex3f(-sizeX, -sizeY, -sizeZ);

		// Right face
		gl.glVertex3f(sizeX, sizeY, sizeZ);
		gl.glVertex3f(sizeX, -sizeY, sizeZ);
		gl.glVertex3f(sizeX, -sizeY, -sizeZ);
		gl.glVertex3f(sizeX, sizeY, -sizeZ);
		gl.glEnd();

		gl.glTranslatef(-centerX, -centerY, -centerZ);
	}

	private float[] getCurFromGrid(int x, int y) {
		float tx = (y - gridSize / 2);
		float ty = -(x - gridSize / 2);

		return new float[] { tx, ty };
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		gl.setSwapInterval(1);

		WIDTH = width;
		HEIGHT = height;

		// gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();

		int size = 5 * 2;
		gl.glOrthof(-size, size, -size, size, size, -size);
		float h = (float) HEIGHT / (float) WIDTH;

		float left;
		float right;
		float bottom;
		float top;

		if (h < 1) {
			left = -1.0f;
			right = 1.0f;
			bottom = -h;
			top = h;
		} else {
			h = 1.0f / h;
			left = -h;
			right = h;
			bottom = -1.0f;
			top = 1.0f;
		}

		gl.glFrustum(left, right, bottom, top, 0f, 60.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// gl.glLoadIdentity();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {

			/** Bloc pour déplacer la cellule sélectionnée */
			case KeyEvent.VK_UP:
				if (gridX > 0)
					gridX--;
				break;
			case KeyEvent.VK_DOWN:
				if (gridX < gridSize - 1)
					gridX++;
				break;
			case KeyEvent.VK_LEFT:
				if (gridY > 0)
					gridY--;
				break;
			case KeyEvent.VK_RIGHT:
				if (gridY < gridSize - 1)
					gridY++;
				break;

			/** Bloc pour déplacer la grille */
			case KeyEvent.VK_NUMPAD8:
				posY += transScale;
				break;
			case KeyEvent.VK_NUMPAD2:
				posY -= transScale;
				break;
			case KeyEvent.VK_NUMPAD4:
				posX -= transScale;
				break;
			case KeyEvent.VK_NUMPAD6:
				posX += transScale;
				break;

			/** Bloc pour orienter la grille */
			case KeyEvent.VK_A:
				rotZ += rotScale;
				break;
			case KeyEvent.VK_E:
				rotZ -= rotScale;
				break;
			case KeyEvent.VK_Z:
				rotX += rotScale;
				break;
			case KeyEvent.VK_S:
				rotX -= rotScale;
				break;
			case KeyEvent.VK_Q:
				rotY += rotScale;
				break;
			case KeyEvent.VK_D:
				rotY -= rotScale;
				break;

			/** Bloc pour gérer le zoom */
			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_NUMPAD3:
				zoom -= zoomScale;
				if (zoom == 0) {
					zoom -= zoomScale;
				}
				break;
			case KeyEvent.VK_PAGE_UP:
			case KeyEvent.VK_NUMPAD9:
				zoom += zoomScale;
				if (zoom == 0) {
					zoom += zoomScale;
				}
				break;

			/** Bloc pour réinitialiser la vue */
			case KeyEvent.VK_C:
			case KeyEvent.VK_NUMPAD5:
				setViewDefaults();
				break;

			case KeyEvent.VK_P:
				suspend = !suspend;
				System.err.println((suspend ? "Paused" : "Resumed") + " the game");
				break;

			case KeyEvent.VK_I:
				if (!solving) {
					solving = true;
					System.out.println("Solving current");
					new Thread(new Runnable() {
						public void run() {
							sudotris.solve();
							solving = false;
						}
					}).start();
				}
				break;

			case KeyEvent.VK_R:
				setRuntimeDefaults();
				sudotris = new Sudotris(gridSize);
				break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
