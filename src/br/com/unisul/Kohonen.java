package br.com.unisul;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Kohonen {

    /** Vetor de entrada */
    private int[] x;
    
    /** Mapa contendo os pesos */
    private double[][] w;
    
    /** Taxa de aprendizado */
	private double taxaAprendizado;
	
	/** Raio */
	private int raio = 1;
	
	 /** Vetor de entrada */
    private int[] entradaArquivoTeste;
    
	public Kohonen() {
		super();
		x = new int[63];
		taxaAprendizado = 0.6;
		inicializarPesosRandomicos();
	}

	private void inicializarPesosRandomicos() {
		Random random = new Random();
		w = new double[63][25];

		for (int i = 0; i < 63; i++) {
			for (int j = 0; j < 25; j++) {
				w[i][j] = (random.nextDouble() + 2) - 1;
			}
		}
	}

	public void inicializarEntrada(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String leitor;
			int pos = 0;
			
			while ((leitor = br.readLine()) != null) {
				for (int i = 0; i < 7; i++) {
					if (leitor.substring(i, i+1).equals("#"))
						x[pos] = 1;
					else 
						x[pos] = -1;
					
					pos++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public void inicializarArquivoTeste() {
		entradaArquivoTeste = new int[63];
		BufferedReader br = null;
		
		try {
			File file = new File("testes/teste.txt");
			br = new BufferedReader(new FileReader(file));
			String leitor;  
			int pos = 0;
			
			while ((leitor = br.readLine()) != null) {
				for (int i = 0; i < 7; i++) {
					if (leitor.substring(i, i+1).equals("#"))
						entradaArquivoTeste[pos] = 1;
					else if (leitor.substring(i, i+1).equals("."))
						entradaArquivoTeste[pos] = -1;
					else 
						entradaArquivoTeste[pos] = 0;
					
					pos++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
	}

	public int[] getX() {
		return x;
	}

	public void setX(int[] x) {
		this.x = x;
	}

	public double[][] getW() {
		return w;
	}

	public void setW(double[][] w) {
		this.w = w;
	}

	public double getTaxaAprendizado() {
		return taxaAprendizado;
	}

	public void setTaxaAprendizado(double taxaAprendizado) {
		this.taxaAprendizado = taxaAprendizado;
	}

	public int getRaio() {
		return raio;
	}

	public void setRaio(int raio) {
		this.raio = raio;
	}

	public int[] getEntradaArquivoTeste() {
		return entradaArquivoTeste;
	}

	public void setEntradaArquivoTeste(int[] entradaArquivoTeste) {
		this.entradaArquivoTeste = entradaArquivoTeste;
	}

}