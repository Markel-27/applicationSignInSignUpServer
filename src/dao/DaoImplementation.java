
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
 * @since 23/10/2020
 * @author Eneko, Endika, Markel
 */
public class DaoImplementation implements Signable {   
   /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.servidor.daoImplementation");
    /**
     * Atributo que es un pool. Un poo que va a ser una pila con conexiones a la Base de datos.
     */
    private Pool pool = Pool.getPool();
    /**
     * Un PreparedStatement. Para realizar las querys a la base de datos.
     */
    private PreparedStatement stmt = null;
    /**
     * Una conexión.
     */
    private Connection con = null;
    /**
     * Consulta si en la base de datos usuario existe el login 
     */
    private final String CONSULTAR_SI_LOGIN_ESTA = "Select * from usuario where BINARY login = ?";
    /**
     * Consulta si hay un usuario con el login y la contraseña indicados.
     */
    private final String CONSULTAR_SI_ESTA_USUARIO = "Select * from usuario where BINARY login = ? and BINARY password = ?";
    /**
     * Insertar un nuevo usuario.
     */
    private final String INSERTAR_USUARIO = "Insert into usuario (login,email,fullName,status,privilege,password,lastAccess,lastPasswordChange)"
            + " values (?,?,?,?,?,?,?,?)";
    /**
     * Actualizar la fecha de´la última entrada de un usuario.
     */
    private final String ACTUALIZAR_ULTIMA_ENTRADA_USUARIO = "Update usuario set lastAccess = ? where login = ? and password = ?";
    
    /**
     * Comprueba que el usuario está dado de alta. Está sincronizado para que no accedan varios hilos a la vez al método.
     * @param user Un usuario
     * @return Un usuario
     * @throws ExcepcionPasswdIncorrecta
     * @throws ExcepcionUserNoExiste
     * @throws Exception
     */
    @Override
    public synchronized User signIn(User user) throws ExcepcionPasswdIncorrecta,ExcepcionUserNoExiste,Exception {
        //Mensaje logger entrada de método signIn.
        LOGGER.log(Level.INFO, "Método signin del dao");
        //Inicializar fuera del try
        ResultSet rs = null;
        //Meter en un try por si da error de sqlException Que no se pueda conectar a la base de datos
        try{
            //Guardar en el atributo de tipo Connection unna conexión de la pila.
            con = pool.getConnection();
            //Guardar en el atributo de tipo Statement una conexión de busqueda de si el nombre de usuario está.
            stmt = con.prepareStatement(CONSULTAR_SI_LOGIN_ESTA);
            //Añadir las variable login del user al statement
            stmt.setString(1, user.getLogin());
            //Guardar en el atributo de tipo Statement una conexión de busqueda de un usuario.
            rs = stmt.executeQuery();
            //Si no devuelve nada el login no existe en la base de datos
            if(rs.next()==false){
                //Lanzar excepción user no existe
                throw new ExcepcionUserNoExiste(); 
            }              
            //Si no, el login existe mirar si coincide el login y la contraseña
             else{
                stmt = con.prepareStatement(CONSULTAR_SI_ESTA_USUARIO);
                //Añadir las variable login del user al statement
                stmt.setString(1, user.getLogin());
                //Añadir las variable password del user al statement
                stmt.setString(2, user.getPassword());
                rs = stmt.executeQuery(); 
                //Si no hay resultados
                if(rs.next()==false){
                    //Lanzar excepción contraseña incorrecta
                    throw new ExcepcionPasswdIncorrecta(); 
                }             
            }
        }catch(SQLException e){
            LOGGER.log(Level.INFO,e.getMessage());
            throw new Exception();
        }finally{
            try{
               //Liberar la conexión
            pool.freeConnection();
            //Si los objetos de las clases Resultset y PreparedStament no son nulos cerrar.
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();     
            }catch(SQLException e){
                LOGGER.log(Level.INFO,e.getMessage());
               throw new Exception();
            }
        }
        return user;
    }

    /**
     * Registra un usuario en la base de datos. Está sincronizado para que no accedan varios hilos a la vez al método.
     * @param user Un usuario
     * @throws ExcepcionUserYaExiste 
     * @throws excepciones 
     */
    @Override
    public synchronized void signUp(User user) throws ExcepcionUserYaExiste,Exception{
        //Mensaje logger entrada de método signUp.
        LOGGER.log(Level.INFO, "Método signup del DaoImplementation");
        ResultSet rs = null;
        try{
           //Guardar en el atributo de tipo Connection unna conexión de la pila.
            con = pool.getConnection();
            //Guardar en el atributo de tipo Statement una conexión de busqueda de si el nombre de usuario está.
            stmt = con.prepareStatement(CONSULTAR_SI_LOGIN_ESTA);
            //Añadir las variable login del user al statement
            stmt.setString(1, user.getLogin());
            //Guardar en el atributo de tipo Statement una conexión de busqueda de un usuario.
            rs = stmt.executeQuery();
            //Si rs ( resultado de la query) no es vacío, hay en la Base de datos un usuario con el login.
            if(rs.next()){
                //Lanzar excepción user no existe
                throw new ExcepcionUserYaExiste();               
            }
            else{
                stmt = con.prepareStatement(INSERTAR_USUARIO);
                //Añadir los valores al statement
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getFullName());
                stmt.setString(4, user.getStatus().name());
                stmt.setString(5, user.getPrivilege().name());
                stmt.setString(6, user.getPassword());
                stmt.setTimestamp(7,Timestamp.valueOf(LocalDateTime.now()));
                stmt.setTimestamp(8,Timestamp.valueOf(LocalDateTime.now()));
                //El prepared stamtement ejecuta la instrucción un insert en este caso.
                stmt.executeUpdate();
                 } 
        }catch(SQLException e){
            LOGGER.log(Level.INFO,e.getMessage());
            throw new Exception();
        }finally{
            //Liberar la conexión, devuelve la coneción a la pila del pool.
            pool.freeConnection();
            try{
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();     
            }catch(SQLException e){
               LOGGER.log(Level.INFO,e.getMessage());
               throw new Exception();
            }
        }
    }

    /**
     * Actualiza la hora de salida de un usuario de la aplicación. Está sincronizado para que no accedan varios hilos a la vez al método.
     * @param user Un usuario
     * @throws SQLException 
     * @throws excepciones
     */
    @Override
    public synchronized void logOut(User user) throws Exception{
         //Mensaje logger entrada de método logout.
        LOGGER.log(Level.INFO, "Método logout del Daoimplementation");
        try{
            //Guardar en el atributo de tipo Connection unna conexión de la pila.
            con = pool.getConnection();
            //Guardar en el atributo de tipo Statement una conexión de actualizar fecha salida.
            stmt = con.prepareStatement(ACTUALIZAR_ULTIMA_ENTRADA_USUARIO);
            //Añadir los valores al statement
            stmt.setTimestamp(1,Timestamp.valueOf(LocalDateTime.now()));
            //Añadir los valores al statement
            stmt.setString(2, user.getLogin());
            //Añadir los valores al statement
            stmt.setString(3, user.getPassword());
            //El prepared stamtement ejecuta la instrucción un insert en este caso.
            stmt.executeUpdate();
            //Liberar la conexión
            pool.freeConnection();
        }catch(Exception e){
            LOGGER.log(Level.INFO,e.getMessage());
            throw new Exception();
        }finally{
            try{
                if (stmt != null)
                    stmt.close();     
            }catch(SQLException e){
               LOGGER.log(Level.INFO,e.getMessage());
               throw new Exception();
            }
        }
    }
    
    
}
