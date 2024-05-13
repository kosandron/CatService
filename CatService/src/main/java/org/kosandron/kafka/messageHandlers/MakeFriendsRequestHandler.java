package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.kafka.requests.MakeFriendsRequest;
import org.kosandron.services.CatService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MakeFriendsRequestHandler implements MessageHandler {
    private final static String KEY = "makeFriends";
    private final CatService catService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String handle(String value) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MakeFriendsRequest request = mapper.readValue(value, MakeFriendsRequest.class);

        catService.makeFriend(request.id1(), request.id2());
        return "Success!";
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
