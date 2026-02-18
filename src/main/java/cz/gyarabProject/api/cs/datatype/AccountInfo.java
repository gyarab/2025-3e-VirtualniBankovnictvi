package cz.gyarabProject.api.cs.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountInfo (
        String id,
        Identification identification,
        String currency,
        Servicer servicer,
        String nameI18N,
        String productI18N,
        List<String> ownersNames,
        RelationShip relationShip,
        SuitableScope suitableScope
) {
    public record Identification (
            String iban, String other
    ) {}
    public record Servicer (
            String bankCode, String countryCode, String bic
    ) {}
    public record RelationShip (
            boolean isOwner
    ) {}
    public record SuitableScope () {}
}
