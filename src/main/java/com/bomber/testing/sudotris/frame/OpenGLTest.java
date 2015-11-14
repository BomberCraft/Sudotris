package com.bomber.testing.sudotris.frame;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;

public class OpenGLTest implements GLEventListener, KeyListener {

	private Frame frame;
	private GLCanvas canvas;
	private AnimatorBase animator;

	private int WIDTH = 512;
	private int HEIGHT = 512;

	private float posX = 0f;
	private float posY = 0f;

	private float rotX = 0f;
	private float rotY = 0f;

	public static void main(String[] args) {
		OpenGLTest sudotris = new OpenGLTest();
		sudotris.start();

	}

	public OpenGLTest() {
		this.frame = new Frame("Simple OpenGL frame");
		this.canvas = new GLCanvas();

		this.animator = new Animator(canvas);

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
	}

	public void start() {
		frame.setVisible(true);
		animator.start();

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
		System.err.println("INIT GL IS: " + gl.getClass().getName());
		System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));

		float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float mat_shininess[] = { 25.0f };
		float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };

		float red[] = { 0.8f, 0.1f, 0.0f, 0.7f };
		float yellow[] = { 0.8f, 0.75f, 0.0f, 0.7f };
		float blue[] = { 0.2f, 0.2f, 1.0f, 0.7f };
		float brown[] = { 0.8f, 0.4f, 0.1f, 0.7f };

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
		// gl.glClearColor((float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random());
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Get the GL corresponding to the drawable we are animating
		GL2 gl = drawable.getGL().getGL2();

		// gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Special handling for the case where the GLJPanel is translucent
		// and wants to be composited with other Java 2D content
		if (GLProfile.isAWTAvailable() && (drawable instanceof com.jogamp.opengl.awt.GLJPanel)
				&& !((com.jogamp.opengl.awt.GLJPanel) drawable).isOpaque()
				&& ((com.jogamp.opengl.awt.GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent()) {
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		} else {
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		}

		draw(drawable);

		gl.glFlush();
	}

	private void draw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// TODO Auto-generated method stub

		gl.glLoadIdentity();

		gl.glTranslatef(posX, posY, 0);

		gl.glRotatef(rotX, 1, 0, 0);
		gl.glRotatef(rotY, 0, 1, 0);

		gl.glColor4f(0, .2f, .7f, .8f);

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2f(-1, 0);
		gl.glVertex2f(1, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2f(0, -1);
		gl.glVertex2f(0, 1);
		gl.glEnd();

		gl.glColor3f(0, 0, 0);

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-.1f, -.1f);
		gl.glVertex2f(-.1f, .1f);
		gl.glVertex2f(.1f, .1f);
		gl.glVertex2f(.1f, -.1f);
		gl.glEnd();

		gl.glColor3f(.5f, .7f, .3f);

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(.2f, .2f);
		gl.glVertex2f(.2f, .8f);
		gl.glVertex2f(.8f, .8f);
		gl.glVertex2f(.8f, .2f);
		gl.glEnd();

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// System.err.println("Gears: Reshape " + x + "/" + y + " " + width + "x" + height);
		GL2 gl = drawable.getGL().getGL2();

		int swapInterval = 1;
		gl.setSwapInterval(swapInterval);

		float h = (float) height / (float) width;

		WIDTH = width;
		HEIGHT = height;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();

		// gl.glOrthof(-10, 10, -10, 10, -10, 10);

		if (h < 1)
			gl.glFrustum(-1.0f + posX, 1.0f + posX, -h + posY, h + posY, 0f, 60.0f);
		else {
			h = 1.0f / h;
			gl.glFrustum(-h + posX, h + posX, -1.0f + posY, 1.0f + posY, 0f, 60.0f);
		}

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -6.0f);
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
		float transScale = .05f;
		float rotScale = 1f;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_Z:
				posY += transScale;
				break;
			case KeyEvent.VK_S:
				posY -= transScale;
				break;
			case KeyEvent.VK_Q:
				posX -= transScale;
				break;
			case KeyEvent.VK_D:
				posX += transScale;
				break;

			case KeyEvent.VK_LEFT:
				rotY += rotScale;
				break;
			case KeyEvent.VK_RIGHT:
				rotY -= rotScale;
				break;
			case KeyEvent.VK_UP:
				rotX += rotScale;
				break;
			case KeyEvent.VK_DOWN:
				rotX -= rotScale;
				break;
		}

		System.out.println("posX: " + posX + " - posY: " + posY + " | rotX: " + rotX + " - rotY: " + rotY);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
