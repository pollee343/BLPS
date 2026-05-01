package app.interfaces;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;

public interface ConnectionInterface extends Connection {
    String createTask(String title, String description) throws ResourceException;
}
