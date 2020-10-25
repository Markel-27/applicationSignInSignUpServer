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
    
    private String url;
    private String user;
    private String passwd;
    private String driver;
    
    /**
     * Constructor privado, solo accesible dentro de la clase. Inicializa la pila.
     */
    private Pool(){
        //Inicializar la pila
        pilaContenedoraConexiones = new Stack();
        //La longitud de la pila son el número de conexiones preestablecida en la clase principal del servidor
        for(int i=0;i<=Integer.parseInt(fichero.getString("NumeroConexionesMaximas"));i++){
            //Abrir una conexión.
            this.openConnection();
            //Introducir una conexión en la pila.
            pilaContenedoraConexiones.push(con);
        }
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
       //Retorna una conexión de la pila. La sacade la pila con pop.
       return (Connection) this.pilaContenedoraConexiones.pop();
    }
    
    /**
     * Devuelve una conexión a la pila con el método push de la clase Stack (Pila).
     */   
    public void freeConnection (){
       //Introduce una conexión en la pila.
       this.pilaContenedoraConexiones.push(con);
    }
    //Meter las conexiones en el constructor en la pila?????
    private void openConnection() {
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
