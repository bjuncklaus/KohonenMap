package br.com.unisul;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class Tela {
	private static Button btnIniciar;
	private static Spinner spinnerTaxa;
	private static Spinner spinnerIteracoes;
	private static Text textCaminho;
	private static Text textEpocas;
	private static JFileChooser upload;
	private static ScrolledComposite componenteImgSrc;
	private static ScrolledComposite scrolledComposite;
	private static Spinner spinnerCluster;
	private static Button btnLimpar;
	private static TabFolder tabFolder;
	
	public static void main (String [] args) {
		final Learning learning = new Learning();
		final Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setText("Kohonen SOM - Learning Process");
		shell.setSize(533, 474);
		shell.setLayout(null);
		
		learning.setShell(shell);
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 281, 498, 156);
		
		final TabItem tabClusters = new TabItem(tabFolder, SWT.NONE);
		tabClusters.setText("Clusters (%)");
		
		scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		tabClusters.setControl(scrolledComposite);
		learning.setScrolledComposite(scrolledComposite);
		
		spinnerIteracoes = new Spinner(shell, SWT.BORDER);
		spinnerIteracoes.setMaximum(300);
		spinnerIteracoes.setMinimum(1);
		spinnerIteracoes.setBounds(162, 13, 52, 21);
		
		Label lblQtdIteraes = new Label(shell, SWT.NONE);
		lblQtdIteraes.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQtdIteraes.setBounds(10, 12, 135, 18);
		lblQtdIteraes.setText("Quantidade itera\u00E7\u00F5es:");
		
		spinnerTaxa = new Spinner(shell, SWT.BORDER);
		spinnerTaxa.setDigits(2);
		spinnerTaxa.setMaximum(99);
		spinnerTaxa.setMinimum(1);
		spinnerTaxa.setBounds(162, 49, 52, 21);
		
		Label lblTaxaDeAprendizado = new Label(shell, SWT.NONE);
		lblTaxaDeAprendizado.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblTaxaDeAprendizado.setText("Taxa de aprendizado:");
		lblTaxaDeAprendizado.setBounds(10, 48, 135, 21);
		
		btnIniciar = new Button(shell, SWT.NONE);
		btnIniciar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (learning.getImagem() != null) {
					limpar(learning, tabClusters);
					
					learning.setTaxa(Double.parseDouble(spinnerTaxa.getText().replace(",", ".")));
					learning.setQTD_CLUSTER(Integer.parseInt(spinnerCluster.getText()));
					learning.setQTD_ITERACOES(Integer.parseInt(spinnerIteracoes.getText()));
					
					
					// Inicio do processo de aprendizagem
					learning.inicializarPesosRandomicos();
					learning.treinar();
					learning.classificar();
				} else {
					JOptionPane.showMessageDialog(null, "Selecione uma imagem de entrada.");
				}
			}
		});
		btnIniciar.setBounds(10, 130, 68, 23);
		btnIniciar.setText("Iniciar");
		
		textCaminho = new Text(shell, SWT.BORDER);
		textCaminho.setEditable(false);
		textCaminho.setBounds(237, 9, 236, 19);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				upload = new JFileChooser("C://");
				upload.setAcceptAllFileFilterUsed(true);
				upload.addChoosableFileFilter(new FileNameExtensionFilter("JPG", "jpg"));
				upload.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png")); 
				upload.addChoosableFileFilter(new FileNameExtensionFilter("GIF", "gif")); 
				
				File file = null;
				int retorno = upload.showOpenDialog(null);
				
				if (retorno == JFileChooser.OPEN_DIALOG) {
					textCaminho.setText(upload.getSelectedFile().getAbsolutePath());
				}
				
				if (!textCaminho.equals("")) {
					file = new File(textCaminho.getText());
					try {
						learning.setImagem(ImageIO.read(file));
						learning.setNomeImagemOriginal(file.getName());
						componenteImgSrc.setBackgroundImage(resize(new Image(null, file.getAbsolutePath()), learning.getImagem()));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btnNewButton.setBounds(479, 10, 29, 18);
		btnNewButton.setText("...");
		
		btnLimpar = new Button(shell, SWT.NONE);
		btnLimpar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				spinnerIteracoes.setValues(0, 1, 300, 0, 1, 10);
				spinnerCluster.setValues(0, 2, 100, 0, 1, 10);
				spinnerTaxa.setValues(0, 1, 99, 2, 1, 10);
				textCaminho.setText("");
				textEpocas.setText("");
				componenteImgSrc.setBackgroundImage(null);
				learning.setImagem(null);
				
				limpar(learning, tabClusters);
			}
		});
		btnLimpar.setBounds(140, 130, 68, 23);
		btnLimpar.setText("Limpar");
		
		componenteImgSrc = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		componenteImgSrc.setBounds(237, 46, 271, 229);
		componenteImgSrc.setExpandHorizontal(true);
		componenteImgSrc.setExpandVertical(true);
		
		textEpocas = new Text(shell, SWT.BORDER);
		textEpocas.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.BOLD));
		textEpocas.setEditable(false);
		textEpocas.setBounds(151, 233, 46, 19);
		learning.setTextEpocas(textEpocas);
		
		Label lblEpoca = new Label(shell, SWT.NONE);
		lblEpoca.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblEpoca.setBounds(10, 234, 49, 29);
		lblEpoca.setText("\u00C9poca:");
		
		Label lblQuantidadeDeClusters = new Label(shell, SWT.NONE);
		lblQuantidadeDeClusters.setText("Quantidade de clusters:");
		lblQuantidadeDeClusters.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQuantidadeDeClusters.setBounds(10, 85, 147, 18);
		
		spinnerCluster = new Spinner(shell, SWT.BORDER);
		spinnerCluster.setMinimum(2);
		spinnerCluster.setBounds(162, 86, 52, 21);
		
		
		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		learning.getFrame().dispose();
		display.dispose();
		
	}
	
	private static Image resize(Image image, BufferedImage imagem) {
		int width = 271;
		int height = 229;
		
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose();
		return scaled;
	}
	
	private static void limpar(final Learning learning, final TabItem tabClusters) {
		scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		tabClusters.setControl(scrolledComposite);
		learning.setScrolledComposite(scrolledComposite);
		
		if (learning.getFrame() != null)
			learning.getFrame().dispose();
	}
}