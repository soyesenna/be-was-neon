package response.data;

public class HttpResponse {

    private final String header;
    private final byte[] body;
    private final boolean hasBody;
    private final boolean isSuccess;

    public HttpResponse() {
        this.header = "";
        this.hasBody = false;
        this.body = new byte[]{};
        this.isSuccess = false;
    }

    public HttpResponse(String header) {
        this.header = header;
        this.hasBody = false;
        this.body = new byte[]{};
        this.isSuccess = true;
    }

    public HttpResponse(String header, byte[] body) {
        this.header = header;
        this.body = body;
        this.hasBody = true;
        this.isSuccess = true;
    }

    public String getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean hasBody() {
        return this.hasBody;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }
}
