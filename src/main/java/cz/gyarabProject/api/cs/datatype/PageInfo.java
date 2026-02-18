package cz.gyarabProject.api.cs.datatype;

import java.util.List;

public record PageInfo<T>(int pageNumber,
                          int pageCount,
                          int nextPage,
                          int pageSize,
                          List<T> items) {
}


