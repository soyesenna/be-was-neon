package property;

import utils.HTTPMethods;

import java.util.Objects;

public class Property {
    private HTTPMethods httpMethod;
    private String mappingURL;
    private Property(HTTPMethods httpMethod, String mappingURL) {
        this.httpMethod = httpMethod;
        this.mappingURL = mappingURL;
    }

    public HTTPMethods getHttpMethod() {
        return httpMethod;
    }

    public String getMappingURL() {
        return mappingURL;
    }

    public static Property of(HTTPMethods methods, String mappingURL) {
        return new Property(methods, mappingURL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return httpMethod.equals(property.httpMethod) && mappingURL.contentEquals(property.mappingURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, mappingURL);
    }
}
