/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.SQLException;
import poolConexion.Pool;

/**
 *
 * @author 2dam
 */
public class DaoImplementation {    
    
    //Accede a la conexion a la clase Pool 
    private Connection getConnection() throws SQLException {
        return Pool.getConexion();
    }
    
}
