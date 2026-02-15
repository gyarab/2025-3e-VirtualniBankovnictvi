package cz.gyarabProject.api.sporitelna.datatype;

public record PageInfo<T>(int pageNumber,
                          int pageCount,
                          int nextPage,
                          int pageSize,
                          T[] items) {
}


