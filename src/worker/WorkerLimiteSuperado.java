/**
 *  Contiene el Worker.
 */
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
     * Un atributo Socket.
     */
    private Socket socket;
    /**
     * Un mensaje.
     */
    private Mensaje mensaje;
    
    /**
     * Constructor de la clase
     * @param socket Extremo del socket por el que comunicarse con el cliente.
     */
    public WorkerLimiteSuperado(Socket socket){
        setSocket(socket);
    }

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
     * Método de ejecución del Hilo
     */
    public void run(){
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
