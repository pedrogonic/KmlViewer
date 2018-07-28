package com.pedrogonic.kmlviewer.servlets;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pedro Coelho
 */
public class ServletJSONResponse {
    
    private String message;
    private List<String> errors = null;
    private Object obj;
    
    /**
     * Default constructor
     * <p>
     * @param status
     * @param message 
     */
    public ServletJSONResponse(String message) {
        this.message = message;
    }
    
    /**
     * Default constructor with errors list
     * <p>
     * @param status
     * @param message
     * @param errors 
     */
    public ServletJSONResponse(String message, List<String> errors) {
        this(message);
        this.errors = errors;
    }
    
    /**
     * Constructor adding one error to error list
     * <p>
     * @param status
     * @param message
     * @param error 
     */
    public ServletJSONResponse(String message, String error) {
       this(message);
       addError(error);
    }
    
    /**
     * Adds error to error list
     * <p>
     * Returns number of errors
     * @param error error to be added
     * @return number of errors
     */
    public int addError(String error) {
        if (errors == null)
            errors = new ArrayList();
        errors.add(error);
        return errors.size();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Object getObj() {
        return obj;
    }

    /**
     * Sets the object to send as a response
     * <p>
     * Filed obj gets NULL if object is not Serializable by the Gson library.
     * @param obj object to be returned
     */
    public void setObj(Object obj) {
        try {
            
            Gson gson = new Gson();
            gson.toJson(obj);

            this.obj = obj;
            
        } catch(Exception e) {
            this.obj = null;
            System.out.println("Couldn´t serialize object");
        }
    }
    
    /**
     * Returns the JSON representation of this object
     * <p>
     * @return JSON String
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
}
