package com.pedrogonic.kmlviewer.dto;

import com.pedrogonic.kmlviewer.tools.file.UploadItem;
import dao.GeographicFileDAO;
import java.io.File;
import javax.servlet.http.Part;

/**
 * Classe que representa um arquivo KML/KMZ
 * <p>
 * @author Pedro Coelho
 */
public class GeographicFile extends UploadItem {

    private int id;
    private int fiscCod;
    
    /**
     * Construtor simples que identifica completamente um arquivo no BD
     * <p>
     * @param id 
     */
    public GeographicFile(int id) {
        super(null, null, null);
        this.id = id;
    }
    
    /**
     * Construtor completo
     * <p>
     * @param id
     * @param fiscCod 
     * @param fullPath 
     */
    public GeographicFile(int id, int fiscCod, String fullPath) {
        super(null, null, null);
        this.id = id;
        this.fiscCod = fiscCod;
        this.file = new File(fullPath);
    }
    
    /**
     * Construtor para uso pelo UploadServlet
     * <p>
     * @param part
     * @param partName
     * @param dir
     * @param fiscCod
     * @throws Exception 
     */
    public GeographicFile(Part part, String partName, String dir, Integer fiscCod) throws Exception {
        super(part, partName, dir);
        
        this.fiscCod = fiscCod;
    }

    /**
     * Processa o arquivo KMZ/KML
     * <p>
     * Associa o arquivo a uma atividade e parseia o KML para inclusão do seu 
     * conteúdo no BD.
     * @throws Exception 
     */
    @Override
    protected void process() throws Exception {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFiscCod() {
        return fiscCod;
    }

    public void setFiscCod(int fiscCod) {
        this.fiscCod = fiscCod;
    }
    
}
