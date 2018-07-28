package com.pedrogonic.kmlviewer.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.Part;

/**
 * Classe genérica para upload de arquivos pelo UploadServlet
 * <p>
 * @author Pedro Coelho
 */
public class UploadItem {
    
    protected String dir;
    protected Part part;
    protected String partName;
    protected File file;
    
    public UploadItem(Part part, String partName, String dir){
        this.dir = dir;
        this.partName = partName;
        this.part = part;
    }
    
    public void write() throws Exception {
        
        OutputStream out = null;
        InputStream filecontent = null;
        
        try {
            File path = new File(dir);
            path.mkdirs();
            
            file = new File(dir + partName);
            file.createNewFile();

            out = new FileOutputStream(file);
            
            filecontent = part.getInputStream();
            
            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Erro escrevendo arquivo no disco!");
        } finally {
            if (out != null) {
            out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }
        
        process();

    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
    
    /**
     * Implement this method to process the input file (e.g.: save in DB)
     * @throws Exception 
     */        
    protected  void process() throws Exception{};
}
