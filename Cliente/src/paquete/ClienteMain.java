package paquete;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteMain {

	public static void main(String[] args) throws IOException {
		// Declaración de variables comunes.
		Scanner sc = new Scanner (System.in);
		String host = "localhost";
		int puerto = 6000, opcion = 99;
		boolean finalizado = false;
		String nombre;
		Jugador jug;
		
		System.out.println("Juego iniciado para el cliente...");
		
		// Declaración e inicialización de las variables de flujo de datos (son autocerrables).
		try (Socket cliente = new Socket(host, puerto);
				ObjectOutputStream flujoSalida = new ObjectOutputStream(cliente.getOutputStream());
				DataInputStream flujoEntrada = new DataInputStream(cliente.getInputStream());){
			
			// Comienza el programa cliente.
			System.out.print("Dime tu nombre: ");
			nombre = sc.nextLine();
			
			System.out.printf("Bienvenido %s! Comienza el juego. ", nombre);
			jug = new Jugador(nombre);
			
			//Se resetean los flujos de salida después de mandar para que no recoja nada del caché previo
			flujoSalida.writeObject(jug);
			flujoSalida.reset();
			
			System.out.println(flujoEntrada.readUTF());
			
			while(!finalizado) {
				do {
					System.out.printf("¿Cuál será tu próximo movimiento %s? Elige:"
							+ "\n\t 1. Atacar."
							+ "\n\t 2. Bloquear."
							+ "\n\t 3. Saltar."
							+ "\n\t 4. Agacharse."
							+ "\nRecuerda elegir uno de los números: ", nombre);
					try {
						opcion = Integer.parseInt(sc.nextLine());
					}catch(Exception e) {
						System.out.println(e.getMessage());
					}
					
					switch(opcion) {
					case 1:
						jug.setAccionActual(Accion.ATACAR);
						System.out.println(jug.getAccionActual());
						break;
					case 2:
						jug.setAccionActual(Accion.BLOQUEAR);
						break;
					case 3:
						jug.setAccionActual(Accion.SALTAR);
						break;
					case 4:
						jug.setAccionActual(Accion.AGACHARSE);
						break;
					default:
						System.out.println("¡Debes elegir un número entre 1 y 4!");
						break;
					}
				}while(opcion != 1 && opcion != 2 && opcion != 3 && opcion != 4);
				
				flujoSalida.writeObject(jug);
				flujoSalida.reset();
				
				// Se recibe el resultado de los movimientos.
				System.out.println(flujoEntrada.readUTF());
				
				// Se ajusta la vida del jugador.
				jug.setVida(flujoEntrada.readInt());
				
				// Se comprueba si la partida ha concluido.
				finalizado = flujoEntrada.readBoolean();
				
			}
			
			if(jug.getVida() > 0)
				System.out.println("Combate finalizado. Has ganado!");
			else
				System.out.println("Combate finalizado. Has perdido!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo en la entrada o la salida: " + e.getMessage());
		}
		
		
		
		sc.close();
		
	}

}
