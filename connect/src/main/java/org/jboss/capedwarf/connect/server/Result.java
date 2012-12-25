package org.jboss.capedwarf.connect.server;

import java.io.InputStream;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Result {
    private int status;
    private InputStream stream;
    private long executionTime;
    private long contentLength = -1;

    Result() {
        executionTime = System.currentTimeMillis();
    }

    public void end() {
        executionTime = System.currentTimeMillis() - executionTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}
