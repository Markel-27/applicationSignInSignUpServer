/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import interfaz.Signable;

/**
 *
 * @author 2dam
 */
public class DaoFactory {
    
    public static Signable getSignable(){
        return new DaoImplementation();
    }
}
