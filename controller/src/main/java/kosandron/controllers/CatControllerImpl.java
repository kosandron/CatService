package kosandron.controllers;

import jakarta.validation.Valid;
import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;
import kosandron.dto.UserDto;
import kosandron.enums.Color;
import kosandron.data.InputCatData;
import kosandron.enums.UserRole;
import kosandron.exceptions.NotFoundException;
import kosandron.exceptions.OtherOwnerDataException;
import kosandron.services.CatService;
import kosandron.services.security.UserService;
import kosandron.validation.ValueOfEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.EnumUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cat")
@RequiredArgsConstructor
public class CatControllerImpl implements CatController {
    private final CatService catService;
    private final UserService userService;

    @Override
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public Long create(@Valid @RequestBody InputCatData inputCatData, Principal principal) {
        UserDto user = getOwnerDto(principal);
        if (!user.getRoles().contains(UserRole.ADMIN) && !user.getCatOwnerId().equals(inputCatData.ownerId())) {
            throw new OtherOwnerDataException();
        }

        return catService.create(inputCatData.name(), inputCatData.birthDate(), inputCatData.breed(), Color.valueOf(inputCatData.color()), inputCatData.ownerId());
    }

    @Override
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void removeById(@PathVariable("id") Long id, Principal principal) {
        UserDto user = getOwnerDto(principal);
        if (!user.getRoles().contains(UserRole.ADMIN) && !user.getCatOwnerId().equals(getById(id, principal).getOwnerId())) {
            throw new OtherOwnerDataException();
        }

        catService.removeById(id);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public CatMainDataDto getById(@PathVariable("id") Long id,  Principal principal) {
        UserDto user = getOwnerDto(principal);
        CatMainDataDto data = catService.getById(id);
        if (!user.getRoles().contains(UserRole.ADMIN) && !user.getCatOwnerId().equals(data.getOwnerId())) {
            throw new OtherOwnerDataException();
        }
        return data;
    }

    @Override
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getAll(Principal principal) {
        UserDto user = getOwnerDto(principal);
        return catService.getAll().stream().filter(cat -> cat.getOwnerId().equals(user.getCatOwnerId())  || user.getRoles().contains(UserRole.ADMIN)).toList();
    }

    @Override
    @PutMapping("/makeFriends")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void makeFriend(@RequestParam(name = "cat1") Long firstCatId, @RequestParam(name = "cat2") Long secondCatId, Principal principal) {
        UserDto user = getOwnerDto(principal);
        CatMainDataDto cat1 = catService.getById(firstCatId);
        CatMainDataDto cat2 = catService.getById(secondCatId);
        if (!user.getRoles().contains(UserRole.ADMIN) && !(user.getCatOwnerId().equals(cat1.getOwnerId()) || user.getCatOwnerId().equals(cat2.getOwnerId()))) {
            throw new OtherOwnerDataException();
        }
        catService.makeFriend(firstCatId, secondCatId);
    }

    @Override
    @PutMapping("/removeFriends")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void removeFriend(@RequestParam(name = "cat1") Long firstCatId, @RequestParam(name = "cat2") Long secondCatId, Principal principal) {
        UserDto user = getOwnerDto(principal);
        CatMainDataDto cat1 = catService.getById(firstCatId);
        CatMainDataDto cat2 = catService.getById(secondCatId);
        if (!user.getRoles().contains(UserRole.ADMIN) && !(user.getCatOwnerId().equals(cat1.getOwnerId()) || user.getCatOwnerId().equals(cat2.getOwnerId()))) {
            throw new OtherOwnerDataException();
        }
        catService.removeFriend(firstCatId, secondCatId);
    }

    @Override
    @GetMapping("/{id}/friends")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getFriends(@PathVariable("id") Long id, Principal principal) {
        UserDto user = getOwnerDto(principal);
        return catService.getFriends(id).stream().filter(cat -> cat.getOwnerId().equals(user.getCatOwnerId()) || user.getRoles().contains(UserRole.ADMIN)).toList();
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getFilterCats(@RequestParam(name = "color") Optional<String> color, @RequestParam(name = "breed") Optional<String> breed, @RequestParam(name = "name") Optional<String> name, Principal principal) {
        if (color.isPresent() && !Color.contains(color.get())) {
            return new ArrayList<>();
        }
        UserDto user = getOwnerDto(principal);
        return catService.getAllWithFilter(color, breed, name).stream().filter(cat -> cat.getOwnerId().equals(user.getCatOwnerId()) || user.getRoles().contains(UserRole.ADMIN)).toList();
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
