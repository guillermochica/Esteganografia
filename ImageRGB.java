/*
 * Clase para cargar, crear y manejar imágenes BMP
 */
package esteganografia;


import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class ImageRGB {

  private BufferedImage img;
  
  public ImageRGB(String f){
  // Requisito : f (o f.bmp) es el nombre de un fichero .bmp
  // imagen RGB construida a aprtir del fichero f
  // (o f.bmp si falta la extensión en el nombre)
	if(f.length()<4 || !f.substring(f.length()-4,f.length()).equals(".bmp")){f=f+".bmp";}
	try{
	  DataInputStream inBMP = new DataInputStream(new FileInputStream(new File(f)));
	  inBMP.skipBytes(18); // salta la cabecera
	  // ancho y alto de la imagen
	  int ancho = leeEntero32bits(inBMP); int  altura = leeEntero32bits(inBMP);      
	  inBMP.skipBytes(28); // salta los datos no útiles de la cabecera
	  // crea y rellena img
	  img = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
	  int tamanoRelleno = (4-((ancho*3) % 4))%4; // numero de bytes de relleno en cada linea
	  // Lectura de pixels
		for(int y=altura-1; y>=0; y--){
		  for(int x=0; x<ancho; x++){img.setRGB(x, y, leePixel24bits(inBMP));}      
		  inBMP.skipBytes(tamanoRelleno); // salta el relleno
	   } 
	   inBMP.close();
	}
	catch(Exception e){ System.out.println("Problema con la lectura del fichero "+f);}
  }

  private ImageRGB (int ancho, int altura){
	  // imagen no definida de ancho x alto pixeles
		img = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
  }
  
  public static ImageRGB unir(int ancho, int altura, PixelRGB pix){
  // resultado: una imagen unida de ancho x alto pixeles igual a pix
	ImageRGB iRGB = new ImageRGB(ancho, altura);
	for(int i=0; i<altura; i++){
	  for(int j=0; j<ancho; j++){iRGB.setPixelRGB(i, j, pix);}   
	} 
	return iRGB;
  }
  
  public static ImageRGB blanco(int ancho, int altura){
  // resultado: una imagen blanca de ancho x alto pixeles
    return unir(ancho, altura, PixelRGB.blanco());
  }
  
  public static ImageRGB negro(int ancho, int altura){
  // resultado: una imagen negra de ancho x alto pixeles
	return unir(ancho, altura, PixelRGB.negro());
  }
  
  private static int leeEntero32bits(DataInputStream in){
  // efecto: lectura de un entero sobre 4 bytes desde in
  // resultado: el entero leido
    byte[] b = new byte[4];
    int resul = 0;
    try{
      in.read(b);
      resul = b[0] & 0xFF;
      resul = resul + ((b[1]&0xFF) << 8);
      resul = resul + ((b[2]&0xFF) << 16);
      resul = resul + ((b[3]&0xFF) << 24);	
    }
    catch(Exception e){}
    return resul;
  }

   
  private static int leePixel24bits(DataInputStream in){
  // efecto: lectura de un pixel sobre 3 bytes desde in
  // resultado: el entero correspondiente al pixel leido
    byte[] b = new byte[3];
    int result = 0;
    try{
      in.read(b);  		
      result = b[0] & 0xFF;
      result = result + ((b[1] & 0xFF) << 8);
      result = result + ((b[2] & 0xFF) << 16);
    }
    catch(Exception e){}
    return result;
  }


  public boolean guardaBitMap(String f) {
  // efecto: crea a partir de this un fichero bit-map de nombre f
  // o f.bmp si falta la extensión en el nombre
  // efecto: indica si se ha guardado el archivo
    if(f.length()<4 || !f.substring(f.length()-4,f.length()).equals(".bmp")){f=f+".bmp";}
    try{
      DataOutputStream outBMP = new DataOutputStream(new FileOutputStream(f));
      //escritura d ela cabecera del fichero
      outBMP.write(0x42); outBMP.write(0x4D); //Tipo
      int ancho = img.getWidth(); int altura = img.getHeight();
      int tamanoRelleno =(4-((ancho*3) % 4))%4; // número de bytes de relleno al final de cada línea
      escribeEntero32bits(outBMP,altura*(ancho*3+tamanoRelleno)+54); // Tamaño del fichero
      escribeEntero32bits(outBMP,0); // Reservado
      escribeEntero32bits(outBMP,54);escribeEntero32bits(outBMP,40);
      escribeEntero32bits(outBMP,ancho); // Ancho
      escribeEntero32bits(outBMP,altura); // Alto
      outBMP.write(1); outBMP.write(0); 
      outBMP.write(24); outBMP.write(0); // Número de bits por pixel
      escribeEntero32bits(outBMP,0); // sin compresión
      escribeEntero32bits(outBMP,altura*(ancho*3+tamanoRelleno)); // tamaño de la imagen en bytes
      escribeEntero32bits(outBMP,2851); escribeEntero32bits(outBMP,2851); // resolución en ancho x alto
      escribeEntero32bits(outBMP,0); // numero de colores = 2^^24
      escribeEntero32bits(outBMP,0); // todos los colores son importantes
      //Escritura del cuerpo del fichero BMP
      for (int y=altura-1; y>=0; y--) {
      	//System.out.print(" linea "+y);
        for (int x=0; x<ancho; x++) {escribePixel24bits(outBMP, img.getRGB(x,y));}
        //Relleno
        for (int j=0; j<tamanoRelleno; j++) {outBMP.write(0);}
      }
      //Cierra fichero
      outBMP.close();
      return true;
    }
    catch(Exception e){System.out.println("ERROR : "+e); return false;}
  }

  private static void escribeEntero32bits(DataOutputStream salida, int n) {
  // efecto: escribe n en la salida sobre 4 bytes según el formato littel indian
    try {
      salida.write((n) & 0xFF);
      salida.write((n>>8) & 0xFF);
      salida.write((n>>16) & 0xFF);
      salida.write((n>>24) & 0xFF);
    }
    catch (Exception e) {}
  }
   
  private static void escribePixel24bits(DataOutputStream salida, int p) {
  // efecto: escribe el pixel correspondiente a p en la salida sobre 3 bytes
    try {
      salida.write((p) & 0xFF);
      salida.write((p>>8) & 0xFF);
      salida.write((p>>16) & 0xFF);
    }
    catch (Exception e) {}
  }
  
  public int altura(){ // la altura de this
	  return img.getHeight(); 
  }
  
  public int ancho(){ // el ancho de this
	  return img.getWidth();
  }
  
  public PixelRGB getPixelRGB(int i, int j){
  // requisito : 0<=i<this.altura() et 0<=j<this.ancho()
  // resultado : el pixel en posición (i,j)
  
	  int p=img.getRGB(j,i);
	  return new PixelRGB((p>>16)&0xFF,(p>>8)&0xFF,p&0xFF);
  }
  
  public void setPixelRGB(int i, int j, PixelRGB pix){
  // requisito : 0<=i<this.altura() et 0<=j<this.ancho()
  // efecto : cambia por pix el pixel en posición (i,j)
	  int p=pix.getB()+(pix.getG()<<8) + (pix.getR()<<16);
	  img.setRGB(j,i,p);
  }
}
