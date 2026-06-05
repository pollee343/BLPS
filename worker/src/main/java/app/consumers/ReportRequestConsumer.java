package app.consumers;

import app.dto.messages.ApplicationJiraExportMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

// Слушает очередь выгрузки в Jira, получает applicationId и запускает обработку заявки.

@Component
@RequiredArgsConstructor
public class ReportRequestConsumer {

    private final ObjectMapper objectMapper;
    private final RuntimeService runtimeService;

    @JmsListener(destination = "${app.queues.legal-report}")
    public void receive(String body) throws Exception {
        ApplicationJiraExportMessage message = objectMapper.readValue(body, ApplicationJiraExportMessage.class);
        runtimeService.createMessageCorrelation("PromisedPaymentRejectionCreated")
                .setVariable("applicationId", message.getApplicationId())
                .correlateStartMessage();
    }
}
