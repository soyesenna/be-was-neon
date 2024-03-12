package webserver;

import Data.ParsedHttpRequest;
import db.Database;
import enums.ContentType;
import enums.QueryParm;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HTTPParser;
import utils.Paths;

import javax.swing.*;
import java.io.*;
import java.util.Map;
import java.util.function.Consumer;

import static utils.StringUtils.*;
import static utils.StringUtils.appendHttpEndLine;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final Map<String, Consumer<Map<QueryParm, String>>> queryURL = Map.of(
            "CREATE", (query) ->
                Database.addUser(new User(query.get(QueryParm.USERID), query.get(QueryParm.PASSWORD), query.get(QueryParm.NAME), query.get(QueryParm.EMAIL)))
    );

    private final HTTPParser parser;
    private final ParsedHttpRequest parsingResult;
    private final DataOutputStream dos;

    public ResponseHandler(HTTPParser parser, ParsedHttpRequest parsingResult, DataOutputStream dos) {
        this.parser = parser;
        this.parsingResult = parsingResult;
        this.dos = dos;
    }

    public void responseGetMethod() throws IOException {
        if (parsingResult.hasQuery()) {
            //url query와 매핑된 consumer 실행
            queryURL.get(parsingResult.getURL().toUpperCase()).accept(parsingResult.getQuery());
            logger.debug("추가된 유저 = " + Database.findUserById(parsingResult.getQuery().get(QueryParm.USERID)));
            response302Header(DEFAULT_URL);
        }
        else {
            responseFile();
            logger.debug("Response 된 파일 = " + parsingResult.getURL());
        }

        logger.info("GET Response DONE");
    }


    private void responseFile() throws IOException{
        String urlPath = Paths.STATIC_RESOURCES + parsingResult.getURL();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
            byte[] body = parser.combineStreamOfByteArrays(
                    fileReader.lines()
                            .map((s) -> appendNewLine(s).getBytes()));

            response200Header(body.length);
            responseBody(body);
        }
    }

    private void response302Header(String redirectURL) {
        try {
            dos.writeBytes(appendHttpEndLine("HTTP/1.1 302 Found "));
            dos.writeBytes(appendHttpEndLine("Location: " + redirectURL));
            dos.writeBytes(END_OF_HTTP_LINE);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes(appendHttpEndLine("HTTP/1.1 200 OK "));
            dos.writeBytes(appendHttpEndLine("Content-Type: " + parsingResult.getContentType().getType()));
            dos.writeBytes(appendHttpEndLine("Content-Length: " + lengthOfBodyContent));
            dos.writeBytes(END_OF_HTTP_LINE);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
