package br.com.unisul;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Learning extends Thread {

	private BufferedImage imagem;
	private double[][] w;
	private int QTD_PIXEL = 3;
	private double taxa = 0.0001;
	private int QTD_CLUSTER = 1;
	private int QTD_ITERACOES = 1;
	private Text textEpocas;
	private ScrolledComposite scrolledComposite;
	private Shell shell;
	private JFrame frame;
	private TreeMap<Integer, List<Integer[]>> grupos;
	private HashMap<Integer, Integer[]> mediaGrupos;
	private String nomeImagemOriginal;

	public Learning() {
		super();
	}
	
	public void treinar() {
		ImageDrawing desenhadora = new ImageDrawing();
		desenhadora.setLargura(imagem.getWidth());
		desenhadora.setAltura(imagem.getHeight());
		
		int[] imageInPixels = imagem.getRGB(0, 0, imagem.getWidth(), imagem.getHeight(), null, 0, imagem.getWidth());
		
		// Passo 2. Enquanto a condição de parada for falsa, faça
		for (int epoca = 0; epoca <= QTD_ITERACOES; epoca++) {
			//Passo 3. Para cada vetor de treinamento faça
			for (int i = 0; i < imageInPixels.length; i++) {
				int[] pixel = getPixels(imageInPixels[i]);
				
				// Passo 4. Para cada j calcule:
				int menorIndice = clusterizacao(pixel);
	
				// Passo 6. Para todas as unidades j em uma vizinhança	especificada e para todos os i atualize:
				atualizarPesos(menorIndice, pixel);
			}
			
			TreeMap<Integer, List<Integer[]>> grupos = new TreeMap<Integer, List<Integer[]>>();
			preencheGrupos(grupos);
			
			int[] pixelsAgrupados = agrupaPixels(grupos);
			
			desenhar(pixelsAgrupados, desenhadora, String.valueOf(epoca));
			
			// Passo 7. Alterar taxa de aprendizado
			taxa = (taxa - 0.01) < 0.01 ? 0.01 : taxa - 0.01;
			textEpocas.setText(String.valueOf(epoca));
			textEpocas.update();
		}
	}

	private int[] agrupaPixels(TreeMap<Integer, List<Integer[]>> grupos) {
		int cont = 0;
		int[] pixelsAgrupados = new int[imagem.getWidth()*imagem.getHeight()*3];
		for (Entry<Integer, List<Integer[]>> pixel : grupos.entrySet()) {
			for (Integer[] integers : pixel.getValue()) {
				pixelsAgrupados[cont++] = integers[0];
				pixelsAgrupados[cont++] = integers[1];
				pixelsAgrupados[cont++] = integers[2];
			}
		}
		
		return pixelsAgrupados;
	}

	private void desenhar(int[] pixelsAgrupados, ImageDrawing desenhadora, String titulo) {
		desenhadora.getPainel().removeAll();
		desenhadora.getPainel().add(desenhadora.createImageLabel(pixelsAgrupados));
		
		frame = desenhadora;
		
		frame.setTitle(titulo);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
		try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void atualizarPesos(int menorIndice, int[] pixel) {
		for (int i = 0; i < QTD_PIXEL; i++) {
			w[i][menorIndice] += taxa * (pixel[i] - w[i][menorIndice]); 

			int indiceMenos;
			int indiceMais;

			if (menorIndice == 0) {
				indiceMenos = QTD_CLUSTER-1;
				indiceMais = 1;
			} else if (menorIndice == QTD_CLUSTER-1) {
				indiceMenos = QTD_CLUSTER-2;
				indiceMais = 0;
			} else {
				indiceMenos = menorIndice - 1;
				indiceMais = menorIndice + 1;
			}

			w[i][indiceMenos] += taxa * (pixel[i] - w[i][indiceMenos]); 
			w[i][indiceMais] += taxa * (pixel[i] - w[i][indiceMais]); 
		}
	}

	public void classificar() {
		grupos = new TreeMap<Integer, List<Integer[]>>();
		preencheGrupos(grupos);
		mostraResultados(grupos);
		reconhecer();
		mostrarOriginal();
	}
	
	private void preencheGrupos(TreeMap<Integer, List<Integer[]>> grupos) {
		List<Integer[]> pixels;
		int[] imageInPixels = imagem.getRGB(0, 0, imagem.getWidth(), imagem.getHeight(), null, 0, imagem.getWidth());
		for (int i = 0; i < imageInPixels.length; i++) {
			int[] pixel = getPixels(imageInPixels[i]);
			Integer[] pixelDoGrupo = {pixel[0], pixel[1], pixel[2]};
			
			// Passo 4. Para cada j calcule:
			int menorIndice = clusterizacao(pixel);
			if ((pixels = grupos.get(menorIndice)) == null) {
				pixels = new ArrayList<Integer[]>();
				grupos.put(menorIndice, pixels);
			}
			pixels.add(pixelDoGrupo);
		}
	}

	private void mostraResultados(TreeMap<Integer, List<Integer[]>> grupos) {
	    mediaGrupos = new HashMap<Integer, Integer[]>();
		CLabel cluster = null;
		int y = 0;
		int x = 0;
		
		for (Entry<Integer, List<Integer[]>> pixel : grupos.entrySet()) {
			int qtdRed = 0, qtdGreen = 0, qtdBlue = 0;
			double avR, avG, avB, avC;
			for (Integer[] integers : pixel.getValue()) {
				qtdRed += integers[0];
				qtdGreen += integers[1];
				qtdBlue += integers[2];
			}
			avR = qtdRed / pixel.getValue().size();
			avG = qtdGreen / pixel.getValue().size();
			avB = qtdBlue / pixel.getValue().size();
			
			Integer[] pxMedia = {(int)avR, (int)avG, (int)avB};
			mediaGrupos.put(pixel.getKey(), pxMedia);
			
			avC = ((double)pixel.getValue().size() / (double)(imagem.getHeight()*imagem.getWidth())) * 100d;
			
			double areaCluster = (((80*450)/QTD_CLUSTER));
			int tamanhoCluster = (int) Math.sqrt(areaCluster) - 3;
			
			cluster = new CLabel(shell, SWT.BORDER | SWT.CENTER);
			cluster.setAlignment(SWT.CENTER);
			cluster.setBackground(new Color(shell.getDisplay(), (int)avR, (int)avG, (int)avB));
			cluster.setToolTipText("Cluster " + pixel.getKey() + " - " + new BigDecimal(avC).setScale(4, BigDecimal.ROUND_DOWN).doubleValue() + "%");
			
			if (x*tamanhoCluster > 450) {
				y += tamanhoCluster;
				x = 0;
			}

			cluster.setBounds(x*tamanhoCluster, y, tamanhoCluster, tamanhoCluster);
			cluster.setParent(scrolledComposite);
			
			cluster.setText(String.valueOf(new BigDecimal(avC).setScale(1, BigDecimal.ROUND_DOWN).doubleValue()));
			x++;
		}
	}
	
	private int clusterizacao(int[] pixel) {
		double menorValor = Double.MAX_VALUE;
		int menorIndice = 0;

		for (int j = 0; j < QTD_CLUSTER; j++) {
			double valor = 0;

			// D(j) = ∑i(wij−xi)² Distância Euclidiana 
			for (int i = 0; i < QTD_PIXEL; i++) {
//				valor += (w[i][j] - pixel[i]) < 0 ? (w[i][j] - pixel[i]) * -1 : (w[i][j] - pixel[i]);
				valor += Math.abs(w[i][j] - pixel[i]);
			}

			// Passo 5. Encontre o índice j onde D(j) seja mínimo.
			if (menorValor > valor) {
				menorValor = valor;
				menorIndice = j;
			}
		}
		return menorIndice;
	}
	
	public void reconhecer() {
	    ImageDrawing desenhadora = new ImageDrawing();
        desenhadora.setLargura(imagem.getWidth());
        desenhadora.setAltura(imagem.getHeight());
        
	    int[] imageInPixels = imagem.getRGB(0, 0, imagem.getWidth(), imagem.getHeight(), null, 0, imagem.getWidth());
	    int[] pixelSaida = new int[imageInPixels.length * 3];
	    int indexPixels = 0;
	    for (int i = 0; i < imageInPixels.length; i++) {
            int[] pixel = getPixels(imageInPixels[i]);
            
            int menorIndice = clusterizacao(pixel);

            pixelSaida[indexPixels++] = mediaGrupos.get(menorIndice)[0];
            pixelSaida[indexPixels++] = mediaGrupos.get(menorIndice)[1];
            pixelSaida[indexPixels++] = mediaGrupos.get(menorIndice)[2];
        }
	    
	    desenhar(pixelSaida, desenhadora, "Imagem Reduzida");
	    
	    BufferedImage image = new BufferedImage(imagem.getWidth(), imagem.getHeight(), BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();
        raster.setPixels(0, 0, imagem.getWidth(), imagem.getHeight(), pixelSaida);
        
        File diretorio = new File("C:\\Open Friday");
        if (!diretorio.exists())
            diretorio.mkdir();
        
        File f = new File("C:\\Open Friday\\" + nomeImagemOriginal.split("\\.")[0] + "_reduzida." + nomeImagemOriginal.split("\\.")[1]);
        try {
            ImageIO.write(image, "PNG", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void mostrarOriginal() {
	    ImageDrawing desenhadora = new ImageDrawing();
        desenhadora.setLargura(imagem.getWidth());
        desenhadora.setAltura(imagem.getHeight());
        
        int[] imageInPixels = imagem.getRGB(0, 0, imagem.getWidth(), imagem.getHeight(), null, 0, imagem.getWidth());
        int[] pixelSaida = new int[imageInPixels.length * 3];
        int indexPixels = 0;
        for (int i = 0; i < imageInPixels.length; i++) {
            int[] pixel = getPixels(imageInPixels[i]);
            pixelSaida[indexPixels++] = pixel[0];
            pixelSaida[indexPixels++] = pixel[1];
            pixelSaida[indexPixels++] = pixel[2];
        }
        
        desenhar(pixelSaida, desenhadora, "Imagem Original");
	}
	
	public void inicializarPesosRandomicos() {
		w = new double[QTD_PIXEL][QTD_CLUSTER];

		for (int i = 0; i < QTD_PIXEL; i++) {
			for (int j = 0; j < QTD_CLUSTER; j++) {
				w[i][j] = (Math.random());
			}
		}
	}
	
	private int[] getPixels(int sumPixel) {
		//					RED								GREEN						BLUE
		return new int[]{(sumPixel & 0x00ff0000) >> 16, (sumPixel & 0x0000ff00) >> 8, (sumPixel & 0x000000ff)};
	}

	public BufferedImage getImagem() {
		return imagem;
	}

	public void setImagem(BufferedImage imagem) {
		this.imagem = imagem;
	}

	public double getTaxa() {
		return taxa;
	}

	public void setTaxa(double taxa) {
		this.taxa = taxa;
	}

	public int getQTD_CLUSTER() {
		return QTD_CLUSTER;
	}

	public void setQTD_CLUSTER(int qTD_CLUSTER) {
		QTD_CLUSTER = qTD_CLUSTER;
	}

	public int getQTD_ITERACOES() {
		return QTD_ITERACOES;
	}

	public void setQTD_ITERACOES(int qTD_ITERACOES) {
		QTD_ITERACOES = qTD_ITERACOES;
	}

	public Text getTextEpocas() {
		return textEpocas;
	}

	public void setTextEpocas(Text textEpocas) {
		this.textEpocas = textEpocas;
	}

	public ScrolledComposite getScrolledComposite() {
		return scrolledComposite;
	}

	public void setScrolledComposite(ScrolledComposite scrolledComposite) {
		this.scrolledComposite = scrolledComposite;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

    public String getNomeImagemOriginal() {
        return nomeImagemOriginal;
    }

    public void setNomeImagemOriginal(String nomeImagemOriginal) {
        this.nomeImagemOriginal = nomeImagemOriginal;
    }
}