package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import io.pdfdata.model.Operation;
import io.pdfdata.model.Proc;
import io.pdfdata.model.ProcessedDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.pdfdata.Network.Method.GET;
import static io.pdfdata.Network.Method.POST;
import static io.pdfdata.Util.*;

/**
 * API request facility enabling the creation of new {@link Proc}s, and the retrieval of
 * existing {@link Proc}s.
 *
 * Example:
 *
 * <pre>API pdfdata = new API();
 * Proc proc = pdfdata.procs().configure()
 *     .withFiles(new File("path/to/document.pdf"))
 *     .withOperations(new Metadata())
 *     .start();</pre>
 *
 * @publicapi
 */
public class ProcsRequest extends Request {
    private static final String base = "/procs";
    private static final TypeReference<Proc> TYPEREF = new TypeReference<Proc>() {};
    private static final TypeReference<List<Proc>> TYPEREF_LIST =
            new TypeReference<List<Proc>> () {};
    private static final int DEFAULT_WAIT = 30;

    ProcsRequest (API pdfdata) {
        super(pdfdata);
    }

    private static final Set<String> RESOURCEFUL_OPS =
            Util.setFrom("attachments", "images", "xmp-metadata");

    private Proc registerResources (Proc proc) throws IOException {
        if (proc.getDocuments() != null) {
            List<Integer> resourceOps = new ArrayList<>();
            for (int i = 0, len = proc.getOperations().size(); i < len; i++) {
                Operation op = proc.getOperations().get(i);
                if (RESOURCEFUL_OPS.contains(op.getOperationName())) {
                    resourceOps.add(i);
                }
            }

            if (!resourceOps.isEmpty()) {
                for (ProcessedDocument d : proc.getDocuments()) {
                    for (int opNumber : resourceOps) {
                        d.getResults().get(opNumber).registerResources();
                    }
                }

            }
        }

        return proc;
    }

    /**
     * Retrieves a {@link Proc} given its ID.
     */
    public Proc byID (String procid) throws IOException {
        return registerResources(doRequest(GET, base + "/" + procid, TYPEREF));
    }

    /**
     * Initializes and returns a builder to configure and start a new {@link Proc}.
     */
    public ProcCreationBuilder configure () {
        return new ProcCreationBuilder();
    }

    public class ProcCreationBuilder {
        private final Set<File> files;
        private final Set<String> documentIDs;
        private final Set<String> documentTags;
        private final ArrayList<Operation> operations;
        private final int wait;

        ProcCreationBuilder () {
            this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new ArrayList<>(), DEFAULT_WAIT);
        }

        ProcCreationBuilder(Set<File> files, Set<String> docIDs,
                            Set<String> documentTags,
                            ArrayList<Operation> operations, int wait) {
            this.files = files == null ? new HashSet<>() : files;
            this.documentIDs = docIDs == null ? new HashSet<>() : docIDs;
            this.documentTags = documentTags == null ? new HashSet<>() : documentTags;
            this.operations = operations == null ? new ArrayList<>() : operations;
            this.wait = wait;
        }

        /**
         * Starts a new proc, returning a {@link Proc} entity representing it. This method will
         * block depending upon the configured {@link #wait() wait value}.
         *
         * @throws IllegalArgumentException if certain configuration invariants found not to
         * hold, e.g. that both files and document IDs have been provided
         * @throws IOException
         */
        public Proc start () throws IOException {
            Map<String, Object> params = kvmap("docid", documentIDs,
                    "file", files, "tag", documentTags);
            ArrayList documentIdentifiersUsed = new ArrayList();
            for (Map.Entry param : params.entrySet()) {
                if (((Set)param.getValue()).size() > 0) {
                    documentIdentifiersUsed.add(param.getKey());
                }
            }
            switch (documentIdentifiersUsed.size()) {
                case 1: break;
                case 0:
                    throw new IllegalArgumentException("No source documents were specified, " +
                            "cannot create proc. You must provide: a set " +
                            "of files to upload and process OR a set of tags used" +
                            " to identify previously-uploaded source documents OR a set of " +
                            "document IDs. Use one of `.withFiles()`, `.withTags()`, or " +
                            "`.withDocumentIDs()` prior to attempting to `.start()` the proc.");
                default:
                    throw new IllegalArgumentException(
                            String.format("Attempted to start a proc with more than one type of " +
                                    "identifier of source documents: %s. Only *one* of new files " +
                                    "to upload, document tags, or document IDs can be provided " +
                                    "when configuring a new proc.", documentIdentifiersUsed));
            }

            if (wait != DEFAULT_WAIT) params.put("wait", wait);
            params.put("operations", operations);
            return registerResources(doRequest(POST, base, params, TYPEREF));
        }

        public ProcCreationBuilder withDocumentIDs (String... docIDs) {
            return withDocumentIDs(Arrays.asList(docIDs));
        }

        public ProcCreationBuilder withDocumentIDs (Collection<String> docIDs) {
            return new ProcCreationBuilder(files, union(documentIDs, setFrom(docIDs)),
                    documentTags, operations, wait);
        }

        public ProcCreationBuilder withFiles (File... files) {
            return withFiles(Arrays.asList(files));
        }

        public ProcCreationBuilder withFiles (String... paths) {
            return withFiles(Arrays.asList(paths));
        }

        /**
         * @param paths a collection of {@link File}s and/or {@link String} paths denoting files
         */
        public ProcCreationBuilder withFiles (Collection paths) {
            return new ProcCreationBuilder(
                    union(this.files, (Set<File>)paths.stream()
                            .map(s -> s instanceof File ? s : new File((String)s))
                            .collect(Collectors.toSet())),
                    documentIDs, documentTags, operations, wait);
        }

        public ProcCreationBuilder withTags (String... tags) {
            return withTags(Arrays.asList(tags));
        }

        public ProcCreationBuilder withTags (Collection<String> tags) {
            return new ProcCreationBuilder(files, documentIDs,
                    union(this.documentTags, setFrom(tags)), operations, wait);
        }

        public ProcCreationBuilder withOperations (Operation... operations) {
            return withOperations(Arrays.asList(operations));
        }

        public ProcCreationBuilder withOperations (Collection<Operation> operations) {
            ArrayList<Operation> ops = new ArrayList<>(this.operations);
            ops.addAll(operations);
            return new ProcCreationBuilder(files, documentIDs, documentTags, ops, wait);
        }

        public ProcCreationBuilder withWait (int seconds) {
            return new ProcCreationBuilder(files, documentIDs, documentTags, operations, seconds);
        }
    }

}