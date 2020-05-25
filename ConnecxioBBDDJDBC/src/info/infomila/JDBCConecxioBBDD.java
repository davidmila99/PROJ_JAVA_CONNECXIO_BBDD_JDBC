/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.infomila;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class JDBCConecxioBBDD implements Interficie{
    
    private Connection con = null;
    private String nomFitxerPropietats;

    public JDBCConecxioBBDD(String nomFitxerPropietats) {
        if(nomFitxerPropietats == null){
            throw new JDBCException("El fitxer de propietats es obligatori");
        }
        this.nomFitxerPropietats = nomFitxerPropietats;
        
        Properties p = new Properties();
        try {
            p.load(new FileReader(nomFitxerPropietats));
        } catch (IOException ex) {
            throw new JDBCException("Problemes en carrregar la configuracio");
        }
        
        
        String url = p.getProperty("url");
        String usu = p.getProperty("usuari");
        String pwd = p.getProperty("contrasenya");
        if (url==null || usu==null || pwd==null) {
            throw new JDBCException("Falten alguna de les tres propietats");
        }
        
        Connection con = null;
        try {
            con = DriverManager.getConnection(url,usu,pwd);
            con.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new JDBCException("Problemes en establir la connecxio");
        }
    }

    @Override
    public void close() {
        if(con != null){
            try {
                con.close();
                con = null;
            } catch (SQLException ex) {
                throw new JDBCException("Problemes en tancar la connecxio");
            }
        }else{
            throw new JDBCException("No es pot tancar una connecxio nula");
        }
    }

    @Override
    public List<Categoria> getListCategories() {
        return null;
    }

    @Override
    public void canviarNomCategoria(Categoria canviar, String nouNom) throws InterficieException {
    }

    @Override
    public void afegirCategoriaFilla(Categoria c) {
    }

    @Override
    public List<Ruta> getListRutesXCategoria(Categoria c) {
        return null;
    }

    @Override
    public void commit() {
        if(con != null){
            try {
                con.commit();
            } catch (SQLException ex) {
                throw new JDBCException("No hem pogut fer commit");
            }
        }else{
            throw new JDBCException("No es pot fer commit de una connecxio nula");
        }
    }

    @Override
    public void borrarCategoria(Categoria c) throws InterficieException {
    }
    
    
    
    
}
