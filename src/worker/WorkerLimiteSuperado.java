
package worker;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mensaje.Accion;
import mensaje.Mensaje;

/**
 * Esta clase extiende de Thread y gestiona la comunicación con el cliente cuando el número de conexiones se han superado.
 * @version 1.0
 * @since 30/10/2020
 * @author Eneko, Endika, Markel
 */
public class WorkerLimiteSuperado extends Thread{
    /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.servidor.workerLimiteSuperado.thread");
    /**
     * Un atributo Socket.
     */
    private Socket socket;
    /**
     * Un mensaje.
     */
    private Mensaje mensaje;
    
    /**
     * Recoge un socket.
     * @return Un socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Asigna el socket al parámetro socket.
     * @param socket Extremo del socket en el lado servidor.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    /**
     * Método que ejecuta el hilo. Arranca cuando un objeto de la clase ejecuta el método start(). 
     */
    public void run(){
        //Mensaje Logger al acceder al método
        LOGGER.log(Level.INFO, "Método run del hilo de la clase WorkerLimiteSuperado");
        //Añadimos la acción de error al mensaje
        mensaje.setAccion(Accion.TIEMPO_EXPIRADO);
        //Clase para escribir objetos. Y enviarlos a traves del socket.
        ObjectOutputStream oos=null;
        try {
            //Inicializar el objeto ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            oos.writeObject(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(WorkerLimiteSuperado.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
}
