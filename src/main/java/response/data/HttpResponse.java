package response.data;

import exceptions.NoResponseBodyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.RequestReader;
import response.util.ResponseStatus;
import utils.ContentType;
import utils.Paths;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static utils.StringUtils.DEFAULT_URL;
import static utils.StringUtils.appendHttpEndLine;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private String header;
    private byte[] body;
    private boolean hasBody;

    public HttpResponse() {
        this.header = "";
        this.hasBody = false;
        this.body = new byte[]{};
    }

    public String getHeader() {
        return this.header;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean hasBody() {
        return this.hasBody;
    }

    public void setHeader(ResponseStatus status, ContentType type) throws NoResponseBodyException{
        StringBuilder sb = new StringBuilder();

        sb.append(appendHttpEndLine(status.getCode()));
        switch (status) {
            case OK -> {
                if (!this.hasBody) throw new NoResponseBodyException();
                sb.append(appendHttpEndLine("Content-Type: " + type.getType()));
                sb.append(appendHttpEndLine("Content-Length: " + this.body.length));
            }
            case REDIRECT -> {
                sb.append(appendHttpEndLine("Location: " + DEFAULT_URL));
            }
        }

        this.header = sb.toString();
    }

    public void setBody(String urlPath) {
        StringBuilder sb = new StringBuilder();
        logger.debug(urlPath);

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
            fileReader.lines()
                    .forEach(string -> {
                        sb.append(appendHttpEndLine(string));
                    });
        } catch (IOException e) {
            //파일이 아닐경우 받은 String을 그대로 설정한다
            this.body = urlPath.getBytes();
            this.hasBody = true;
            return;
        }

        this.body = sb.toString().getBytes();
        this.hasBody = true;
    }
}

