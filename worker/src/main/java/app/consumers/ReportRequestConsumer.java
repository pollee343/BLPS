package app.consumers;

import app.dto.messages.ReportRequestMessage;
import app.services.ApplicationProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

//cлушает очередь legal-report.requests получает JSON-сообщение переаодит его в ReportRequestMessage запускает обработку заявки

@Component
@RequiredArgsConstructor
public class ReportRequestConsumer {

    private final ObjectMapper objectMapper;
    private final ApplicationProcessingService applicationProcessingService;

    @JmsListener(destination = "${app.queues.legal-report}")
    public void receive(String body) throws Exception {
        ReportRequestMessage message = objectMapper.readValue(body, ReportRequestMessage.class);
        applicationProcessingService.process(message.getApplicationId());
    }
}
