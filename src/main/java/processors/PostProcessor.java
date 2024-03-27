package processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import property.annotations.Processor;

@Processor("/article")
public class PostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PostProcessor.class);
    private static final PostProcessor instance = new PostProcessor();

    private PostProcessor() {

    }

    public static PostProcessor getInstance() {
        return instance;
    }

}
