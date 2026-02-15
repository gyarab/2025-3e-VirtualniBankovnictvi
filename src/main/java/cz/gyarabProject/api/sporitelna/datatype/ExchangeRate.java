package cz.gyarabProject.api.sporitelna.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRate(String country,
                           String name,
                           String shortName,
                           int amount,
                           String validFrom,
                           double valBuy,
                           double valSell,
                           double valMid,
                           double currBuy,
                           double currSell,
                           double currMid,
                           double move,
                           double cnbMid,
                           int version) {
}
