package cz.gyarabProject.api.datatype;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class ObjectMappers {
    private final ObjectMapper mapper;
    private final ObjectMapper decimal;

    public ObjectMappers() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Give mapper the possibility to map values to java.time classes.
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Tell the mapper to map timestamps as ISO-8601 string intead of integer.
        decimal = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Give mapper the possibility to map values to java.time classes.
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Tell the mapper to map timestamps as ISO-8601 string intead of integer.
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS); // Make the mapper use BigDecimal instead of double.
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ObjectMapper getDecimalMapper() {
        return decimal;
    }
}
