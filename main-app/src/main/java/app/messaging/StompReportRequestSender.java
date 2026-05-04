package app.messaging;

import app.dto.messages.ReportRequestMessage;
import app.services.interfases.ReportRequestSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.transport.stomp.StompConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class StompReportRequestSender implements ReportRequestSender {

    private final ObjectMapper objectMapper;

    @Value("${activemq.stomp.host:localhost}")
    private String host;

    @Value("${activemq.stomp.port:61613}")
    private int port;

    @Value("${activemq.stomp.login:admin}")
    private String login;

    @Value("${activemq.stomp.passcode:admin}")
    private String passcode;

    @Value("${activemq.report-requests.destination:/queue/report.requests}")
    private String destination;

    @Override
    public void send(Long applicationId) {
        String body = toJson(new ReportRequestMessage(applicationId));
        StompConnection connection = new StompConnection();

        try {
            connection.open(host, port);
            connection.connect(login, passcode);
            connection.send(destination, body, null, messageHeaders());
            connection.disconnect();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось отправить заявку на отчет в ActiveMQ через STOMP", e);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при отправке STOMP-сообщения в ActiveMQ", e);
        }
    }

    private String toJson(ReportRequestMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать сообщение заявки на отчет", e);
        }
    }

    private HashMap<String, String> messageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("amq-msg-type", "text");
        return headers;
    }
}
