package response.data;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.util.DynamicHTMLMapper;
import response.util.ResponseStatus;
import utils.ContentType;

import java.io.*;
import java.net.URLDecoder;
import java.util.Collection;

import static utils.StringUtils.DEFAULT_URL;
import static utils.StringUtils.appendHttpEndLine;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private static final String DELETE_COOKIE = "max-age=0";
    private String header;
    private byte[] body;
    private boolean hasBody;
    private String cookie;
    private boolean hasCookie;

    public HttpResponse() {
        this.header = "";
        this.hasBody = false;
        this.body = new byte[]{};
        this.cookie = "sid=";
        this.hasCookie = false;
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

    public boolean hasCookie() {
        return this.hasCookie;
    }

    public void setCookie(String cookieId) {
        this.cookie += cookieId;
        this.cookie += "; Path=/";
        this.hasCookie = true;
    }

    public void deleteCookie(String cookieId) {
        this.cookie += cookieId + ";";
        this.cookie += DELETE_COOKIE;
        this.hasCookie = true;
    }

    public void setHeader(ResponseStatus status, ContentType type){
        StringBuilder sb = new StringBuilder();

        sb.append(appendHttpEndLine(status.getCode()));
        switch (status) {
            case OK -> {
                sb.append(appendHttpEndLine("Content-Type: " + type.getType()));
                sb.append(appendHttpEndLine("Content-Length: " + this.body.length));
        }
            case REDIRECT -> {
                sb.append(appendHttpEndLine("Location: " + DEFAULT_URL));
            }
        }

        if (this.hasCookie) {
            sb.append(appendHttpEndLine("Set-Cookie: " + this.cookie));
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

    //동적으로 html생성하는 메서드
    public void setBodyInLogin(String urlPath, String userName) {
        StringBuilder sb = new StringBuilder();
        logger.debug(urlPath);

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
            String decodeName = URLDecoder.decode(userName, "UTF-8");
            fileReader.lines()
                    .forEach(string -> {
                        if (string.contains(DynamicHTMLMapper.DYNAMIC_LOGIN_CODE.getValue())) {
                            sb.append(appendHttpEndLine(DynamicHTMLMapper.WELCOME_PAGE_LOGIN.getValue()));
                        } else if (string.contains(DynamicHTMLMapper.ADD_USERNAME_CODE.getValue())) {
                            sb.append(appendHttpEndLine("<a>환영합니다! " + decodeName + "님 </a>"));
                        } else {
                            sb.append(appendHttpEndLine(string));
                        }
                    });
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        }

        this.body = sb.toString().getBytes();
        this.hasBody = true;
    }

    public void setLoginListBody(String urlPath){
        Collection<User> users = Database.findAll();

        StringBuilder listHTML = new StringBuilder();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
            fileReader.lines()
                    .forEach(string -> {
                        if (string.contains(DynamicHTMLMapper.ADD_ALL_USERS_CODE.getValue())) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("<tr>");
                            for (User user : users) {
                                String decodeName = "";
                                try {
                                     decodeName = URLDecoder.decode(user.getName(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                sb.append("<th>").append(user.getUserId()).append("</th>");
                                sb.append("<th>").append(user.getPassword()).append("</th>");
                                sb.append("<th>").append(decodeName).append("</th>");
                                sb.append("<th>").append(user.getEmail()).append("</th>");
                            }
                            sb.append("</tr>");
                            listHTML.append(appendHttpEndLine(sb.toString()));
                        } else listHTML.append(appendHttpEndLine(string));
                    });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        this.body = listHTML.toString().getBytes();
        this.hasBody = true;

    }
}


