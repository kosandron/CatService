package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.kafka.requests.GetCatsByOwnerIdRequest;
import org.kosandron.kafka.requests.GetOwnerByIdRequest;
import org.kosandron.services.OwnerService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetCatsByOwnerIdRequestHandler implements MessageHandler {
    private final static String KEY = "getCatsByOwnerId";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        GetCatsByOwnerIdRequest request = mapper.readValue(value, GetCatsByOwnerIdRequest.class);
        List<Long> cats = ownerService.getCatsByOwnerId(request.id());

        return mapper.writeValueAsString(cats);
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
