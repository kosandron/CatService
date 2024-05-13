package org.kosandron.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class OwnerMainDataDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("birthday")
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;

    public OwnerMainDataDto(Long id, String name, String birthday) {
        this.id = id;
        this.name = name;
        this.birthday = LocalDate.parse(birthday);
    }

    public OwnerMainDataDto(String value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JsonNode actualObj = mapper.readTree(value);
        id = actualObj.get("id").asLong();
        name = actualObj.get("name").asText();
        birthday = LocalDate.parse(actualObj.get("birthday").asText());
    }
}
