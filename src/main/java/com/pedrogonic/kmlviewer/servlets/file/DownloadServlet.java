package com.pedrogonic.kmlviewer.servlets.file;

import com.pedrogonic.kmlviewer.servlets.ServletJSONResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.GeographicFileDAO;

/**
 * Servlet para download de arquivos
 * <p>
 * Para implementar um novo upload com este servlet basta:
 *  1-      Criar um valor no enum Method;
 *  2-      Buscar arquivo desejado dentro do switch do método getFileForMethod(). O 
 *          servlet enviará um json com mensagem "OK" para o cliente;
 *  3.a-    Utilizar funções implementadas no arquivo fileHelper.js
 *  OU
 *  3.b-    Implementar via ajax a chamada a este servlet, que deve ter como success
 *          uma function que chamará de novo este servlet com o parâmetro 
 *          DownloadExistingFile setado em true caso o arquivo exista.
 * <p>
 * @author Pedro Coelho
 */
@WebServlet(name = "DownloadServlet", urlPatterns = {"/DownloadServlet"})
public class DownloadServlet extends HttpServlet {

    private ServletJSONResponse res;
    
    /**
     * Enum que define os metodos existentes.
     */
    private enum Method {
        KMZDOWNLOAD("KMZDownload");
        
        private final String methodName;
        
        private final static Map<String, Method> MAP;
        
        static {
            MAP = new HashMap();
            for (Method m : Method.values())
                MAP.put(m.getMethodName(), m);
        }
        
        /**
         * Construtor
         * @param methodName nome do metodo a ser chamado no parametro method do request
         */
        private Method(String methodName) { this.methodName = methodName; }

        public static Method getMethodFromParameter(String param) throws Exception { 
            Method m = MAP.get(param);
            if (m == null)
                throw new Exception("Método não existente!");
            return m;
        }
        
        public String getMethodName() {
            return methodName;
        }
        
    }
    
    /**
     * Método que deve ser modificado para acrescentar novos métodos de Download
     * <p>
     * Para criar novos métodos, basta modificar o switch para buscar o 
     * arquivo desejado no novo método 
     * <p>
     * @param request
     * @param method
     * @return 
     */
    private File getFileForMethod(HttpServletRequest request, Method method) {
        
        File file = null;
        
        switch(method) {
            case KMZDOWNLOAD:
                Integer fileId = null;
                try {
                    fileId = Integer.parseInt(request.getParameter("fileId"));
                } catch(Exception e) { res.addError("ID do arquivo não informado!"); break; }
                
//                GeographicFileDAO dao = new GeographicFileDAO();
//                file = (dao.getFileFromId(fileId.intValue()) == null? null : dao.getFileFromId(fileId.intValue()).getFile());
                break;
        }
        
        return file;
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
        
        res = new ServletJSONResponse("OK");
        Boolean DOWNLOADEXISTINGFILE = Boolean.parseBoolean(request.getParameter("downloadExistingFile"));
        System.out.println("Executei com flag: " + DOWNLOADEXISTINGFILE);
        
        Method method = null;
        File file = null;
        
        try {
            method = Method.getMethodFromParameter(request.getParameter("method"));
        } catch(Exception e) { // Método inexixtente já envia resposta com erro.
            res.addError(e.getMessage());
            res.setMessage("ERR");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(res.toJSON());
        }
        
        file = getFileForMethod(request, method);
        
        /*
            Checando se foi passado o parâmetro para baixar um arquivo automaticamente,
            após verificação, ou (else) em caso negativo, verificar existência.
        */
        if (DOWNLOADEXISTINGFILE) { // Download automático após verificação
            
            /*
                Check desnecessário se o fluxo correto for seguido. 
                Prevenindo chamadas diretas de download através de flag 
                DOWNLOADEXISTINGFILE errada.
            */
            if (file != null) {
                
                response.setContentType("application/octet-stream");
                response.setContentLength((int) file.length());
                response.setHeader( "Content-Disposition",
                String.format("attachment; filename=\"%s\"", file.getName()));

                OutputStream out = response.getOutputStream();
                try (FileInputStream in = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    out.flush();
                } catch(Exception e) { res.addError(e.getMessage()); }
                

                
            } else { // Supostamente isso não vai acontecer, mas vale prevenir...
            
                res.addError("Arquivo não existe!"); 
                res.setMessage("ERR");
                
            }
                
        } else if (file == null) { // Verficação se arquivo existe.
            
            res.setMessage("ERR"); 
            res.addError("Arquivo não existe!"); 
        
        }
                
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
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
