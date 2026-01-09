package paquete;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
				
				// Va recogiendo cadenas que va añadiendo a su buffer. Posteriormente se añadirá la descripción al resultado.
				StringBuilder descripcionJugadas = new StringBuilder();
				comprobarJugadas(jug1, jug2, descripcionJugadas);
				comprobarJugadas(jug2, jug1, descripcionJugadas);
				
				System.out.println("Jugadas comprobadas");
				resultado = "--------------------RESULTADOS--------------------\n %s - Movimiento: %s - PH actuales: %d\n %s - Movimiento: %s - PH actuales: %d\n%s--------------------------------------------------"
						.formatted(jug1.getNombre(), jug1.getAccionActual(), jug1.getVida(), jug2.getNombre(), jug2.getAccionActual(), jug2.getVida(), descripcionJugadas.toString());
				
				// Se envía el resultado a cada cliente.
				flujoSalida1.writeUTF(resultado);
				flujoSalida2.writeUTF(resultado);
				
				// Se mandan las vidas de cada jugador para que se actualicen en cliente.
				flujoSalida1.writeInt(jug1.getVida());
				flujoSalida2.writeInt(jug2.getVida());
				
				
				if(!jug1.estaVivo() || !jug2.estaVivo()) {
					finalizado = true;
				}
				
				//Se envían los resultados finales del combate para ver si deben seguir luchando.
				flujoSalida1.writeBoolean(finalizado);
				flujoSalida2.writeBoolean(finalizado);
			}
			
			//Se mandan los resultados del combate para ver si los jugadores ganaron o perdieron.
			flujoSalida1.writeBoolean(jug1.estaVivo());
			flujoSalida2.writeBoolean(jug2.estaVivo());
			
			
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
		Accion ataque = atacante.getAccionActual();
		
		//En función del ataque, las opciones de esquiva pueden cambiar: 
		if(ataque == Accion.PUNIETAZO || ataque == Accion.PATADA) {
			switch(defensor.getAccionActual()) {
			case BLOQUEAR:
				defensor.daño(5);
				desc.append("%s ataca pero %s bloquea como puede (5 de daño)\n".formatted(atacante.getNombre(), defensor.getNombre()));
				break;
			case SALTAR:
				if(ataque == Accion.PUNIETAZO) {
					defensor.daño(20);
					desc.append("%s salta pero %s ataca con un terrible puñetazo que atina en sus partes bajas ¡Eso tuvo que doler! (20 de daño crítico)\n".formatted(defensor.getNombre(), atacante.getNombre()));
				}else
					desc.append("%s salta por lo que la patada de %s falla estrepitosamente...\n".formatted(defensor.getNombre(), atacante.getNombre()));
				
				break;
			case AGACHARSE:
				if(ataque == Accion.PATADA) {
					defensor.daño(20);
					desc.append("%s se agacha pero %s ataca con una terrible patada que atina en toda su jeta ¡Han saltado varios dientes! (20 de daño crítico)\n".formatted(defensor.getNombre(), atacante.getNombre()));
				}else
					desc.append("%s se agacha por lo que el puñetazo de %s falla estrepitosamente...\n".formatted(defensor.getNombre(), atacante.getNombre()));
				break;
			default:
				defensor.daño(10);
				desc.append("%s ataca pero %s también ¡Menudo intercambio de golpes! (10 de daño)\n".formatted(atacante.getNombre(), defensor.getNombre()));
				break;
			}
		}
		
		
		
	}
	

}
