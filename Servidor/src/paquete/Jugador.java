package paquete;

import java.io.Serializable;

public class Jugador implements Serializable{
	/**
	 Clase que almacena el nombre del jugador, su vida, y la acción de cada turno
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private int vida;
	private Accion accionActual;
	
	public Jugador(String nombre) {
		super();
		this.nombre = nombre;
		this.vida = 100;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}

	public Accion getAccionActual() {
		return accionActual;
	}

	public void setAccionActual(Accion accionActual) {
		this.accionActual = accionActual;
	}
	
	// Método para restar el daño a la vida actual.
	public void daño(int cantidad) {
		
		// De esta forma se evita que la cantidad de vida llegue a ser negativa.
		this.vida = Math.max(vida - cantidad, 0);
	}
	
	// Método para saber si el jugador sigue en pie (vivo).
	public boolean estaVivo() {
		return vida > 0;
	}
}
