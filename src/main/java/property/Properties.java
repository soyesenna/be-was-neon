package property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HTTPMethods;

import java.util.HashMap;
import java.util.Map;

public class Properties {
    private static final Logger logger = LoggerFactory.getLogger(Properties.class);
    private static Map<Property, MappedService> propertyMapping = new HashMap<>();

    private static final Properties instance = new Properties();

    public static Properties getInstance() {
        return instance;
    }

    private void addProperty(Property property, MappedService method) {
        propertyMapping.put(property, method);
    }

    public MappedService getProcessingMethodByProperty(Property property) {
        MappedService mappedService = propertyMapping.get(property);
        if (mappedService == null) {
            //매핑된 url이 없을 경우 파일요청으로 간주
            // static file processor 응답
            mappedService = propertyMapping.get(Property.of(HTTPMethods.GET, "."));
        }
        return mappedService;
    }

}
