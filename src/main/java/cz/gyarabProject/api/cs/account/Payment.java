package cz.gyarabProject.api.cs.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.AccountInfo;
import cz.gyarabProject.api.cs.datatype.Balance;
import cz.gyarabProject.api.cs.datatype.PageInfo;
import cz.gyarabProject.api.cs.datatype.Transaction;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class Payment extends Sender {
    private final Property props;
    private final ObjectMapper mapper;
    private static final Property.Bank bank = Property.Bank.CS;

    public Payment(Property props, HttpClient client, ObjectMappers mappers) {
        super(props, client);
        this.props = props;
        this.mapper = mappers.getMapper();
    }

    public PageInfo<List<AccountInfo>> getAccountInfo(int size, int page, String sort, String order)
            throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of(
                "size", size,
                "page", page,
                "sort", sort,
                "order", order
        ));
        HttpResponse<String> response = send(props.getUri(
                bank, Property.Environment.SANDBOX, "account", query, "my", "accounts"
        ));

        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public List<Balance> getBalance(String id) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of("id", id));
        HttpResponse<String> response = send(
                props.getUri(bank, Property.Environment.SANDBOX, "account", query,
                        "my", "accounts", id, "balance")
        );

        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public PageInfo<Transaction> getTransactions(
            String id, LocalDate from, LocalDate to, int size, int page, String sort, String order
    ) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of(
                "id", id,
                "fromDate", from,
                "toDate", to,
                "size", size,
                "page", page,
                "sort", sort,
                "order", order
        ));
        HttpResponse<String> response = send(props.getUri(
                bank, Property.Environment.SANDBOX, "account", query,
                "my", "accounts", id, "transactions"
        ));

        return mapper.readValue(response.body(), new TypeReference<>() {});
    }
}
