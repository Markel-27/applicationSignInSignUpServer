/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poolConexion;

import java.sql.Connection;
import java.sql.SQLException;
import javax.activation.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author markel
 */

public class Pool {
    private static BasicDataSource ds = null;

    public static BasicDataSource getDataSource(){
        if (ds == null){
            ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUsername("root");
            ds.setPassword("abcd*1234");
            ds.setUrl("jdbc:mysql://localhost:3306/SignInSignUp");
            ds.setMaxActive(10); //NUMERO MAXIMO DE CONEXIONES?????
            ds.setMinIdle(1);
            ds.setMaxIdle(2);
            ds.setMaxWait(180000);
        }
        return ds;
    }
    
    public static Connection getConexion() throws SQLException {
        return getDataSource().getConnection();
    }
    
}

    
