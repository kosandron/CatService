package org.kosandron.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosandron.data.InputOwnerData;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.dto.UserDto;
import org.kosandron.enums.UserRole;
import org.kosandron.exceptions.NotFoundException;
import org.kosandron.exceptions.OtherOwnerDataException;
import org.kosandron.kafka.OwnerKafkaProducer;
import org.kosandron.kafka.requests.*;
import org.kosandron.security.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerControllerImpl implements OwnerController {
    private final UserService userService;
    private final OwnerKafkaProducer ownerKafkaProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    @GetMapping("/{ownerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OwnerMainDataDto getOwnerById(@PathVariable("ownerId") Long ownerId) throws Exception {
        Object response = ownerKafkaProducer.kafkaRequestReply("getOwnerById", mapper.writeValueAsString(new GetOwnerByIdRequest(ownerId)));
        return mapper.convertValue(response, OwnerMainDataDto.class);
    }

    @Override
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<OwnerMainDataDto> getAllOwners() throws Exception {
        var response = ownerKafkaProducer.kafkaRequestReply("getAllOwners","").toString();
        List<OwnerMainDataDto> owners = mapper.readValue(response, new TypeReference<>() {});
        return owners;
    }

    @Override
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OwnerMainDataDto createOwner(@Valid @RequestBody InputOwnerData ownerData) throws Exception {
        Object response = ownerKafkaProducer.kafkaRequestReply("createOwner", mapper.writeValueAsString(new CreateOwnerRequest(ownerData.name(), ownerData.birthDate())));
        OwnerMainDataDto ownerMainDataDto = mapper.convertValue(response, OwnerMainDataDto.class);
     //   Long id = ownerService.create(ownerData.name(), ownerData.birthDate());
        userService.add(ownerData.login(), ownerData.password(), ownerMainDataDto.getId(), ownerData.roles());
       // return id;
        return ownerMainDataDto;
    }

    @Override
    @DeleteMapping("/delete/{ownerId}")
    public void deleteOwner(@PathVariable("ownerId") Long ownerId) throws Exception {
        userService.removeByOwnerId(ownerId);
        ownerKafkaProducer.kafkaRequestReply("deleteOwner", mapper.writeValueAsString(new DeleteOwnerRequest(ownerId)));
     //   ownerService.removeById(ownerId);
    }

    @Override
    @GetMapping("/{ownerId}/cats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<Long> getCats(@PathVariable("ownerId") Long ownerId, Principal principal) throws Exception {
        String name = principal.getName();
        UserDto userDto = userService.getUserByLogin(name);

        if (userDto == null) {
            throw NotFoundException.ownerNotFound(ownerId);
        }
        if (!userDto.getRoles().contains(UserRole.ADMIN) && !userDto.getCatOwnerId().equals(ownerId)) {
            throw new OtherOwnerDataException();
        }
     //   return ownerService.getCatsListByOwnerId(ownerId);
        var response = ownerKafkaProducer.kafkaRequestReply("getCatsByOwnerId", mapper.writeValueAsString(new GetCatsByOwnerIdRequest(ownerId))).toString();
        List<Long> catIds = mapper.readValue(response, new TypeReference<>() {});
        return catIds;
    }
}
