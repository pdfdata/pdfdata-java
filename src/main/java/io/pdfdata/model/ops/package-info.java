/**
 * This package contains classes modeling each of the PDF content- and
 * data-extraction {@link io.pdfdata.model.Operation}s offered by
 * <a href="https://www.pdfdata.io">PDFDATA.io</a>. You
 * will use these to {@link io.pdfdata.ProcsRequest.ProcCreationBuilder#withOperations(io.pdfdata.model.Operation...)
 * configure} and start new {@link io.pdfdata.model.Proc}s, and also to access data extraction
 * {@link io.pdfdata.model.Operation.Result}s
 * {@link io.pdfdata.model.ProcessedDocument#getResults() via completed procs}.
 *
 * Complete documentation on the types of PDF operations available is available
 * in the <a href="https://www.pdfdata.io/apidoc/?java#operations">PDFDATA.io API reference</a>.
 *
 * @publicapi
 */
package io.pdfdata.model.ops;
