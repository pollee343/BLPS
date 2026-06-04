package app.consumers;

import app.dto.messages.ApplicationJiraExportMessage;
import app.services.ApplicationProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

// Слушает очередь выгрузки в Jira, получает applicationId и запускает обработку заявки.

@Component
@RequiredArgsConstructor
public class ReportRequestConsumer {

    private final ObjectMapper objectMapper;
    private final ApplicationProcessingService applicationProcessingService;

    @JmsListener(destination = "${app.queues.legal-report}")
    public void receive(String body) throws Exception {
        ApplicationJiraExportMessage message = objectMapper.readValue(body, ApplicationJiraExportMessage.class);
        applicationProcessingService.process(message.getApplicationId());
    }
}
