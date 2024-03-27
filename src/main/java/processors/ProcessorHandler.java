package processors;

import property.MappedService;
import property.Properties;
import property.Property;
import request.data.HttpRequest;
import response.data.HttpResponse;
import utils.Paths;

import java.lang.reflect.InvocationTargetException;

public class ProcessorHandler {
    private final HttpRequest request;
    private final HttpResponse response;

    private static final Properties properties = Properties.getInstance();

    public ProcessorHandler(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void doProcess() throws InvocationTargetException, IllegalAccessException {
        //현재 요청에 대한 processor 가져옴
        MappedService nowProcessor = properties.getProcessingMethodByProperty(Property.of(request.getMethods(), request.getURL()));
        if (nowProcessor == null) {
            // file요청인 경우 static file processor 응답
            if (request.getURL().contains(".")) nowProcessor = properties.getFileProcessingProperty();
            else {
                // 파일 요청도 아니고 매핑된 url도 아닐경우 404 응답
                response.setStatus404NotFound();
                response.setBody(Paths.STATIC_RESOURCES + "/NotFound.html");
                return;
            }
        }
        //url에 따른 요청 실행
        serviceStart(nowProcessor, request, response);
    }

    private void serviceStart(MappedService nowProcessor, HttpRequest request, HttpResponse response) throws InvocationTargetException, IllegalAccessException {
        nowProcessor.method().invoke(nowProcessor.instance().invoke(null), request, response);
    }
}
