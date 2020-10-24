/**
 * Contiene el pool de conexiones.
 */
package poolConexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Pool de conexión con la base de datos.
 * @version 1.0
 * @since 30/10/2020
 * @author Eneko, Endika, Markel
 */
public class Pool {
    /**
     * Un objeto pool satatico de la clase, solo existirá un pool.
     */
    private static  Pool unPool = null;
    /**
     * Una pila que contendrá las conexiones al pool.
     */
    private Stack pilaContenedoraConexiones;
    /**
     * Leer los datos del fichero properties con la información de la base de datos.
     */
    private ResourceBundle fichero;
    /**
     * Una conexión a la BBDD.
     */
    private Connection con = null;
    
    private String url;
    private String user;
    private String passwd;
    private String driver;
    
    /**
     * Constructor privado, solo accesible dentro de la clase. Inicializa la pila.
     */
    private Pool(){
        pilaContenedoraConexiones = new Stack();
    }
    /**
     * Accede una vez solo al constructor.
     * @return Una instacia del pool.
     */
    public static Pool getPool(){
        //Si el pool está sin inicializar entra
        if(unPool == null)
            //Crea una instancia de la clase Pool. 
            unPool = new Pool();
        return unPool;
    }
    
    /**
     * Abre una conexión con la base de datos.
     * @return Una conexion.
     */
    public Connection getConnection (){
       return (Connection) this.pilaContenedoraConexiones.pop();
    }
    
    /**
     * Cierra una conexión con la base de datos.
     */   
    public void freeConnection (){
       this.pilaContenedoraConexiones.push(con);
    }
    //Meter las conexiones en el constructor en la pila?????
    private void openConnection() {
        //Separar la insercion de datos del try catch lo primero una vez lo otro necesito 25 conexiones o las que me diga los hilos
        fichero = ResourceBundle.getBundle("datosconexionbasededatos");
        url = fichero.getString("Conn");
        user = fichero.getString("DBUser");
        passwd = fichero.getString("DBPass");
        driver = fichero.getString("Driver");
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,passwd);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("No se conecta");
        }
    }
}
