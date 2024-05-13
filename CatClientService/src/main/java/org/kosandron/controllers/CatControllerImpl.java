package org.kosandron.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosandron.data.InputCatData;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.dto.UserDto;
import org.kosandron.enums.Color;
import org.kosandron.enums.UserRole;
import org.kosandron.exceptions.NotFoundException;
import org.kosandron.exceptions.OtherOwnerDataException;
import org.kosandron.kafka.CatKafkaProducer;
import org.kosandron.kafka.OwnerKafkaProducer;
import org.kosandron.kafka.requests.*;
import org.kosandron.security.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cat")
@RequiredArgsConstructor
public class CatControllerImpl implements CatController {
    private final UserService userService;
    private final CatKafkaProducer catKafkaProducer;
    private final OwnerKafkaProducer ownerKafkaProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public CatMainDataDto create(@Valid @RequestBody InputCatData inputCatData, Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        if (!user.getRoles().contains(UserRole.ADMIN) && !user.getCatOwnerId().equals(inputCatData.ownerId())) {
            throw new OtherOwnerDataException();
        }

        Object response = catKafkaProducer.kafkaRequestReply("createCat", mapper.writeValueAsString(new CreateCatRequest(
                inputCatData.name(),
                inputCatData.birthDate(),
                inputCatData.breed(),
                Color.valueOf(inputCatData.color()),
                inputCatData.ownerId()
        )));
        CatMainDataDto cat = mapper.convertValue(response, CatMainDataDto.class);
        // TODO: получить Dto
        ownerKafkaProducer.kafkaRequestReply("addCat", mapper.writeValueAsString(new AddCatByOwnerIdRequest(inputCatData.ownerId(), cat.getId())));
        return cat;
      //  return catService.create(inputCatData.name(), inputCatData.birthDate(), inputCatData.breed(), Color.valueOf(inputCatData.color()), inputCatData.ownerId());
    }

    @Override
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void removeById(@PathVariable("id") Long id, Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        if (!user.getRoles().contains(UserRole.ADMIN) && !user.getCatOwnerId().equals(getById(id, principal).getOwnerId())) {
            throw new OtherOwnerDataException();
        }
        Long ownerId = getById(id, principal).getOwnerId();
        ownerKafkaProducer.kafkaRequestReply("deleteCat", mapper.writeValueAsString(new DeleteCatByOwnerIdRequest(ownerId, id)));
        catKafkaProducer.kafkaRequestReply("deleteCat", mapper.writeValueAsString(new DeleteCatRequest(id)));
       // catService.removeById(id);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public CatMainDataDto getById(@PathVariable("id") Long id, Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        Object response = catKafkaProducer.kafkaRequestReply("getCatById", mapper.writeValueAsString(new GetCatByIdRequest(id)));
        CatMainDataDto data = mapper.convertValue(response, CatMainDataDto.class);
        if (!user.getRoles().contains(UserRole.ADMIN) && !user.getCatOwnerId().equals(data.getOwnerId())) {
            throw new OtherOwnerDataException();
        }
        return data;
    }

    @Override
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getAll(Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        var response = catKafkaProducer.kafkaRequestReply("getAllCats","").toString();
        List<CatMainDataDto> cats = mapper.readValue(response, new TypeReference<>() {});
        return cats.stream().filter(cat -> cat.getOwnerId().equals(user.getCatOwnerId())  || user.getRoles().contains(UserRole.ADMIN)).toList();
    }

    @Override
    @PutMapping("/makeFriends")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void makeFriend(@RequestParam(name = "cat1") Long firstCatId, @RequestParam(name = "cat2") Long secondCatId, Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        CatMainDataDto cat1 = getById(firstCatId, principal);
        CatMainDataDto cat2 = getById(secondCatId, principal);
        if (!user.getRoles().contains(UserRole.ADMIN) && !(user.getCatOwnerId().equals(cat1.getOwnerId()) || user.getCatOwnerId().equals(cat2.getOwnerId()))) {
            throw new OtherOwnerDataException();
        }
        catKafkaProducer.kafkaRequestReply("makeFriends", mapper.writeValueAsString(new MakeFriendsRequest(firstCatId, secondCatId)));
    }

    @Override
    @PutMapping("/removeFriends")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void removeFriend(@RequestParam(name = "cat1") Long firstCatId, @RequestParam(name = "cat2") Long secondCatId, Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        CatMainDataDto cat1 = getById(firstCatId, principal);
        CatMainDataDto cat2 = getById(secondCatId, principal);
        if (!user.getRoles().contains(UserRole.ADMIN) && !(user.getCatOwnerId().equals(cat1.getOwnerId()) || user.getCatOwnerId().equals(cat2.getOwnerId()))) {
            throw new OtherOwnerDataException();
        }
        catKafkaProducer.kafkaRequestReply("removeFriends", mapper.writeValueAsString(new DeleteFriendRequest(firstCatId, secondCatId)));
    }

    @Override
    @GetMapping("/{id}/friends")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getFriends(@PathVariable("id") Long id, Principal principal) throws Exception {
        UserDto user = getOwnerDto(principal);
        var response = catKafkaProducer.kafkaRequestReply("getFriends",mapper.writeValueAsString(new GetFriendsRequest(id))).toString();
        List<CatMainDataDto> cats = mapper.readValue(response, new TypeReference<>() {});
        return cats.stream().filter(cat -> cat.getOwnerId().equals(user.getCatOwnerId()) || user.getRoles().contains(UserRole.ADMIN)).toList();
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getFilterCats(@RequestParam(name = "color") Optional<String> color, @RequestParam(name = "breed") Optional<String> breed, @RequestParam(name = "name") Optional<String> name, Principal principal) throws Exception {
        if (color.isPresent() && !Color.contains(color.get())) {
            return new ArrayList<>();
        }
        UserDto user = getOwnerDto(principal);
        String colorString = color.isEmpty() ? null : color.get();
        String breedString = breed.isEmpty() ? null : breed.get();
        String nameString = name.isEmpty() ? null : name.get();

        var response = catKafkaProducer
                .kafkaRequestReply("getFilterCats",mapper.writeValueAsString(new GetFilterCatsRequest(colorString, breedString, nameString))).toString();
        List<CatMainDataDto> cats = mapper.readValue(response, new TypeReference<>() {});
        return cats.stream().filter(cat -> cat.getOwnerId().equals(user.getCatOwnerId()) || user.getRoles().contains(UserRole.ADMIN)).toList();
    }

    private UserDto getOwnerDto(Principal principal) {
        String login = principal.getName();
        UserDto userDto = userService.getUserByLogin(login);

        if (userDto == null) {
            throw NotFoundException.userNotFound(login);
        }

        return userDto;
    }
}
