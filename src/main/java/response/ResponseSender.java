package response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.data.HttpResponse;
import utils.StringUtils;

import java.io.*;

import static utils.StringUtils.CRLF;

public class ResponseSender {
    private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);

    private final HttpResponse response;
    private final DataOutputStream dos;

    public ResponseSender(HttpResponse response, DataOutputStream dos) {
        this.response = response;
        this.dos = dos;
    }

    public void doResponse() throws IOException{
        logger.debug("doResponse Called");
        logger.debug(response.getHeader());
        dos.writeBytes(response.getHeader());
        dos.writeBytes(CRLF);
        logger.debug("header response done");
        if (response.getBody().length != 0){
            logger.debug(String.valueOf(response.getBody().length));
            dos.write(response.getBody());
        }
        dos.flush();
    }
}

