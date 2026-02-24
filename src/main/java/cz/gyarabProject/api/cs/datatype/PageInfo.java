package cz.gyarabProject.api.cs.datatype;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PageInfo<T>(int pageNumber,
                          int pageCount,
                          int nextPage,
                          int pageSize,
                          @JsonAlias({"accounts, items"})
                          List<T> items) {
}


