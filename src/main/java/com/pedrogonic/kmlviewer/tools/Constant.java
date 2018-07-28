package com.pedrogonic.kmlviewer.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pedro Coelho
 */
public enum Constant {
    
    OUPUT_DIR("C:" + File.separator + "TEMP" + File.separator);
    
    String value;
    
    private static final Map<String, Constant> MAP;
    
    static {
        MAP = new HashMap();
        for (Constant c : Constant.values())
            MAP.put(c.getValue(), c);
    }
    
    private Constant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    
    
}
