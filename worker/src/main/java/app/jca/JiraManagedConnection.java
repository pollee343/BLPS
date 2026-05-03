package app.jca;

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

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) {
        return new JiraConnection(this, mcf);
    }

    @Override
    public void destroy() {}

    @Override
    public void cleanup() {}

    @Override
    public void associateConnection(Object connection) {}

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource() {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {}
    @Override
    public PrintWriter getLogWriter() { return null; }
}
