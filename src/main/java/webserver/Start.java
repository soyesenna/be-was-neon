package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import webserver.prepareing.AnnotationScanner;

public class Start {
    private static final Logger logger = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) throws Exception{
        AnnotationScanner.scan();

        logger.info("WebServer Start!!");
        WebServer webServer = new WebServer();
        webServer.start(args);

    }


}
