package io.pdfdata.model.ops;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.pdfdata.model.Operation;

import java.util.List;

/**
 * @publicapi
 */
public class Text extends Operation {
    public static final Layout DEFAULT_LAYOUT = Layout.PRESERVE;

    // TODO it'd be nice to figure out how to not include this in serializations when it's default
    private Layout layout;

    public Text () {
        this(DEFAULT_LAYOUT);
    }

    public Text (Layout layout) {
        super("text");
        this.layout = layout;
    }

    public static enum Layout {
        PRESERVE, DECOMPOSE
    }

    public Layout getLayout() {
        return layout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Text text = (Text) o;

        return layout == text.layout;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + layout.hashCode();
        return result;
    }

    public static class Page extends io.pdfdata.model.Page {
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class Result extends Operation.Result {
        private List<Page> data;

        public Result() {
            super("text");
        }

        public List<Page> getData() {
            return data;
        }
    }
}
