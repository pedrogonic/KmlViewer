package com.pedrogonic.kmlviewer.servlets.file;

import com.pedrogonic.kmlviewer.servlets.ServletJSONResponse;
import com.pedrogonic.kmlviewer.tools.Constant;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;

/**
 * Serlvet para upload de arquivos.
 * <p>
 * Para implementar um novo upload com este servlet basta:
 *   1-   Criar uma nova entrada no enum Method, informando o nome do método,
 *        uma lista dos arquivos aceitos (se o arquivo não estiver listado no enum FileType,
 *        criar novo valor) e o diretório de destino;
 *   2-   Modificar os comandos SWITCH nos métodos doPost, processRequest e writeFileItem
 *        para incluir o novo método e adicionar tratamento ao upload específico;
 *   3-   (Opcional) Criar uma classe que estenda tools.uploaditems.UploadItem para ter 
 *        suporte à escrita ao Banco ou qualquer outro tipo de tratamento após escrita em disco
 *        dos arquivos enviados;
 *   4.a- Utilizar as funções do arquivo fileHelper.js para subir um arquivo no front-end;
 *   OU
 *   4.b- Implementar upload de arquivo no front-end.
 * <p>
 * @author Pedro Coelho
 */
@WebServlet("/UploadServlet")
@MultipartConfig(
		fileSizeThreshold 	= 1024 * 1024 * 1, //1MB
		maxFileSize 		= 1024 * 1024 * 50, //50MB
		maxRequestSize		= 1024 * 1024 * 50) //50MB
public class UploadServlet extends HttpServlet {

    private Method method;
    private ServletJSONResponse res;
    private int totalFilesCount;
    private int successfulFilesCount;
    
    
    /**
     * Enum que define arquivos que podem ser aceitos ou barrados pelo servlet
     */
    public enum FileType {
        KMZ(".kmz"), KML(".kml"), XLS(".xls"), XLSX(".xlsx"),
        CSV(".csv"), TSV(".tsv"), PDF(".pdf"), TXT(".txt");
        
        private final String extension;
        
        private static Map<String, FileType> MAP;
        
        static {
            MAP = new HashMap();
            for (FileType ft : FileType.values())
                MAP.put(ft.getExtension(), ft);
        }
        
        public static FileType fromString(final String extension) throws Exception {
            FileType ft = MAP.get(extension);
            if (ft == null)
                throw new Exception("Tipo de arquivo não suportado!");
            return ft;
        }
        
        FileType(String extension) { this.extension = extension; }
        
        public String getExtension() { return extension; }
    }
    
    /**
     * Enum que define os metodos existentes.
     */
    private enum Method {
        KMZUPLOAD("KMZUpload", new FileType[] {FileType.KMZ, FileType.KML}, Constant.OUPUT_DIR.getValue());
        
        private final String methodName;
        private final List<FileType> acceptedTypes;
        private String outputDir;
        
        private final static Map<String, Method> MAP;
        
        static {
            MAP = new HashMap();
            for (Method m : Method.values())
                MAP.put(m.getMethodName(), m);
        }
        
        /**
         * Construtor
         * @param methodName nome do metodo a ser chamado no parametro method do request
         * @param acceptedTypes lista com os FileTypes aceitos
         * @param outputDir destino dos arquivos, que pode ser modificado pelo método appendToOutputDir
         */
        private Method(String methodName, FileType[] acceptedTypes, String outputDir) {
            this.methodName = methodName;
            this.acceptedTypes = Arrays.asList(acceptedTypes);
            this.outputDir = outputDir;
        }

        public static Method getMethodFromParameter(String param) throws Exception { 
            Method m = MAP.get(param);
            if (m == null)
                throw new Exception("Método não existente!");
            return m;
        }
        
        public String getMethodName() {
            return methodName;
        }

        public List<FileType> getAcceptedTypes() {
            return acceptedTypes;
        }

        public String getOutputDir() {
            return outputDir;
        }
        
        /**
         * Método para modificar o destino do arquivo
         * <p>
         * Ex: incluir subpastas com o ID da atividade associada ao arquivo.
         * @param path 
         */
        public void appendToOutputDir(String path) {
            outputDir += path;
        }
        
        public boolean contains(FileType fileType) {
            return acceptedTypes.contains(fileType);
        }
        
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        /**
         * Add custom handler to methods
         */
        switch(method) {
            default:
                break;
        }
        
        String plural = (successfulFilesCount > 1? "s" : "");
        
        res.setMessage(
                (successfulFilesCount > 0? successfulFilesCount : "Nenhum")
                + " arquivo" + plural + " salvo" + plural + "!");
        
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(res.toJSON());
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        res = new ServletJSONResponse("OK");
        
        try {
            method = Method.getMethodFromParameter(request.getParameter("method"));
        } catch(Exception e) {
            res.setMessage("Erro!");
            res.addError(e.getMessage());
            response.setStatus(500);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(res.toJSON());
            return;
        }
        
        successfulFilesCount = 0;
        
        Object additionalInformation = null;
        
        /**
         * Add custom handler to methods
         */
        switch(method) {
            case KMZUPLOAD:
                
                Integer fiscCod;
                try {
                    fiscCod = Integer.parseInt(request.getParameter("fiscCodUpload"));
                } catch (Exception e) {
                    res.setMessage("Erro!");
                    res.addError("Fiscalização não selecionada!");
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(res.toJSON());
                    return;
                }
                additionalInformation = fiscCod;
                
                method.appendToOutputDir(fiscCod + File.separator);
            default:
                break;
        }
        
        Collection<Part> parts;
        
        try {
            parts = request.getParts();

            totalFilesCount = parts.size();

            for (Part part : parts) {
                String fileName = getFileName(part);
                checkFile(part, fileName, additionalInformation);
            }
                        
            processRequest(request, response);
            
        } catch (Exception ex) {
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void checkFile(Part part, String partName, Object additionalInformation) {
        try {
                
            FileType partType = FileType.fromString("." + FilenameUtils.getExtension(partName));

            if (method.contains(partType))
                writeFileItem(part, partName, additionalInformation);
            else 
                res.addError("Tipo do arquivo " + part.getName() + " não suportado!");
                
        } catch(Exception e) {
            e.printStackTrace();
            res.addError("Tipo do arquivo " + part.getName() + " não suportado!");
        }
    }
    
    private void writeFileItem(Part part, String partName, Object additionalInformation) {
        
        try {
            UploadItem uploadItem = null;

            switch(method) {
                case KMZUPLOAD:
                    uploadItem = new GeographicFile(part, partName, method.getOutputDir(), (Integer) additionalInformation);
                    break;
                default:
                    uploadItem = new UploadItem(part, partName, method.getOutputDir());
                    break;
            }
            
            if (uploadItem != null)
                uploadItem.write();
            
            successfulFilesCount++;
        } catch(Exception e) {
            e.printStackTrace();
            res.addError("Erro no arquivo " + partName + ": " + e.getMessage());
        }
        
    }
    
    private String getFileName(final Part part) {   
        final String partHeader = part.getHeader("content-disposition");
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet para fazer uploads de arquivos aceitos pelo método apontado.";
    }// </editor-fold>

}
