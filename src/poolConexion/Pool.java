/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poolConexion;

/**
 *
 * @author markel
 */
public class Pool {
    
    public static BasicDataSource ds = null;

    private void DataSource(){
        if (ds == null){
            ds = new BasicDataSource() {};
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUsername("root");
            ds.setPassword("abcd*1234");
            ds.setUrl("jdbc:mysql://localhost:3306/SignInSignUp");
            ds.setMaxActive(10); //NUMERO MAXIMO DE CLIENTES/CONEXIONES?????
            ds.setMaxIdle(2);
            ds.setMaxWait(180000);
        }
    }
    
}

    
