/**
 * Contiene la aplicación del servidor de Base de datos usuarios
 */
package grupog5.pkg2020.application.signin.signup.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import worker.Worker;

/**
 * @version 1.0
 * @since 26/10/2020
 * @author Eneko, Endika, Markel
 */
public class GrupoG52020ApplicationSignInSignUpServer {

    /**
     * Indica el número máximo de conexiones posibles al servidor.
     */
    private final static Integer NUMERO_CONEXIONES_MAXIMAS = 25;
    /**
     * Número de conexiones activas, controlar que no superen las máximas preestablecidas.
     */
    private static Integer conexionesActuales = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //Declaración de un Server socket para atender las peticiones del cliente
        ServerSocket socketServidor;
        //Inicialización del serverSocket indicando puerto por el que escucha
        socketServidor = new ServerSocket(6500);//Numero de puerto guardado en config properties??
        //Bucle infinito
        while (true){
            //Si hay conexiones disponibles.
            if(conexionesActuales < NUMERO_CONEXIONES_MAXIMAS){
                //Acepta el server socket una conexión y traslada la atención de esa conexión a un socket.
                Socket unSocket = socketServidor.accept();
                //Sumar la conexión actual al atributo que controla las conexiones activas.
                conexionesActuales++;
                //Crear el hilo.
                Worker worker = new Worker(unSocket);
            } 
            /*
            if(!worker.isAlive())
               conexionesActuales--; 
            */          
        }
    }
    
}
