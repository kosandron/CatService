package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.kafka.requests.GetOwnerByIdRequest;
import org.kosandron.services.OwnerService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetOwnerByIdRequestHandler implements MessageHandler {
    private final static String KEY = "getOwnerById";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        GetOwnerByIdRequest request = mapper.readValue(value, GetOwnerByIdRequest.class);

        OwnerMainDataDto dto = ownerService.getById(request.id());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(dto);
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}