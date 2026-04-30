package interfaces;

public interface JiraConnectionInterface extends AutoCloseable {
    String createTask(String title, String description);
    void close();
}
