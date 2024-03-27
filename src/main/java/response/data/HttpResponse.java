package response.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.util.dynamic.DynamicHtmlResolver;
import response.util.ResponseStatus;
import utils.ContentType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static utils.StringUtils.CRLF;
import static utils.StringUtils.appendCRLF;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private ResponseStatus status;
    private ResponseHeader header;
    private byte[] body;
    private Map<String, Object> dynamicAttributes;

    public HttpResponse() {
        //default status -> OK
        this.status = ResponseStatus.OK;
        this.header = new ResponseHeader();
        this.body = new byte[0];
        this.dynamicAttributes = new HashMap<>();
    }

    public void setStatus200OK() {
        this.status = ResponseStatus.OK;
    }

    public void setStatus404NotFound() {
        this.status = ResponseStatus.NOT_FOUND;
    }

    public void setStatus302Found(String redirectPath) {
        this.status = ResponseStatus.REDIRECT;
        header.setRedirectPath(redirectPath);
    }

    public void setBody(String path){
        try {
            //동적으로 응답해야하는지 확인
            if (this.dynamicAttributes.isEmpty()) setStaticBody(path);
            else setDynamicBody(path);
        } catch (IOException e) {
            logger.error("Body를 만드는 도중 에러가 발생했습니다 : {}", e.getMessage());
        }

        this.header.setContentType(resolveContentType(path));
        this.header.setContentLength(this.body.length);
    }

    private void setStaticBody(String path) throws IOException{
        logger.debug("Make Static Body");
        StringBuilder sb = new StringBuilder();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            fileReader.lines()
                    .forEach(string -> {
                        sb.append(string).append(CRLF);
                    });
        }

        this.body = sb.toString().getBytes();
    }

    private void setDynamicBody(String path) throws IOException{
        logger.debug("Make Dynamic Body");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {

            DynamicHtmlResolver resolver = new DynamicHtmlResolver(fileReader, dynamicAttributes);

            this.body = resolver.getProcessedBody().getBytes();
        }
    }

    private ContentType resolveContentType(String path) {
        String[] tmp = path.split("/");
        String fileName = tmp[tmp.length - 1];

        return ContentType.valueOf(fileName.split("\\.")[1].toUpperCase());
    }

    public void addAttribute(String key, Object value) {
        this.dynamicAttributes.put(key.toUpperCase(), value);
    }

    public void setCookie(String sid) {
        this.header.setCookie(sid);
    }

    public void deleteCookie(String sid) {
        this.header.deleteCookie(sid);
    }

    public String getHeader() {
        return this.status.getCode() + CRLF + header.toString();
    }

    public byte[] getBody() {
        return this.body;
    }
}
