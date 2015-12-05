package br.com.unisul;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Window extends JFrame {  
	private static final long serialVersionUID = 4930891648824266753L;
	private BufferedImage imagem;

	public Window() {
		super("Learning Process");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g.create();
		g2.drawImage(imagem, 0, 0, null);
	}

	public BufferedImage getImagem() {
		return imagem;
	}

	public void setImagem(BufferedImage imagem) {
		this.imagem = imagem;
	}
}