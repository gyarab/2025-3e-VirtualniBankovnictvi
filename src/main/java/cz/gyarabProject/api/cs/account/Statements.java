package cz.gyarabProject.api.cs.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.PageInfo;
import cz.gyarabProject.api.cs.datatype.statement.Statement;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;

@Component
public class Statements extends Sender {
    private final Property props;
    private final ObjectMapper mapper;

    public Statements(Property props, ObjectMappers mappers) {
        this.props = props;
        this.mapper = mappers.getMapper();
    }

    public PageInfo<Statement> getStatements(
            String id, LocalDate from, LocalDate to, String format, int size, int page
    ) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of(
                "fromDate", from,
                "toDate", to,
                "format", format,
                "size", size,
                "page", page
        ));
        HttpResponse<String> response = send(
                props.getUri(bank(), Property.Environment.SANDBOX, "account", query, id, "statements")
        );
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public String getDownloads(String id, String accountStatementId, String format) throws IOException, InterruptedException {
        String query = props.buildQuery(Map.of("format", format));
        HttpResponse<String> response = send(props.getUri(
                bank(), Property.Environment.SANDBOX, "account", query,
                id, "statements", accountStatementId, "download"
        ));
        return response.body();
    }
}
