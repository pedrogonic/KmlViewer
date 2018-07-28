package dao;

import core.data.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.map.GeographicFile;
import org.apache.poi.hssf.record.chart.DatRecord;

/**
 * Classe de acesso ao banco para informações relacionadas à classe GeographicFile
 * <p>
 * @author Pedro Coelho
 */
public class GeographicFileDAO {
    
    GeographicFile geographicFile;
    
    public GeographicFileDAO() {}
    
    public GeographicFileDAO(GeographicFile geographicFile) {
        this.geographicFile = geographicFile;
    }
    
    /**
     * Cria no banco de dados uma associação entre este arquivo e uma atividade.
     * <p>
     * @param db BD de destino
     * @param fiscCod id da atividade
     * @throws Exception 
     */
    public void saveActivityAssociation(Database db, int fiscCod) throws Exception {
        
        PreparedStatement ps = db.prepareStatement("insert into " + db.getSchema() +".FACT_GEO_FILE (FILE_PATH, FISC_COD) values (?,?)");
        ps.setString(1, geographicFile.getFile().getAbsolutePath());
        ps.setInt(2, fiscCod);

        ps.executeUpdate();
            
    }
    
    /**
     * Deleta um arquivo do banco pelo ID
     * <p>
     * @return true se sucesso
     * @throws java.lang.Exception problema de deleção no disco ou no BD
     */
    public boolean deleteFile() throws Exception {
        
        if (geographicFile == null)
            throw new Exception("Inicializar DAO com arquivo a ser deletado!");
        
        try ( Database db = new Database() ){
            
            try {
                db.openConnection();

                PreparedStatement ps = db.prepareStatement("delete from " + db.getSchema() + ".FACT_GEO_FILE "
                                                            + "where FILE_COD = ?;");
                ps.setInt(1, geographicFile.getId());

                ps.executeUpdate();

                try {

                    geographicFile.getFile().delete();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Falha ao apagar arquivo do disco!");   
                }
                
            } catch(Exception e) {
                db.setRollback(true);
                throw(e);
            } 
            
        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Falha ao desassociar arquivo no BD!");   
        }
        
        return true;
        
    }
    
    /**
     * Buscar os arquivos referentes a uma atividade
     * <p>
     * @param fiscCod id da atividade
     * @return lista de arquivos
     */
    public List<GeographicFile> getFilesFromActivityCod(int fiscCod) {
        
        List<GeographicFile> list = new ArrayList();
        
        try ( Database db = new Database() ) {
            
            db.openConnection();
            PreparedStatement ps = db.prepareStatement("select \n"
                                                        + "FILE_COD, \n"
                                                        + "FILE_PATH \n"
                                                        + "from " + db.getSchema() + ".FACT_GEO_FILE "
                                                        + "where FISC_COD = ?;");
            ps.setInt(1, fiscCod);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                list.add(new GeographicFile(rs.getInt(1), fiscCod, rs.getString(2)));
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Busca de um arquivo pelo seu ID
     * <p>
     * @param id ID do arquivo
     * @return arquivo
     */
    public GeographicFile getFileFromId(int id) {
        
        GeographicFile geographicFile = null;
        
        try ( Database db = new Database() ) {
            
            db.openConnection();
            PreparedStatement ps = db.prepareStatement("select \n"
                                                        + "FISC_COD, \n"
                                                        + "FILE_PATH \n"
                                                        + "from " + db.getSchema() + ".FACT_GEO_FILE "
                                                        + "where FILE_COD = ?;");
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                geographicFile = new GeographicFile(id, rs.getInt(1), rs.getString(2));
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return geographicFile;
        
    }
    
}
