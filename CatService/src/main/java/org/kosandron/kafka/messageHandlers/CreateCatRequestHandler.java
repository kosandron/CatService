package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.kafka.requests.CreateCatRequest;
import org.kosandron.services.CatService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCatRequestHandler implements MessageHandler {
    private final static String KEY = "createCat";
    private final CatService catService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        CreateCatRequest request = mapper.readValue(value, CreateCatRequest.class);

        CatMainDataDto dto = catService.create(
                request.name(),
                request.birthDate(),
                request.breed(),
                request.color(),
                request.ownerId()
        );
        return mapper.writeValueAsString(dto);
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
