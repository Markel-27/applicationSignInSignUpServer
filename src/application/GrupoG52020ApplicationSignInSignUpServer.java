/**
 * Contiene la aplicación del servidor de Base de datos usuarios
 */
package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import worker.Worker;

/**
 * Aplicación del servidor SignInSignUp. 
 * @version 1.0
 * @since 26/10/2020
 * @author Eneko, Endika, Markel
 */
public class GrupoG52020ApplicationSignInSignUpServer {
    /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.servidor.application");
    /**
     * Leer los datos del fichero properties con la información de la base de datos.
     */
    private static final ResourceBundle FICHERO = ResourceBundle.getBundle("poolConexion.datosconexionbasededatos");
    /**
     * Indica el número máximo de conexiones posibles al servidor. El dato está guardado en un fichero de propiedades en el paquete poolConexion.
     */
    private static final Integer NUMERO_CONEXIONES_MAXIMAS = Integer.parseInt(FICHERO.getString("NumeroConexionesMaximas"));
    /**
     * Número de conexiones activas, controlar que no superen las máximas preestablecidas.
     */
    private static Integer conexionesActuales = 0;

    /**
     * Numero de puerto libre para la comunicación con el servidor por medio de un socket.
     */
    private static final Integer NUMERO_PUERTO = 
            Integer.parseInt(ResourceBundle.getBundle("socket.infoSocket").getString("portNumber"));
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //Mensaje Logger al acceder al método
        LOGGER.log(Level.INFO, "Método main de la aplicación sevidor.");
        // TODO code application logic here
        //Declaración de un Server socket para atender las peticiones del cliente
        ServerSocket socketServidor;
        //Inicialización del serverSocket indicando puerto por el que escucha
        socketServidor = new ServerSocket(NUMERO_PUERTO);
        //Bucle infinito, el servidor atiende todas las conexiones del lado cliente
        while (true){
            LOGGER.log(Level.INFO, "Bucle infinito del servidor atendiendo consultas de clientes.");
            //Si hay conexiones disponibles.
            if(NUMERO_CONEXIONES_MAXIMAS > conexionesActuales ){
                //Acepta el server socket una conexión y traslada la atención de esa conexión a un socket.
                Socket unSocket = socketServidor.accept();
                //Sumar la conexión actual al atributo que controla las conexiones activas.
                conexionesActuales++;
                //Crear el hilo.
                Worker worker = new Worker(unSocket);
                //Hacer un join al thread para que este programa espere a que el thread acabe para seguir.
                try{
                    worker.join();
                }catch(InterruptedException e){
                    LOGGER.log(Level.SEVERE, "Error de interrupción del hilo.");
                }               
                //El thread acaba, actualizar variable conexionesActuales.
                conexionesActuales--;
            } 
                 
        }
    }
    
}
