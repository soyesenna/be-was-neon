package processor;

import org.assertj.core.api.ByteArrayAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import processors.FeedProcessor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import utils.ContentType;
import utils.HTTPMethods;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

public class FeedProcessorTest {

    private FeedProcessor feedProcessor;

    @BeforeEach
    void setUp() {
        feedProcessor = FeedProcessor.getInstance();
    }

    @Test
    @DisplayName("피드 페이지 요청 처리")
    void testFeedPage() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/feed", ContentType.HTML);
        HttpResponse response = new HttpResponse();

        feedProcessor.articlePostPage(request, response);

        assertThat(response.getStatusCode()).contains("302");
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("피드 게시 요청 처리")
    void testPostFeed() {
        HttpRequest request = new HttpRequest(HTTPMethods.POST, "/feed/write", ContentType.JSON);
        request.setBody(Map.of("content", "새로운 피드 내용"));
        HttpResponse response = new HttpResponse();

        feedProcessor.feedWrite(request, response);

        assertThat(response.getStatusCode()).contains("200 OK");
        assertThat(response.getHeader()).contains("application/json");

        ByteArrayAssert baa = new ByteArrayAssert(response.getBody());
        baa.asString().contains("redirectUrl");
    }

    @Test
    @DisplayName("댓글 페이지 요청 처리")
    void testCommentPage() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/feed/comment", ContentType.HTML);
        request.addQuery("feed", "0");

        HttpResponse response = new HttpResponse();

        feedProcessor.commentPage(request, response);

        assertThat(response.getStatusCode()).contains("200 OK");
        assertThat(response.getBody()).isNotEmpty();
    }
}

