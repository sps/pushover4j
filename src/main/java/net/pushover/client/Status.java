package net.pushover.client;

/**
 * API response object
 */
public class Status {

    private final Integer status;

    private String request;

    public Status(Integer status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getRequestId() {
        return request;
    }

    public void setRequestId(String requestId) {
        this.request = requestId;
    }

    @Override
    public String toString() {
        return String.format("status: %s, requestId: %s", status != null ? String.valueOf(status) : "??", request);
    }
}
