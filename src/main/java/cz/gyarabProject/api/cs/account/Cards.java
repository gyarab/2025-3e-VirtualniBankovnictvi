package cz.gyarabProject.api.cs.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.Token;
import cz.gyarabProject.api.cs.datatype.card.Card;
import cz.gyarabProject.api.cs.datatype.PageInfo;
import cz.gyarabProject.api.cs.datatype.card.CardTransaction;
import cz.gyarabProject.api.cs.datatype.card.Reservation;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;

@Component
public class Cards extends Sender {
    private final Property props;
    private final ObjectMapper mapper;

    public Cards(Property props, HttpClient client, ObjectMappers mappers) {
        this.props = props;
        this.mapper = mappers.getMapper();
    }

     public PageInfo<Card> getCards(String[] accountIds, String[] cardTypes, int size, int page, Token token) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of(
                "accountIds", accountIds,
                "cardType", cardTypes,
                "size", size,
                "page", page
        ));
        HttpResponse<String> response = send(props.getUri(
                bank(), Property.Environment.SANDBOX, "account", query, "my", "cards"), token
        );
        return mapper.readValue(response.body(), new TypeReference<>() {});
     }

     public Card getCard(String id, Token token) throws IOException, InterruptedException {
        HttpResponse<String> response = send(props.getUriWithEnding(
                bank(), Property.Environment.SANDBOX, "account", "my", "cards", id), token
        );
        return mapper.readValue(response.body(), new TypeReference<>() {});
     }

     public PageInfo<Reservation> getResevations(String id, String state, int size, int page, Token token) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of(
                "reservationState", state,
                "size", size,
                "page", page
        ));
        HttpResponse<String> response = send(props.getUri(
                bank(), Property.Environment.SANDBOX, "account", query, "my", "cards", id, "reservations"), token
        );
        return mapper.readValue(response.body(), new TypeReference<>() {});
     }

     public PageInfo<CardTransaction> getTransactions(String id, LocalDate from, LocalDate to, int size, int page, Token token) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of(
                "fromBookingDay", from,
                "toBookingDay", to,
                "size", size,
                "page", page
        ));
        HttpResponse<String> response = send(props.getUri(
                bank(), Property.Environment.SANDBOX, "account", query, "my", "card", id, "transactions"), token
        );
        return mapper.readValue(response.body(), new TypeReference<>() {});
     }
}
