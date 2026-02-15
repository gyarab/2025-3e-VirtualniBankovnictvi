package cz.gyarabProject.api.sporitelna.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ATM(int id,
                  Location location,
                  String name,
                  String address,
                  String type,
                  int distance,
                  String state,
                  String stateNote,
                  String city,
                  String postCode,
                  String region,
                  String country,
                  Service[] services,
                  String bankCode,
                  String accessType
                  ) {
    public record Location(double lat, double lng, String accuracy) {}
    public record Service(String type, String flag, String name, String desc) {}
    public record Opening(String weekday, Interval[] intervals) {}
    public record Interval(String from, String to) {}


    public record Flag(Type[] types, Type[] services) {}
    public record Type(String flag, String name, String desc) {}
}
