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
        LOGGER.log(Level.INFO, "Método run del hilo de la clase Worker");
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
        //Entrada salida de datos.
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
                    try{
                        //Llamada al método signIn del Dao. Devuelve un user.
                        user = dao.signIn(mensaje.getUser());
                        //Indicar en el mensaje que todo ha salido bien
                        mensaje.setAccion(Accion.OK);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                        break;
                    }catch(ExcepcionPasswdIncorrecta e){
                        //Se ha producido un error indicar en el mensaje
                        mensaje.setAccion(Accion.PASSWORD_INCORRECTA);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                    }catch(ExcepcionUserNoExiste e){
                        //Se ha producido un error indicar en el mensaje
                        mensaje.setAccion(Accion.USUARIO_NO_EXISTE);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                    }catch(Exception e){
                        //Se ha producido un error indicar en el mensaje
                        mensaje.setAccion(Accion.TIEMPO_EXPIRADO);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                    }
                //Caso de que la acción a realizar sea un SignUp
                case SIGNUP:
                    try{
                        //Llamada al método signIn del Dao.
                        dao.signUp(mensaje.getUser());
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                        break;
                    }catch(ExcepcionUserYaExiste e){
                        //Se ha producido un error indicar en el mensaje
                        mensaje.setAccion(Accion.USUARIO_YA_EXISTE);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                    }catch(Exception e){
                        //Se ha producido un error indicar en el mensaje
                        mensaje.setAccion(Accion.TIEMPO_EXPIRADO);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                    }
                default:
                    try{
                        //Llamada al método logOut del Dao.
                        dao.logOut(mensaje.getUser()); 
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                        break;
                    }catch(Exception e){
                        //Se ha producido un error indicar en el mensaje
                        mensaje.setAccion(Accion.TIEMPO_EXPIRADO);
                        //Escribir en el socket el mensaje.
                        oos.writeObject(mensaje);
                    }
            }
        //Catch Errores entrada salida de datos
        }catch(IOException e){
             LOGGER.log(Level.INFO, "Catch de esntada salida reenvio de mensaje de servidor a cliente.");
        }catch(Exception e){
             LOGGER.log(Level.INFO, "Catch de reenvio de mensaje de servidor a cliente.");
        }//Cerrar los Streams
        finally{
            //Dentro de try catch dan error de IOException
            try{
            oos.close();
            ois.close();
        }catch(IOException e){
            LOGGER.log(Level.INFO, "Catch cerrando los Output/Input stream.");
        }
        }       
    }
}