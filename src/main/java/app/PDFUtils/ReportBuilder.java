package app.PDFUtils;

import app.model.entities.MoneyOperation;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import app.model.utils.OperationInformation;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ReportBuilder {

    @Value("${FONT_DIR}")
    private String fontPath;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    DateTimeFormatter formatterToText =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));

    public byte[] getBillReport(String accountNumber, LocalDate fromDate, LocalDate toDate, UserData userData,
                                 BigDecimal total, BigDecimal remains, BigDecimal rem_on_from, String email,
                                 List<MoneyOperation> incomes) throws IOException {

        String from = fromDate.format(formatter);
        String to = toDate.format(formatter);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfFont russianFont = PdfFontFactory.createFont(fontPath, "Identity-H");

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc).setFont(russianFont);

        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new PageNumberHandler());

        document.add(new Paragraph().setBold().setTextAlignment(TextAlignment.CENTER).add(new Text("Счет").setFontSize(20)));
        document.add(new Paragraph().setBold().setTextAlignment(TextAlignment.CENTER).add(new Text("№ " + accountNumber).setFontSize(20)));


        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        Cell leftCell = new Cell()
                .add(new Paragraph().add(new Text("Кому: ").setBold())
                        .add(new Text(userData.getUser().getLastName()
                                + " " + userData.getUser().getFirstName()
                                + " " + userData.getUser().getMiddleName() + "\n"))
                        .add(new Text("Куда: ").setBold())
                        .add(new Text(email  + "\n"))
                        .add(new Text("Лицевой счет: "))
                        .add(new Text(accountNumber + "\n").setBold()))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);

        Cell rightCell = new Cell()
                .add(new Paragraph()
                        .add(new Text("от ")).add(to  + "\n")
                        .add(new Text("за услуги предприятия\n"))
                        .add(new Text("ПАО \"МТС\"\n"))
                        .add(new Text("Российская Федерация, 109147, г. Москва, ул. Марксистская, д.4, стр. 1\n"))
                        .add(new Text("за период с "))
                        .add(from)
                        .add(new Text("по "))
                        .add(to))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(leftCell);
        table.addCell(rightCell);
        document.add(table);

        document.add(new Paragraph()
                .add(new Text("Лицевой счет абонента № " + accountNumber).setBold().setFontSize(18))
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER)
                .add(new Text("Израсходовано за период: " + total.toString() + " руб.").setBold().setFontSize(20)));

        document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER)
                .add(new Text("Остаток на " + to.toString() + " " + remains.toString() + " руб.").setBold().setFontSize(20)));

        document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER)
                .add(new Text("ПАО \"МТС\" выражает Вам свою благодарность за своевременную оплату счета.").setItalic()));

        document.add(new Div().setHeight(200));

        table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        leftCell = new Cell()
                .add(new Paragraph()
                        .add(new Text("Руководитель организации или иное\n" +
                                "уполномоченное лицо (подпись, фио)\n")))
                .add(new Paragraph()
                        .add(new Text(".......................")
                                .setTextAlignment(TextAlignment.CENTER)))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);

        rightCell = new Cell()
                .add(new Paragraph()
                        .add(new Text("Главный бухгалтер или иное\n" +
                                "уполномоченное лицо (подпись, фио)\n")))
                .add(new Paragraph()
                        .add(new Text(".......................")
                                .setTextAlignment(TextAlignment.CENTER)))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);

        table.addCell(leftCell);
        table.addCell(rightCell);
        document.add(table);

        document.add(new Div().setHeight(70));

        document.add(new Paragraph()
                .add(new Text("Платежные реквизиты:\n" +
                        "ПАО \"МТС\"\n" +
                        "ИНН/КПП: 7740000076/997750001\n" +
                        "Расчетные счета:\n" +
                        "№ 40822810800000000004 в ПАО \"МТС-Банк\" БИК 044525232\n" +
                        "кор. счёт 30101810600000000232, идентификатор платежа:\n" +
                        "926136652051600000000ЛС12").setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT));

        document.add(twoSideTextMaking(new Text("Остаток на " + from).setBold().setItalic().setFontSize(14),
                new Text("руб.\n" + rem_on_from)).setBold().setItalic().setFontSize(14));

        document.add(new Paragraph()
                .add(new Text("Расход ресурсов:").setBold().setItalic().setFontSize(14))
                .setTextAlignment(TextAlignment.LEFT));

        document.add(new Paragraph()
                .add(new Text("Услуги МТС " + total).setBold().setFontSize(14))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1)));

        document.add(twoSideTextMaking(new Text("Израсходовано за период: ").setBold().setFontSize(18),
                new Text(total.toString())).setBold().setFontSize(14));

        document.add(new Paragraph()
                .add(new Text("Платежи / корректировки:").setBold().setItalic().setFontSize(14))
                .setTextAlignment(TextAlignment.LEFT));

        table = new Table(UnitValue.createPointArray(new float[]{220f, 120f, 135f, 135f, 220f, 115f}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(cell("Тип платежа"));
        table.addHeaderCell(cell("Сумма\nплатежного\nдокумента,\nруб."));
        table.addHeaderCell(cell("Дата платежа/\nкорректировки"));
        table.addHeaderCell(cell("Дата зачисления\nна л.с."));
        table.addHeaderCell(cell("Примечание"));
        table.addHeaderCell(cell("Зачислено\nна л.с., руб."));

        BigDecimal income_sum = BigDecimal.ZERO;

        for (MoneyOperation income: incomes) {
            income_sum = income_sum.add(income.getAmount());
            table.addCell(income.getName());
            table.addCell(income.getAmount().toString());
            table.addCell(income.getOperationTime().toLocalDate().format(formatter));
            table.addCell(income.getOperationTime().toLocalDate().format(formatter));
            table.addCell("Регистрация платежа");
            table.addCell(income.getAmount().toString());
        }
        document.add(table);

        document.add(twoSideTextMaking(new Text("Итого ").setBold().setItalic().setFontSize(14),
                new Text(income_sum.toString())).setBold().setItalic().setFontSize(14));

        document.add(twoSideTextMaking(new Text("Остаток на " + to).setBold().setItalic().setFontSize(14),
                new Text(remains.toString())).setBold().setItalic().setFontSize(14));

        document.close();

        return out.toByteArray();
    }

    public byte[] getInformationAboutExpensesReport(String accountNumber, LocalDate fromDate, LocalDate toDate,
                                                    UserData userData, Map<String, BigDecimal> forCategory,
                                                    List<OperationInformation> serviceUsages) throws IOException {
        String from = fromDate.format(formatter);
        String to = toDate.format(formatter);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfFont russianFont = PdfFontFactory.createFont(fontPath, "Identity-H");

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc).setFont(russianFont);

        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new PageNumberHandler());

        document.add(new Paragraph().setBold().setTextAlignment(TextAlignment.LEFT).
                add(new Text("Детализация " + this.formatPhone(userData.getPhoneNumber())).setFontSize(20)));

        document.add(twoSideTextMaking(new Text(fromDate.format(formatterToText) + " - " + toDate.format(formatterToText)),
                new Text("Данные актуальны на " + LocalDateTime.now().format(formatter))));

        document.add(new Div().setHeight(10));

        document.add(new Paragraph("Списания по категориям").setBold().setFontSize(16));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth().setBorder(Border.NO_BORDER);

        BigDecimal sum = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : forCategory.entrySet()) {
            sum = sum.add(entry.getValue());
            table.addCell(twoSideTextMaking(new Text(entry.getKey()), new Text(entry.getValue().toString()))
                    .setBorder(Border.NO_BORDER));
        }
        document.add(table);
        document.add(new Paragraph(new Text("Всего: " + sum.toString()).setBold().setTextAlignment(TextAlignment.RIGHT))
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(new Div().setHeight(10));

        document.add(new Paragraph("История транзакций").setBold().setFontSize(16));

        LocalDate now_day = serviceUsages.get(0).getTime().toLocalDate();
        document.add(new Paragraph(new Text(now_day.format(formatter)).setBold()));
        table = new Table(UnitValue.createPercentArray(new float[]{12, 68, 20}))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER);

        for (OperationInformation serviceUsage: serviceUsages) {
            if (now_day.isBefore(serviceUsage.getTime().toLocalDate())) {
                now_day = serviceUsage.getTime().toLocalDate();
                document.add(table);
                document.add(new Paragraph(new Text(now_day.format(formatter)).setBold()));
                table = new Table(UnitValue.createPercentArray(new float[]{12, 68, 20}))
                        .useAllAvailableWidth()
                        .setBorder(Border.NO_BORDER);
            }

            Cell timeCell = new Cell()
                    .add(new Paragraph(formatTime(LocalTime.from(serviceUsage.getTime()))))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingRight(10)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);

            Cell contentCell = new Cell()
                    .add(new Paragraph(serviceUsage.getName())
                            .setBold()
                            .setFontSize(13)
                            .setMarginTop(0)
                            .setMarginBottom(6))
                    .add(new Paragraph(serviceUsage.getDescription())
                            .setFontSize(12)
                            .setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY)
                            .setMultipliedLeading(1.25f))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingRight(10)
                    .setVerticalAlignment(VerticalAlignment.TOP);

            Cell amountCell = new Cell()
                    .add(new Paragraph(new Text("0 P"))
                            .setBold()
                            .setFontSize(13)
                            .setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingTop(0)
                    .setPaddingBottom(0)
                    .setPaddingLeft(0)
                    .setPaddingRight(0)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.RIGHT);

            table.addCell(timeCell);
            table.addCell(contentCell);
            table.addCell(amountCell);
            table.addCell(new Cell(1, 3)
                    .add(new Paragraph(""))
                    .setBorder(Border.NO_BORDER)
                    .setHeight(14)
                    .setPadding(0)
                    .setMargin(0));
        }
        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private Table twoSideTextMaking(Text text1, Text text2) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth().setBorder(Border.NO_BORDER);

        Cell leftCell = new Cell()
                .add(new Paragraph()
                        .add(text1)
                        .setTextAlignment(TextAlignment.LEFT))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);

        Cell rightCell = new Cell()
                .add(new Paragraph()
                        .add(text2)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(leftCell);
        table.addCell(rightCell);
        return table;
    }

    private Cell cell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setPadding(4)
                .setBorder(new SolidBorder(1))
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private String formatPhone(String phone) {
        if (phone == null) return "";

        String digits = phone.replaceAll("\\D", "");

        if (digits.length() == 11 && digits.startsWith("8")) {
            digits = digits.substring(1);
        } else if (digits.length() == 11 && digits.startsWith("7")) {
            digits = digits.substring(1);
        }

        if (digits.length() != 10) {
            return phone;
        }

        return String.format("+7 (%s) %s-%s-%s",
                digits.substring(0, 3),
                digits.substring(3, 6),
                digits.substring(6, 8),
                digits.substring(8, 10));
    }
}
