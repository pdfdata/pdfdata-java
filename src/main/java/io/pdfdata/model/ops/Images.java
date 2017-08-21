package io.pdfdata.model.ops;

import io.pdfdata.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @publicapi
 */
public class Images extends Operation {
    public Images () {
        super("images");
    }

    public static class Page extends io.pdfdata.model.Page {
        private List<Image> images;

        public List<Image> getImages() {
            return images;
        }
    }

    public static class Result extends Operation.Result {
        private List<Page> data;

        public Result() {
            super("images");
        }

        public List<Page> getData() {
            return data;
        }

        /**
         * @nodoc
         */
        public void registerResources () throws IOException {
            for (Images.Page page : data) {
                for (Image img : page.getImages()) {
                    img.registerResource(this);
                }
            }
        }
    }
}
