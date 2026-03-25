package app.model.utils;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationInformation {
    private LocalDateTime time;
    private String name;
    private String description;
}
