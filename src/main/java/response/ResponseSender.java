package response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.data.HttpResponse;
import utils.StringUtils;

import java.io.*;
import java.util.Arrays;

public class ResponseSender {
    private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);

    private final HttpResponse response;
    private final DataOutputStream dos;

    public ResponseSender(HttpResponse response, DataOutputStream dos) {
        this.response = response;
        this.dos = dos;
    }

    public void doResponse() throws IOException{
        dos.write(response.getHeader().getBytes());
        dos.writeBytes(StringUtils.END_OF_HTTP_LINE);
        if (response.hasBody()) dos.write(response.getBody());
        dos.flush();

        logger.debug(response.getHeader());
        logger.info("Send Response");
    }
}

