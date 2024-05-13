package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.kafka.requests.CreateCatRequest;
import org.kosandron.kafka.requests.DeleteCatRequest;
import org.kosandron.services.CatService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteCatRequestHandler implements MessageHandler {
    private final static String KEY = "deleteCat";
    private final CatService catService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        DeleteCatRequest request = mapper.readValue(value, DeleteCatRequest.class);

        catService.removeById(request.id());
        return "Success!";
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
