package org.onpu.websimlpe.common;

/**
 * Created with IntelliJ IDEA.
 * User: andrey
 * Date: 9/22/12
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public enum HttpStatus {
    HTTP_OK(200, "All ok."),
    HTTP_NOT_FOUND(404, "Path not found."),
    HTTP_APPLICATION_ERROR(500, "Application error.");

    private int status;
    private String message;

    private HttpStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
