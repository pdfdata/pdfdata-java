package io.pdfdata.model.ops;

import io.pdfdata.model.Attachment;
import io.pdfdata.model.Image;
import io.pdfdata.model.Operation;

import java.io.IOException;
import java.util.List;

/**
 * @publicapi
 */
public class Attachments extends Operation {
    public Attachments () {
        super("attachments");
    }

    public static class Result extends Operation.Result {
        private List<Attachment> data;

        public Result() {
            super("attachments");
        }

        public List<Attachment> getData() {
            return data;
        }

        /**
         * @nodoc
         */
        public void registerResources () throws IOException {
            for (Attachment ch : data) {
                ch.registerResource(this);
            }
        }
    }
}
