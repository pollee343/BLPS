package app.PDFUtils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.IOException;

public class PageNumberHandler implements IEventHandler {
    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        String pageNumber = String.format("Страница %d из %d",
                pdf.getPageNumber(page), pdf.getNumberOfPages());

        PdfCanvas canvas = new PdfCanvas(page);
        try {
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 10)
                    .moveText(pageSize.getWidth() / 2 - 40, 30) // Внизу страницы
                    .showText(pageNumber)
                    .endText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
