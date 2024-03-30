package org.protocol;

import java.util.HashMap;

/**
 * This class should be used for saving string key value pairs.
 * This class works the same as the normal {@link HashMap} it works only with {@link String} values.
 */
public class ParamList extends HashMap<String, String> {
    public ParamList() {

    }

    /**
     * Adds the key and value into the list.
     *
     * @param key   key to be added
     * @param value value that should be paired with the key
     */
    public ParamList(String key, String value) {
        put(key, value);
    }
}
