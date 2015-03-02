/*
 * Clase que permite esconder textos en imágenes sin perturbar
 * demasiado la imagen.
 */
package esteganografia;

/**
 *
 * @author guillermo
 */

public class Esteganografia {
    
    static final String caracteres = 
      "abcdefghijklmnopqrstuvwxyzŕâéčęëîďôůűü˙ç" +
      "ABCDEFGHIJKLMNOPQRSTUVWXYZŔÂČÉĘËÎĎÔŮŰÜÇ" +
      "0123456789" +
      " ,.;:!?'Ťť\"" +
      "()+-*/=<>[]{}|%" +
      "&~#@\\^" +
      "\t\n\r" +
      "§"; // <- Caracter final
    
    static int rango(char c) {
        //devuelve el rango de un caracter
        //rango: posición del caracter en el string caracteres
        //Si no está en ese string devuelve la longitud de caracteres
        int i = 0;
        while(i<caracteres.length() && caracteres.charAt(i)!=c) {
            i++;
        }
        return i;
    }
    
    static PixelRGB escondeCaracter(PixelRGB pixel, char c) {
        //Esconde un caracter en un pixel
        
        int r = pixel.getR(); //Devuelve la componente roja del pixel
        int g = pixel.getR(); //Devuelve la componente verde del pixel
        int b = pixel.getB(); //Devuelve la componente azul del pixel
        
        int cc = rango(c); //la posición de c en caracteres
        //Si c no está en caracteres, se sustituye por una interrogación
        if (cc == caracteres.length()) {
            cc = rango('?');
        }
        
        //Convertimos el valor del rango a base 5
        //En base 5 el rango está formado por 3 cifras
        //Cada una de estas cifras se guarda en cr, cg y cb
        int cr = (cc/25)%5; 
	int cg = (cc/5)%5;
	int cb = cc%5;
        
        //Cambia las componentes del pixel original
        //Sustituimos el bit menos significativo de cada pixel por las cifras creadas
        int rr = (r/5)*5+cr;
	int gg = (g/5)*5+cg;
	int bb = (b/5)*5+cb;
        
        if (rr>255) {
            rr = rr - 5;
	}
	if (gg > 255) {
            gg = gg-5;
	}
	if (bb > 255) {
            bb = bb-5;
	}
        
        return new PixelRGB(rr, gg, bb);
    }
    
    static char extraeCaracter(PixelRGB pixel) {
        //Extrae el caracter escondido en un pixel
        
        char x;
        
        int cr = pixel.getR();
        int cg = pixel.getG();
        int cb = pixel.getB();
        
        int rango = (cr%5)*25 + (cg%5)*5 + cb%5; //rango en base 10
        
        x = caracteres.charAt(rango);
        
        return x;
    }
    
    static void escondeTexto(ImageRGB img, String texto) {
        
        int k = 0;
        texto = texto + "§";
        
        for (int i = 0 ; i < img.altura() && k<texto.length()  ; i++) {
            for (int j = 0 ; j < img.ancho() && k<texto.length() ; j++) {
		 PixelRGB p = escondeCaracter(img.getPixelRGB(i,j), texto.charAt(k));
		 img.setPixelRGB(i, j, p);
		 k++;  
            }
	}
        
    }
    
    static String extraeTexto(ImageRGB img) {
        
        String texto = "";
	  
	boolean b = true;
	  
	for (int i = 0 ; i<img.altura() && b ; i++) {
            for (int j = 0 ; j<img.ancho() && b ; j++) {
		texto = texto + extraeCaracter(img.getPixelRGB(i, j));
		if (extraeCaracter(img.getPixelRGB(i, j)) == '§') {
                    b = false;
		}
            }
	}
	  
  	return texto;
        
    }

}
