package processors;

import db.Database;
import feed.Feed;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.util.ProcessorUtil;
import property.annotations.PostMapping;
import property.annotations.Processor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import utils.ContentType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static utils.Paths.*;



@Processor("/feed")
public class FeedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FeedProcessor.class);
    private static final FeedProcessor instance = new FeedProcessor();

    private FeedProcessor() {

    }

    public static FeedProcessor getInstance() {
        return instance;
    }

    @PostMapping("/write")
    public void feedWrite(HttpRequest request, HttpResponse response) {
        logger.debug("FeedWrite Call");
        User writeUser = ProcessorUtil.getUserByCookieInSession(request);

        if (writeUser == null) {
            response.setStatus302Found(TEMPLATE_PATH + "/login" + DEFAULT_FILE);
            return;
        } else {
            Map<String, String> body = request.getBody();
            logger.debug(body.toString());
            String contents = body.get("contents");
            if (contents.equals("NONE")) {
                response.addAttribute("NO_CONTENTS", "내용을 입력해주세요!");
                response.setStatus200OK();
                response.setBody(TEMPLATE_PATH + "/feed" + DEFAULT_FILE);
                return;
            }
            String fileData = body.get("file");
            if (fileData.equals("NONE")) {
                response.addAttribute("NO_FILE", "파일을 업로드해주세요!");
                response.setStatus200OK();
                response.setBody(TEMPLATE_PATH + "/feed" + DEFAULT_FILE);
                return;
            }
            ContentType fileType = ContentType.valueOf(body.get("file_type").toUpperCase());

            String imageStoredpath = storeImage(writeUser, body.get("file"), body.get("file_type"));

            Feed feed = new Feed(writeUser.getName(), imageStoredpath, fileType, body.get("contents"));
            Database.addFeed(writeUser, feed);
        }
        response.setStatus200OK();
        response.setJsonBody("{\"redirectUrl\":\"/\"}");
    }

    private String storeImage(User user, String image, String imageType) {
        String hash = String.valueOf(user.hashCode());
        byte[] decodedImage = Base64.getDecoder().decode(image);

        String dirPath = FEED_IMAGE_DIR + hash;
        File imageDir = new File(dirPath);
        int imageCount = 0;
        //이미 해당 유저의 이미지 디렉터리가 존재하면 이미지 개수 셈
        if (imageDir.exists() && imageDir.isDirectory()) {
            String[] files = imageDir.list();
            for (String fileName : files) {
                File file = new File(imageDir + File.separator + fileName);
                if (file.isFile()) {
                    imageCount++;
                }
            }
        } else {
            imageDir.mkdirs();
        }

        String imageStoredPath = dirPath + "/" + imageCount + "." + imageType;
        try (FileOutputStream fileOutputStream = new FileOutputStream(imageStoredPath)) {
            fileOutputStream.write(decodedImage);
        } catch (IOException e) {
            logger.error("이미지를 저장하는데 실패했습니다");
        }

        return imageStoredPath;
    }
}
