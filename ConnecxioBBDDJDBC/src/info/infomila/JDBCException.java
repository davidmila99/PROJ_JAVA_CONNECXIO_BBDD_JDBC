/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.infomila;

/**
 *
 * @author David
 */
public class JDBCException extends RuntimeException{

    public JDBCException() {
    }

    public JDBCException(String arg0) {
        super(arg0);
    }

    public JDBCException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public JDBCException(Throwable arg0) {
        super(arg0);
    }

    public JDBCException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }


    
    
}
