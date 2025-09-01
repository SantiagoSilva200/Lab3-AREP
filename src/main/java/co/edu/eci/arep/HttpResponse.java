package co.edu.eci.arep;

public class HttpResponse {
    private int status;
    private String body;
    private String contentType;

    public HttpResponse() {
        this.status = 200;
        this.body = "";
        this.contentType = "text/html; charset=utf-8";
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void json(String json) {
        this.body = json;
        this.contentType = "application/json; charset=utf-8";
    }

    public void text(String text) {
        this.body = text;
        this.contentType = "text/plain; charset=utf-8";
    }

    public void html(String html) {
        this.body = html;
        this.contentType = "text/html; charset=utf-8";
    }
}