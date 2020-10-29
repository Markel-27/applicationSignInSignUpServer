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
 * Clase que extiende de hilo, se comunica a traves del socket con la aplicación Cliente y le da respuesta a las peticiones del 
 * cliente usando el DaoImplentation para realizar consultas en la base de datos.
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
        Mensaje mensajeRecibido; //=new Mensaje(user,Accion.OK)
        //Vamos a usar una instancia de mensaje diferente para el reenvio.
        Mensaje mensajeAEnviar = new Mensaje();
        //Try catch capturar los posibles errores cuando hay una consulta en la base de datos.
        try {
            //Clase que deserializa objetos que se han escrito con ObjectOutputStream enviado a traves de un socket.
            ois = new ObjectInputStream(socketWorker.getInputStream());
            
            //Leer un mensaje recibido. Castear el objeto a Mensaje. ClassNot FoundException da esto.
            mensajeRecibido = (Mensaje) ois.readObject();
            //Guardar en el atributo user el usuario leido en el mensaje.
            user = mensajeRecibido.getUser();
            //Estudiar las distintas opciones del mensaje recibido, hay 3 opciones Signin signup logout
            switch(mensajeRecibido.getAccion()){
                //Caso de que la acción a realizar sea un SignIn
                case SIGNIN:
                    LOGGER.log(Level.INFO, "Recibida petición SignIn");
                    //Llamada al método signIn del Dao. Devuelve un user.
                    user = dao.signIn(user);
                    //Indicar en el mensaje que todo ha salido bien
                    mensajeAEnviar.setAccion(Accion.OK);
                    mensajeAEnviar.setUser(user);
                    break;   
                //Caso de que la acción a realizar sea un SignUp
                case SIGNUP:
                    LOGGER.log(Level.INFO, "Recibida petición SignUp");
                    //Llamada al método signIn del Dao.
                    dao.signUp(user);
                    //Indicar en el mensaje que todo ha salido bien
                    mensajeAEnviar.setAccion(Accion.OK);
                    mensajeAEnviar.setUser(user);
                    break;
                default:
                    LOGGER.log(Level.INFO, "Recibida petición LogOut");
                    //Llamada al método logOut del Dao.
                    dao.logOut(user);
                    //Indicar en el mensaje que todo ha salido bien
                    mensajeAEnviar.setAccion(Accion.OK);                  
                    break;
            }
        //Tratar las excepciones la base de datos puede lanzar las tres que hemos creado mas las existentes las englobamos con exception que es la padre.
        }catch(ExcepcionPasswdIncorrecta e){
            LOGGER.log(Level.INFO, "Entra al catch de ExceptionPasswordIncorrecta en el worker.");
            //Se ha producido un error indicar en el mensaje
            mensajeAEnviar.setAccion(Accion.PASSWORD_INCORRECTA);
        }catch(ExcepcionUserYaExiste e){
            LOGGER.log(Level.INFO, "Entra al catch de ExceptionUserYaExiste en el worker.");
            //Se ha producido un error indicar en el mensaje
            mensajeAEnviar.setAccion(Accion.USUARIO_YA_EXISTE);
        }catch(ExcepcionUserNoExiste e){
            LOGGER.log(Level.INFO, "Entra al catch de ExceptionUserNoExiste en el worker.");
            //Se ha producido un error indicar en el mensaje
            mensajeAEnviar.setAccion(Accion.USUARIO_NO_EXISTE);
        }catch(Exception e){
            LOGGER.log(Level.INFO, "Entra al catch de Exception en el worker.");
            //Se ha producido un error indicar en el mensaje
            mensajeAEnviar.setAccion(Accion.TIEMPO_EXPIRADO);
        }
        //Ahora enviar el mensaje a la aplicación cliente.
        try{
            //Clase para escribir objetos. Y enviarlos a traves del socket.
            oos = new ObjectOutputStream(socketWorker.getOutputStream());
           //Escribir en el socket el mensaje que va a ir al socket del cliente.
            oos.writeObject(mensajeAEnviar);
        }catch(IOException e){
             LOGGER.log(Level.INFO, "Catch de esntada salida reenvio de mensaje de servidor a cliente.");
        }
        //Cerrar los Streams
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