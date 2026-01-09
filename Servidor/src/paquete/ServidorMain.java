package paquete;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ServidorMain {

	public static void main(String[] args) {
		//Declaración de variables.
		Scanner sc= new Scanner(System.in);
		int puerto = 6000;
		boolean finalizado = false;
		Jugador jug1, jug2;
		String resultado;
		
		System.out.println("Esperando jugadores....");
		
		// Declaración e inicialización de las variables de flujo de datos.
		try (ServerSocket server = new ServerSocket(puerto);
				Socket cliente1 = server.accept();
				Socket cliente2 = server.accept();
					ObjectInputStream flujoEntrada1 = new ObjectInputStream(cliente1.getInputStream());
					ObjectInputStream flujoEntrada2 = new ObjectInputStream(cliente2.getInputStream());
					DataOutputStream flujoSalida1 =  new DataOutputStream(cliente1.getOutputStream());
					DataOutputStream flujoSalida2 = new DataOutputStream(cliente2.getOutputStream());){
			
		
			jug1 = (Jugador) flujoEntrada1.readObject();
			jug2 = (Jugador) flujoEntrada2.readObject();
		
			
			System.out.println("Jugadores recibidos.");
			
			flujoSalida1.writeUTF("Jugarás contra " +  jug2.getNombre());
			flujoSalida2.writeUTF("Jugarás contra " +  jug1.getNombre());
			
			System.out.println("Adversarios enviados.");
			
			while(!finalizado) {
				jug1 = (Jugador) flujoEntrada1.readObject();
				jug2 = (Jugador) flujoEntrada2.readObject();
				
				
				System.out.println("Juagadas recibidas.");
				
				StringBuilder descripcionJugadas = new StringBuilder();
				comprobarJugadas(jug1, jug2, descripcionJugadas);
				comprobarJugadas(jug2, jug1, descripcionJugadas);
				
				System.out.println("Jugadas comprobadas");
				resultado = "-----RESULTADOS-----\n %s - Movimiento: %s - PH actuales: %d\n %s - Movimiento: %s - PH actuales: %d\n%s"
						.formatted(jug1.getNombre(), jug1.getAccionActual(), jug1.getVida(), jug2.getNombre(), jug2.getAccionActual(), jug2.getVida(), descripcionJugadas.toString());
				
				
				flujoSalida1.writeUTF(resultado);
				flujoSalida2.writeUTF(resultado);
				
				flujoSalida1.writeInt(jug1.getVida());
				flujoSalida2.writeInt(jug2.getVida());
				
				
				if(jug1.getVida() == 0 || jug2.getVida() == 0) {
					finalizado = true;
				}
				
				flujoSalida1.writeBoolean(finalizado);
				flujoSalida2.writeBoolean(finalizado);
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("No se ha encontrado ningún jugador compatible: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo en la entrada o salida de datos: " + e.getMessage());
		}
		
		sc.close();
	
	}

	private static void comprobarJugadas(Jugador atacante, Jugador defensor, StringBuilder desc) {
		// Probabilidad 50/50 de que alguna de las dos formas de esquiva falle.
		Random prob = new Random();
		if(atacante.getAccionActual() == Accion.ATACAR) {
			switch(defensor.getAccionActual()) {
			case BLOQUEAR:
				defensor.daño(5);
				desc.append("%s ataca pero %s bloquea (5 de daño)\n".formatted(atacante.getNombre(), defensor.getNombre()));
				break;
			case SALTAR:
				desc.append("%s ataca pero %s salta".formatted(atacante.getNombre(), defensor.getNombre()));
				if(prob.nextBoolean())
					desc.append(", por lo que no recibe daño\n");
				else {
					defensor.daño(15);
					desc.append(". ¡Oh! No ha podido saltar lo suficiente y se ha llevado un golpe en las partes bajas. ¡Eso tuvo que doler!  (15 de daño crítico)\n");
					
				}
				break;
			case AGACHARSE:
				desc.append("%s ataca pero %s se agacha".formatted(atacante.getNombre(), defensor.getNombre()));
				if(prob.nextBoolean())
					desc.append(", por lo que no recibe daño");
				else {
					defensor.daño(15);
					desc.append(". ¡Oh! No se agachó correctamente y se ha llevado un golpe en toda la jeta. ¡Le saltaron unos cuántos dientes! (15 de daño crítico)\n");
				}
				break;
			default:
				defensor.daño(10);
				desc.append("%s ataca pero %s también ¡Menudo intercambio de golpes! (10 de daño)\n".formatted(atacante.getNombre(), defensor.getNombre()));
				break;
			}
		}
		
	}

}
