package br.com.unisul;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageDrawing extends JFrame {
	private static final long serialVersionUID = 7221591168312358927L;
	private int largura;
    private int altura;
    private JPanel painel = new JPanel();
    private BufferedImage imagem;
    
    public ImageDrawing() {
        getContentPane().add(painel);
    }

    public JLabel createImageLabel(int[] pixels) {
        montaImagem(pixels);
        JLabel label = new JLabel( new ImageIcon(imagem) );
        return label;
    }

	private void montaImagem(int[] pixels) {
		imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = imagem.getRaster();
        raster.setPixels(0, 0, largura, altura, pixels);
	}
    
	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public JPanel getPainel() {
		return painel;
	}

	public void setPainel(JPanel painel) {
		this.painel = painel;
	}

	public BufferedImage getImagem() {
		return imagem;
	}

	public void setImagem(BufferedImage imagem) {
		this.imagem = imagem;
	}
}