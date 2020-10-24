/**
 * Contiene el dao.
 */
package dao;

import excepciones.ExcepcionPasswdIncorrecta;
import excepciones.ExcepcionUserNoExiste;
import excepciones.ExcepcionUserYaExiste;
import interfaz.Signable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import poolConexion.Pool;
import user.User;

/**
 * Realiza las consultas a la BBDD.
 * @version 1.0
 * @author Eneko, Endika, Markel
 */
public class DaoImplementation implements Signable {
    
    /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.servidor.daoImplementation");
    /**
     * 
     */
    private Pool pool = Pool.getPool();
    /**
     * 
     */
    private PreparedStatement stmt = null;
    /**
     * 
     */
    private Connection con = null;
    /**
     * 
     */
    private final String CONSULTAR_SI_LOGIN_ESTA = "Select * from usuario where login = ?";
    /**
     * 
     */
    private final String CONSULTAR_SI_ESTA_USUARIO = "Select * from usuario where login = ? and password = ?";
    /**
     * 
     */
    private final String INSERTAR_USUARIO = "Insert into usuario () values (?,?,?,?,?,?,?)";
    /**
     * 
     */
    private final String ACTUALIZAR_ULTIMA_ENTRADA_USUARIO = "Update usuario set lastAccess = ? where login = ? and password = ?";
    
    /**
     * Comprueba que el usuario está dado de alta. Está sincronizado para que no accedan varios hilos a la vez al método.
     * @param user Un usuario
     * @return Un usuario
     * @throws ExcepcionPasswdIncorrecta
     * @throws ExcepcionUserNoExiste
     * @throws SQLException 
     */
    @Override
    public synchronized User signIn(User user) throws ExcepcionPasswdIncorrecta, ExcepcionUserNoExiste,SQLException {
        //Mensaje logger entrada de método signIn.
        LOGGER.log(Level.INFO, "Método signin del dao");
        //Guardar en el atributo de tipo Connection unna conexión de la pila.
        con = pool.getConnection();
        //Guardar en el atributo de tipo Statement una conexión de busqueda de un usuario.
        stmt = con.prepareStatement(CONSULTAR_SI_ESTA_USUARIO);
        //Añadir las variable login del user al statement
        stmt.setString(1, user.getLogin());
        //Añadir las variable password del user al statement
        stmt.setString(2, user.getPassword());
        ResultSet rs = stmt.executeQuery();
        if(rs.next()==false)
            throw new ExcepcionUserNoExiste(); 
        //Liberar la conexión
        pool.freeConnection();
        return user;
    }

    /**
     * Registra un usuario en la base de datos. Está sincronizado para que no accedan varios hilos a la vez al método.
     * @param user Un usuario
     * @throws ExcepcionUserYaExiste 
     */
    @Override
    public synchronized void signUp(User user) throws ExcepcionUserYaExiste {
        //Mensaje logger entrada de método signUp.
        LOGGER.log(Level.INFO, "Método signup del dao");
        //Guardar en el atributo de tipo Connection unna conexión de la pila.
        con = pool.getConnection();
        
    }

    /**
     * Actualiza la hora de salida de un usuario de la aplicación. Está sincronizado para que no accedan varios hilos a la vez al método.
     * @param user Un usuario
     * @throws SQLException 
     */
    @Override
    public synchronized void logOut(User user) throws SQLException{
        //Mensaje logger entrada de método logout.
        LOGGER.log(Level.INFO, "Método logout");
        //Guardar en el atributo de tipo Connection unna conexión de la pila.
        con = pool.getConnection();
        //Guardar en el atributo de tipo Statement una conexión de actualizar fecha salida.
        stmt = con.prepareStatement(ACTUALIZAR_ULTIMA_ENTRADA_USUARIO);
        //Añadir los valores al statement
        stmt.setTimestamp(1,Timestamp.valueOf(LocalDateTime.now()));
        //Liberar la conexión
        pool.freeConnection();
    }
    
}
