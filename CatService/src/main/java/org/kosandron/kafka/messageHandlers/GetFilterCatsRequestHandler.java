package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.kafka.requests.GetFilterCatsRequest;
import org.kosandron.services.CatService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetFilterCatsRequestHandler implements MessageHandler {
    private final static String KEY = "getFilterCats";
    private final CatService catService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        GetFilterCatsRequest request = mapper.readValue(value, GetFilterCatsRequest.class);

        List<CatMainDataDto> cats = catService.getAllWithFilter(request.color(), request.breed(), request.name());
        return mapper.writeValueAsString(cats);
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
