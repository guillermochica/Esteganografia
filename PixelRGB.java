/*
 * Clase para crear y manejar pixeles
 */
package esteganografia;


public class PixelRGB {
  private int r; private int g; private int b;
  
  //constructor
  public PixelRGB(int r, int g, int b){
  // requisito : 0<=r<256 y 0<=g<256 y 0<=b<256
  // pixel de componentes r, g y b
	this.r=r; this.g=g; this.b=b;
  }
 
  public static PixelRGB blanco(){ // resultado: un pixel blanco
	return new PixelRGB(255,255,255);
  }
  public static PixelRGB negro(){ // resultado: un pixel negro
		return new PixelRGB(0,0,0);
  }
  public static PixelRGB gris(int v){ // resultado: un pixel gris de intensidad v
		return new PixelRGB(v,v,v);
  } 
  public int getR(){return r;} // resultado: la componente roja de this
  public int getG(){return g;} // resultado: la componente verde de this
  public int getB(){return b;} // resultado: la componente azul de this

  public String toString(){
  // resultado : this en la forma "<r,g,b>"
  	return "<"+r+","+g+","+b+">";
  }  
}
