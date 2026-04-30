import jakarta.resource.spi.*;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JiraManagedConnection implements ManagedConnection{

    private final JiraManagedConnectionFactory mcf;
    private final List<ConnectionEventListener> listeners = new ArrayList<>();

    public JiraManagedConnection(JiraManagedConnectionFactory mcf) {
        this.mcf = mcf;
    }

    public Object getConnection(
            Subject subject,
            ConnectionRequestInfo cxRequestInfo
    ) {
        return new JiraConnection(this, mcf);
    }

    public void destroy() {}

    public void cleanup() {}

    public void associateConnection(Object connection) {}

    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    public XAResource getXAResource() {
        return null;
    }

    public LocalTransaction getLocalTransaction() {
        return null;
    }

    public ManagedConnectionMetaData getMetaData() {
        return null;
    }

    public void setLogWriter(PrintWriter out) {}
    public PrintWriter getLogWriter() { return null; }
}
