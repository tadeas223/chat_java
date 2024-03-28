package org.example.protocol;

import java.util.HashMap;

public class ParamList extends HashMap<String, String> {
    public ParamList() {

    }

    public ParamList(String key,String value){
        put(key,value);
    }
}
