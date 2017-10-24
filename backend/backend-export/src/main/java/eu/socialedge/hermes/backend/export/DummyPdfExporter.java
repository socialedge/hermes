package eu.socialedge.hermes.backend.export;

public class DummyPdfExporter extends PdfExporter<Dummy> {

    public DummyPdfExporter(String username, String apiKey, EntityConverter<Dummy, String> entityConverter) {
        super(username, apiKey, entityConverter);
    }
}
