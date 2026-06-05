package app.delegates.expenses;

import org.camunda.bpm.engine.delegate.BpmnError;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class CamundaDateValueReader {

    private CamundaDateValueReader() {
    }

    public static LocalDate readLocalDate(Object value, String errorCode, String errorMessage) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toLocalDate();
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toLocalDate();
        }
        if (value instanceof Instant instant) {
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (value instanceof Date date) {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (value instanceof String stringValue) {
            if (stringValue.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(stringValue);
            } catch (RuntimeException ignored) {
                throw new BpmnError(errorCode, errorMessage);
            }
        }
        throw new BpmnError(errorCode, errorMessage);
    }
}
