/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import mensaje.Mensaje;
import user.User;

/**
 *
 * @author 2dam
 */
public class Worker extends Thread implements Serializable{
    /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.cliente.application");
    private Socket socketWorker;
    
    public Worker (Socket socket){
        socketWorker = socket;
        this.start();
    }
    
    public void run(){
        Signable dao = DaoFactory.getSignable();
        User user = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(socketWorker.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socketWorker.getOutputStream());
            Mensaje mensaje = (Mensaje) ois.readObject();
            if(mensaje.getAccion().equals("SIGNIN")){
                user = dao.signIn(mensaje.getUser());
                oos.writeObject(user);
            }
            else if(mensaje.getAccion().equals("SIGNUP"))
                dao.signUp(mensaje.getUser());
            else if(mensaje.getAccion().equals("LOGOUT"))
                dao.logOut(mensaje.getUser());         
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExcepcionPasswdIncorrecta ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExcepcionUserNoExiste ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExcepcionUserYaExiste ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
