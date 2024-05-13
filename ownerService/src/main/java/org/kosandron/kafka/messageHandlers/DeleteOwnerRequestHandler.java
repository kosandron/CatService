package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.kafka.requests.CreateOwnerRequest;
import org.kosandron.kafka.requests.DeleteOwnerRequest;
import org.kosandron.kafka.responses.EmptyResponse;
import org.kosandron.services.OwnerService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteOwnerRequestHandler implements MessageHandler {
    private final static String KEY = "deleteOwner";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {

        DeleteOwnerRequest request = mapper.readValue(value, DeleteOwnerRequest.class);

        ownerService.removeById(request.id());
        return "Success!";
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}