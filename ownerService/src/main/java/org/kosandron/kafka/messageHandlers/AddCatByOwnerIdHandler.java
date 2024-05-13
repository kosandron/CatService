package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.kosandron.kafka.requests.AddCatByOwnerIdRequest;
import org.kosandron.services.OwnerService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddCatByOwnerIdHandler implements MessageHandler {
    private final static String KEY = "addCat";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        AddCatByOwnerIdRequest request = mapper.readValue(value, AddCatByOwnerIdRequest.class);

        ownerService.addCatByOwnerId(request.ownerId(), request.catId());
        return "Success!";
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
