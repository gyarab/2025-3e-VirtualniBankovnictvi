package cz.gyarabProject.api.kb.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountInfo(String accountId,
                          String iban,
                          String currency,
                          @JsonProperty(value="nameI18N") String name,
                          @JsonProperty(value="productI18N") String product) {
}
