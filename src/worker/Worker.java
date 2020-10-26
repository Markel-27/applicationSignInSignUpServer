/**
 * Contiene el Worker.
 */
package worker;

import dao.DaoFactory;
import excepciones.ExcepcionPasswdIncorrecta;
import excepciones.ExcepcionUserNoExiste;
import excepciones.ExcepcionUserYaExiste;
import interfaz.Signable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mensaje.Accion;
import mensaje.Mensaje;
import user.User;

/**
 * Clase que extiende de hilo, se comunica a traves del socket con la aplicación Cliente.
 * @version 1.0
 * @since 26/10/2020
 * @author Eneko, Endika, Markel
 */
public class Worker extends Thread implements Serializable{
    /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.servidor.worker.thread");
    //Atributo Socket.
    private Socket socketWorker;
    
    /**
     * Constructor de la clase. 
     * @param socket Un socket.
     */
    public Worker (Socket socket){
        //Guardar el parámetro en el atributo de la clase.
        socketWorker = socket;
        //Iniciar el Hilo. Llamada al método run del hilo.
        this.start();
    }
    /**
     * Método que ejecuta el hilo.
     */
    public void run(){
        //Mensaje Logger al acceder al método
        LOGGER.log(Level.INFO, "Método run del hilo de la aplicación");
        //Instanciar un objeto Signable.
        Signable dao = DaoFactory.getSignable();
        //Un usuario para guardar el usuario que leemos en el mensaje.
        User user = new User();
        //Clase que deserializa objetos que se han escrito con ObjectOutputStream enviado a traves de un socket.
        ObjectInputStream ois=null;
        //Clase para escribir objetos. Y enviarlos a traves del socket.
        ObjectOutputStream oos=null;
        //Leer un mensaje recibido. Castear el objeto a Mensaje. ClassNot FoundException da esto.
        Mensaje mensaje=new Mensaje(user,Accion.OK);
        //Guardar en el atributo user el usuario leido en el mensaje.
        try {
            //Clase que deserializa objetos que se han escrito con ObjectOutputStream enviado a traves de un socket.
            ois = new ObjectInputStream(socketWorker.getInputStream());
            //Clase para escribir objetos. Y enviarlos a traves del socket.
            oos = new ObjectOutputStream(socketWorker.getOutputStream());
            //Leer un mensaje recibido. Castear el objeto a Mensaje. ClassNot FoundException da esto.
            mensaje = (Mensaje) ois.readObject();
            //Guardar en el atributo user el usuario leido en el mensaje.
            user = mensaje.getUser();
            //Estudiar las distintas opciones del mensaje recibido
            switch(mensaje.getAccion()){
                //Caso de que la acción a realizar sea un SignIn
                case SIGNIN:
                    //Llamada al método signIn del Dao. Devuelve un user.
                    user = dao.signIn(mensaje.getUser());
                    //Indicar en el mensaje que todo ha salido bien
                    mensaje.setAccion(Accion.OK);
                    //Escribir en el socket el mensaje.
                    oos.writeObject(mensaje);
                    break;
                //Caso de que la acción a realizar sea un SignUp
                case SIGNUP:
                    //Llamada al método signIn del Dao.
                    dao.signUp(mensaje.getUser());
                    //Escribir en el socket el mensaje.
                    oos.writeObject(mensaje);
                    break;
                default:
                    //Llamada al método logOut del Dao.
                    dao.logOut(mensaje.getUser()); 
                    //Escribir en el socket el mensaje.
                    oos.writeObject(mensaje);
                    break;
            }
        }catch(ExcepcionPasswdIncorrecta e){
            //Se ha producido un error indicar en el mensaje
            mensaje.setAccion(Accion.PASSWORD_INCORRECTA);
        //Entra al catch de la excepción lanzada en el método signIn del dao
        }catch(ExcepcionUserNoExiste e){
            //Se ha producido un error indicar en el mensaje
            mensaje.setAccion(Accion.USUARIO_NO_EXISTE);
        //Entra al catch de la excepción lanzada en el método signIn del dao. Error global
        }catch(ExcepcionUserYaExiste e){
            //Se ha producido un error indicar en el mensaje
            mensaje.setAccion(Accion.USUARIO_YA_EXISTE);
        }catch(Exception e){
            //Se ha producido un error indicar en el mensaje
            mensaje.setAccion(Accion.TIEMPO_EXPIRADO);
        }
        //FAlta mandar los catch no sé como hacer ioException
    }
}