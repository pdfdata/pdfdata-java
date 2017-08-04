package io.pdfdata.model;

import io.pdfdata.JSON;

import java.io.IOException;
import java.util.Map;

/**
 * Base PDFDATA.io entity class.
 * @nodoc
 */
public class Entity {

    public String toString () {
        try {
            return super.toString() + JSON.to0(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
