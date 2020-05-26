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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class JDBCConecxioBBDD implements Interficie{
    //info.infomila.JDBCConecxioBBDD
    private Connection con;
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
    public void commit()  throws InterficieException{
        if(con != null){
            try {
                con.commit();
            } catch (SQLException ex) {
                throw new InterficieException("Error en fer el commit");
            }
        }
    }

    @Override
    public void borrarCategoria(Categoria c) throws InterficieException {
    }

    @Override
    public ArrayList<Ruta> getRutaList() throws InterficieException{
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G HH:mm:ss z");
        ArrayList<Ruta> ruts = new ArrayList<>();
        if(con != null){
            
            Statement st;
            Ruta auxR;
            Foto auxF;
            Categoria auxC;
            try {
                st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT r.rut_id as RUTID, r.rut_titol AS TITOL,\n" +
                                                "		r.rut_desc_markdown as DESCR, r.rut_desnivell AS DESNIVELL, r.rut_alcada_max AS ALCMAX,\n" +
                                                "		   r.rut_alcada_min AS ALCMIN, r.rut_distanciakm AS DIST,\n" +
                                                "		   r.rut_tempsaprox AS TEMPS, r.rut_circular AS CIRC,\n" +
                                                "		   r.rut_dificultat AS DIF, r.rut_gpxfile AS GPX,\n" +
                                                "		   f.foto_id AS FOTOID,f.foto_titol AS FOTOTITOL,f.foto_url AS FOTOURL,\n" +
                                                "		   c.cat_id AS CATID,c.cat_nom AS CATNOM\n" +
                                                "from ruta r join foto f on r.rut_foto = f.foto_id\n" +
                                                "			join categoria c on r.rut_cat = c.cat_id;");
                while (rs.next()) {
                    auxF = new Foto(rs.getInt("FOTOID"),
                                    rs.getString("FOTOURL"),
                                    rs.getString("FOTOTITOL"));
                    auxC = new Categoria(rs.getInt("CATID"),
                                        rs.getString("CATNOM"));

                    //rs.getDate("TEMPS");
                    //Date d = (Date)rs.getObject("TEMPS");
                    //java.sql.Date sqlDate = rs.getDate("TEMPS");
                    //Date newDate = new Date();//rs.getTimestamp("TEMPS");
                    //java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
                    
                    auxR = new Ruta(rs.getInt("RUTID"),
                                    rs.getString("TITOL"),
                                    rs.getString("DESCR"),
                                    rs.getFloat("DESNIVELL"),
                                    rs.getFloat("ALCMAX"),
                                    rs.getFloat("ALCMIN"),
                                    rs.getFloat("DIST"),
                                    //rs.getTimestamp("TEMPS"),
                                    rs.getBoolean("CIRC"),
                                    rs.getFloat("DIF"),
                                    rs.getString("GPX"),
                                    auxF,
                                    auxC);
                    /*auxR = new Ruta();
                    auxR.setId(rs.getInt("RUTID"));*/
                    ruts.add(auxR);
                }
                rs.close();
            } catch (SQLException ex) {
                throw new InterficieException("Error en recuperar la llista de categories");
            }
        }else {
            throw new InterficieException("Coneccio nula");
        }
        
        return ruts;
    }

    @Override
    public void rollback()  throws InterficieException{
        if(con!=null){
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new InterficieException("Error en fer el rollack");
            }
        }
    }

    @Override
    public ArrayList<Punt> getPutntsRuta(int r) throws InterficieException {
        ArrayList<Punt> punts = new ArrayList<>();
        if(con != null){
            Statement st;
            Punt auxP;
            Foto auxF;
            try {
                st = con.createStatement();
                String que = "select p.punt_numero as PUNTNUMERO,\n" +
                            "	p.punt_nom as PUNTNOM,\n" +
                            "        p.punt_desc as PUNTDESC,\n" +
                            "        p.punt_hora as HORA,\n" +
                            "        p.punt_lat as LAT,\n" +
                            "        p.punt_long as LONGITUD,\n" +
                            "        p.punt_elevacio as PUNTELEVACIO,\n" +
                            "        p.punt_ruta as RUTA,\n" +
                            "        f.foto_id as FOTOID,\n" +
                            "        f.foto_titol as FOTOTITOL,\n" +
                            "        f.foto_url as FOTOURL\n" +
                            "from punt p join foto f on p.punt_foto = f.foto_id\n" +
                            "where punt_ruta = " + r +" order by punt_numero";
                ResultSet rs = st.executeQuery(que);
                while (rs.next()) {
                    auxF = new Foto(rs.getInt("FOTOID"),
                                    rs.getString("FOTOTITOL"),
                                    rs.getString("FOTOURL"));
                 
                    auxP = new Punt(rs.getInt("PUNTNUMERO"),
                                    rs.getInt("RUTA"),
                                    rs.getString("PUNTNOM"),
                                    rs.getString("PUNTDESC"),
                                    //rs.getDate("HORA"),
                                    rs.getDouble("LAT"),
                                    rs.getDouble("LONGITUD"),
                                    rs.getDouble("PUNTELEVACIO"),
                                    auxF);
                    punts.add(auxP);
                    
                }
                rs.close();
            } catch (SQLException ex) {
                throw new InterficieException("Error en recuperar la llista de punts");
            }
        }else{
            throw new InterficieException("Coneccio nula");
        }
        return punts;
    }
    
    
    
    
}
