package org.kosandron.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.kosandron.enums.Color;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatMainDataDto {
    private Long id;
    private String name;
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
    private String breed;
    private Color color;
    private Long ownerId;

    public CatMainDataDto(String value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JsonNode actualObj = mapper.readTree(value);
        id = actualObj.get("id").asLong();
        name = actualObj.get("name").asText();
        birthday = LocalDate.parse(actualObj.get("birthday").asText());
        breed = actualObj.get("breed").asText();
        color = Color.valueOf(actualObj.get("color").asText());
        ownerId = actualObj.get("ownerId").asLong();
    }
}
