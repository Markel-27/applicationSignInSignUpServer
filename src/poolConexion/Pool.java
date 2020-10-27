/**
 * Contiene el pool de conexiones.
 */
package poolConexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pool de conexión con la base de datos.
 * @version 1.0
 * @since 30/10/2020
 * @author Eneko, Endika, Markel
 */
public class Pool {
    /**
     * Atributo Logger para rastrear los pasos de ejecución del programa.
     */
    private static final Logger LOGGER = 
            Logger.getLogger("grupog5.signinsignupapplication.servidor.pooldeconexiones");
    /**
     * Un objeto pool statico de la clase, solo existirá un pool. Se inicializa. Solo accesible dentro de la clase.
     */
    private static  Pool unPool = new Pool();
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
    /**
     * Atributo que almacena la url de la base de datos.
     */
    private String url;
    /**
     * Atributo que almacena el usuario para acceder a la base de datos.
     */
    private String user;
    /**
     * Atributo que almacena la contraseña de acceso a la base de datos.
     */
    private String passwd;
    /**
     * Atributo que almacena el driver de la base de datos.
     */
    private String driver;
    
    /**
     * Constructor privado, solo accesible dentro de la clase. Inicializa la pila.
     */
    private Pool(){
        //Se supone que solo entra una vez aqui. Veremos.
        LOGGER.log(Level.INFO, "Método constructor del pool");
        //Inicializar la pila
        pilaContenedoraConexiones = new Stack();
        //La longitud de la pila son el número de conexiones preestablecida en la clase principal del servidor
    }
    /**
     * Accede una vez solo al constructor. Es estático para que no se acceda con objetos se accede a traves de la clase
     * @return Una instacia del pool.
     */
    public static Pool getPool(){
        return unPool;
    }
    
    /**
     * Recoge una conexión de la pila. Utilizando el método pop de la clase Stack (pila)
     * @return Una conexion.
     */
    public Connection getConnection (){
        //Método del pool que el dao le pide una conexión
        LOGGER.log(Level.INFO, "Método getConnection del pool");
        //Pedir una conexión a la pila si está vacá añadir una
        if(pilaContenedoraConexiones.isEmpty()){
            //Abrir una conexión.
            this.openConnection();
            //Introducir una conexión en la pila.
            pilaContenedoraConexiones.push(con);
        }
       //Retorna una conexión de la pila. La sacade la pila con pop.
       return (Connection) this.pilaContenedoraConexiones.pop();
    }
    
    /**
     * Devuelve una conexión a la pila con el método push de la clase Stack (Pila).
     */   
    public void freeConnection (){
        LOGGER.log(Level.INFO, "Método freeConnection del pool");
       //Introduce una conexión en la pila.
       this.pilaContenedoraConexiones.push(con);
    }
    //Meter las conexiones en el constructor en la pila?????
    private void openConnection() {
        LOGGER.log(Level.INFO, "Método openConnection del pool");
        //Asocia el fichero de propiedades con el objeto de la clase Resource Bundle, clase que lee String del gichero de propiedades.
        fichero = ResourceBundle.getBundle("datosconexionbasededatos");
        //Guardar la información del fichero de propiedades en os atributos de la clase.
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
