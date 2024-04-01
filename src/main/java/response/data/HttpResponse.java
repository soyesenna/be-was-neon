package response.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.util.dynamic.DynamicHtmlResolver;
import response.util.HttpStatus;
import utils.ContentType;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static utils.StringUtils.CRLF;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private HttpStatus status;
    private ResponseHeader header;
    private byte[] body;
    private Map<String, Object> dynamicAttributes;

    public HttpResponse() {
        //default status -> OK
        this.status = HttpStatus.OK;
        this.header = new ResponseHeader();
        this.body = new byte[0];
        this.dynamicAttributes = new HashMap<>();
    }

    public void setStatus200OK() {
        this.status = HttpStatus.OK;
    }

    public void setStatus404NotFound() {
        this.status = HttpStatus.NOT_FOUND;
    }

    public void setStatus302Found(String redirectPath) {
        this.status = HttpStatus.REDIRECT;
        header.setRedirectPath(redirectPath);
    }

    public void setJsonBody(String key, String value) {
        String json = "{\"%s\":\"%s\"}".formatted(key, value);

        this.body = json.getBytes();
        this.header.setContentLength(this.body.length);
        this.header.setContentType(ContentType.JSON);
    }

    public void setBody(String path){
        this.header.setContentType(resolveContentType(path));

        try {
            //동적으로 응답해야하는지 확인
            if (this.header.isHtml() && !this.dynamicAttributes.isEmpty()) {
                setDynamicBody(path);
            } else setStaticBody(path);
        } catch (IOException e) {
            logger.error("Body를 만드는 도중 에러가 발생했습니다 : {}", e.getMessage());
        }

        this.header.setContentLength(this.body.length);
    }

    private void setStaticBody(String path) throws IOException{
        logger.debug("Make Static Body");
        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] allBytes = fis.readAllBytes();

            this.body = allBytes;
        }
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

    public void setSidCookie(String sid) {
        this.header.setSidCookie(sid);
    }

    public void setCookie(String cookieName, String value) {
        this.header.setCookie(cookieName, value);
    }

    public void deleteSidCookie(String sid) {
        this.header.deleteSidCookie(sid);
    }

    public void deleteCookie(String cookieName, String value) {
        this.header.deleteCookie(cookieName, value);
    }

    public String getHeader() {
        return this.status.getCode() + CRLF + header.toString();
    }

    public byte[] getBody() {
        return this.body;
    }
}
