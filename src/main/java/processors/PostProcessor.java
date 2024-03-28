package processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import property.annotations.PostMapping;
import property.annotations.Processor;
import request.data.HttpRequest;
import response.data.HttpResponse;

@Processor("/article")
public class PostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PostProcessor.class);
    private static final PostProcessor instance = new PostProcessor();

    private PostProcessor() {

    }

    public static PostProcessor getInstance() {
        return instance;
    }

    @PostMapping("/write")
    public void articleWrite(HttpRequest request, HttpResponse response) {

    }
}
