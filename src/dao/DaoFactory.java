
package dao;

import interfaz.Signable;

/**
 * Factoria de la Interface Signable.
 * @version 1.0
 * @since 26/10/2020
 * @author Eneko, Endika, Markel
 */
public class DaoFactory {
    /**
     * Recoge una instancia de la clase que implementa la interface.
     * @return Un Signable. 
     */
    public  static Signable getSignable(){
        //Retorna la interfaz pero new de la clase que implemeta la interfaz.
        return new DaoImplementation();
    }
}
