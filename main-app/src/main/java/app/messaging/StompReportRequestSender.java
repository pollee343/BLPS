package app.messaging;

import app.dto.messages.ReportRequestMessage;
import app.services.interfases.ReportRequestSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class StompReportRequestSender implements ReportRequestSender {

    private static final char FRAME_TERMINATOR = '\0';

    private final ObjectMapper objectMapper;

    @Value("${activemq.stomp.host:localhost}")
    private String host;

    @Value("${activemq.stomp.port:61613}")
    private int port;

    @Value("${activemq.stomp.login:admin}")
    private String login;

    @Value("${activemq.stomp.passcode:admin}")
    private String passcode;

    @Value("${activemq.report-requests.destination:/queue/legal-report.requests}")
    private String destination;

    @Override
    public void send(Long applicationId) {
        String body = toJson(new ReportRequestMessage(applicationId));

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);

            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            writeFrame(output, connectFrame());
            readConnectedFrame(input);

            writeFrame(output, sendFrame(body));
            writeFrame(output, "DISCONNECT\n\n" + FRAME_TERMINATOR);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось отправить заявку на отчет в ActiveMQ через STOMP", e);
        }
    }

    private String toJson(ReportRequestMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать сообщение заявки на отчет", e);
        }
    }

    private String connectFrame() {
        return "CONNECT\n"
                + "accept-version:1.2\n"
                + "host:" + host + "\n"
                + "login:" + login + "\n"
                + "passcode:" + passcode + "\n\n"
                + FRAME_TERMINATOR;
    }

    private String sendFrame(String body) {
        return "SEND\n"
                + "destination:" + destination + "\n"
                + "content-type:application/json\n"
                + "\n"
                + body
                + FRAME_TERMINATOR;
    }

    private void writeFrame(OutputStream output, String frame) throws IOException {
        output.write(frame.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private void readConnectedFrame(InputStream input) throws IOException {
        StringBuilder frame = new StringBuilder();
        int next;

        while ((next = input.read()) != -1) {
            if (next == FRAME_TERMINATOR) {
                break;
            }
            frame.append((char) next);
        }

        if (!frame.toString().startsWith("CONNECTED")) {
            throw new IOException("ActiveMQ STOMP CONNECT failed: " + frame);
        }
    }
}
