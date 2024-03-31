package response.util.dynamic;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

import static utils.StringUtils.*;

public class DynamicHtmlResolver {
    private static final Logger logger = LoggerFactory.getLogger(DynamicHtmlResolver.class);

    private final BufferedReader fileReader;
    private final String DYNAMIC_IDENTITY = "[DYNAMIC]";
    private final String ORDER_INSERT_LIST = "INSERT-LIST";
    private final String ORDER_INSERT = "INSERT";
    private final String ORDER_INSERT_ERROR = "INSERT-ERROR";
    private final Map<String, Object> attributes;

    private Map<String, String > dataMapping = new HashMap<>();


    public DynamicHtmlResolver(BufferedReader fileReader, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.fileReader = fileReader;
    }

    public String getProcessedBody() {
        StringBuilder sb = new StringBuilder();

        fileReader.lines()
                .forEach(string -> {
                    if (string.contains(DYNAMIC_IDENTITY)) {
                        //동적으로 생성해야하는 부분을 만나면 인터셉트 후 처리
                        sb.append(appendCRLF(interceptAndChange(string)));
                    } else sb.append(appendCRLF(string));
                });

        return sb.toString();
    }

    //dynamic attributes 형식에 맞게 동적으로 생성함
    private String interceptAndChange(String line) {
        logger.debug("Dynamic intercept");
        //필요없는 부분 잘라냄
        line = line.replace(DYNAMIC_IDENTITY, "").replace("<!--", "").replace("-->", "").trim();
        logger.debug(line);
        //order와 data분리
        String[] tmp = line.split(":");
        String order = tmp[0];
        String data = tmp[1];

        //데이터 파싱
        parseData(data);

        //동적으로 html 생성
        String result = "";

        try {
            //동적 생성 order 확인
            //현재는 list와 string 두개로 구분됨
            if (order.contentEquals(ORDER_INSERT_LIST)) {
                logger.debug("INSERT LIST PROCESS");
                result = insertList();
            } else if (order.contentEquals(ORDER_INSERT)) {
                logger.debug("INSERT PROCESS");
                result = insert();
            } else if (order.contentEquals(ORDER_INSERT_ERROR)) {
                logger.debug("INSERT ERROR PROCESS");
                result = insertError();
            }
        } catch (NoSuchMethodException | InvocationTargetException |IllegalAccessException | UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    private void parseData(String data) {
        StringTokenizer st = new StringTokenizer(data, " ");

        while (st.hasMoreTokens()) {
            String now = st.nextToken();
            logger.debug(now);
            String[] tmp = now.split("=");
            dataMapping.put(tmp[0], tmp[1]);
        }
    }

    private String insertError() {
        Object roughAttribute = attributes.get(dataMapping.get("NAME"));
        if (roughAttribute == null) return "";

        StringBuilder html = new StringBuilder();
        if (roughAttribute.getClass().getName().contains("String")) {
            String errorMessage = (String) roughAttribute;
            html.append("<p class=\"error-message\">").append(errorMessage).append("</p>");
        }

        return html.toString();
    }


    private String insertList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object roughAttribute = attributes.get(dataMapping.get("NAME"));
        if (roughAttribute == null) return "";

        StringBuilder html = new StringBuilder();

        List<User> users = new ArrayList<>();

        //동적으로 생성해야하는 타입과 주어진 타입이 일치하는지 검사
        if (roughAttribute instanceof List) {
            List<Object> convetList = (List<Object>) roughAttribute;
            if (convetList.get(0).getClass().getName().contains(dataMapping.get("TYPE"))) {
                for (Object user : convetList) {
                    users.add((User) user);
                }
            } else throw new IllegalArgumentException("설정된 타입과 실제 타입이 일치하지 않습니다");
        } else throw new IllegalArgumentException("INSERT-LIST 명령은 List 타입만 가질 수 있습니다");

        //설정된 sequence에 따라 html에 추가
        List<String> insertSequence = new ArrayList<>();
        StringTokenizer seqToken = new StringTokenizer(dataMapping.get("SEQ"), ", ()");
        while (seqToken.hasMoreTokens()) {
            insertSequence.add(seqToken.nextToken());
        }

        for (User user : users) {
            html.append("<tr>");
            for (String seq : insertSequence) {
                html.append("<th>");
                Class<User> userClass = User.class;
                Method getMethod = userClass.getMethod("get" + seq);
                html.append(getMethod.invoke(user));
                html.append("</th>");
            }
            html.append("</tr>");
        }
        return html.toString();
    }

    private String insert() throws UnsupportedEncodingException {
        Object roughData = attributes.get(dataMapping.get("NAME"));
        if (roughData == null) return "";

        String insetString = "";

        //String attibutes 처리
        if (roughData.getClass().getName().contains(dataMapping.get("TYPE"))) {
            insetString = (String) roughData;
            insetString = URLDecoder.decode(insetString, "UTF-8");
        } else throw new IllegalArgumentException("설정된 타입과 실제 타입이 일치하지 않습니다");


        return insetString;
    }

}
