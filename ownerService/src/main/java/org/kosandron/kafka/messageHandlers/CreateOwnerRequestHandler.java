package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.kafka.requests.AddCatByOwnerIdRequest;
import org.kosandron.kafka.requests.CreateOwnerRequest;
import org.kosandron.services.OwnerService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOwnerRequestHandler implements MessageHandler {
    private final static String KEY = "createOwner";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        CreateOwnerRequest request = mapper.readValue(value, CreateOwnerRequest.class);

        OwnerMainDataDto dto = ownerService.create(request.name(), request.birthDay());
        return mapper.writeValueAsString(dto);
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}