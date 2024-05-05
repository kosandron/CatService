package kosandron.controllers;

import jakarta.validation.Valid;
import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;
import kosandron.data.InputOwnerData;
import kosandron.dto.UserDto;
import kosandron.enums.UserRole;
import kosandron.exceptions.NotFoundException;
import kosandron.exceptions.OtherOwnerDataException;
import kosandron.services.OwnerService;
import kosandron.services.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerControllerImpl implements OwnerController {
    private final OwnerService ownerService;
    private final UserService userService;

    @Override
    @GetMapping("/{ownerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OwnerMainDataDto getOwnerById(@PathVariable("ownerId") Long ownerId) {
        return ownerService.getById(ownerId);
    }

    @Override
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<OwnerMainDataDto> getAllOwners() {
        return ownerService.getAll();
    }

    @Override
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Long createOwner(@Valid @RequestBody InputOwnerData ownerData) {
        Long id = ownerService.create(ownerData.name(), ownerData.birthDate());
        userService.add(ownerData.login(), ownerData.password(), id, ownerData.roles());
        return id;
    }

    @Override
    @DeleteMapping("/delete/{ownerId}")
    public void deleteOwner(@PathVariable("ownerId") Long ownerId) {
        userService.removeByOwnerId(ownerId);
        ownerService.removeById(ownerId);
    }

    @Override
    @GetMapping("/{ownerId}/cats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CatMainDataDto> getCats(@PathVariable("ownerId") Long ownerId, Principal principal) {
        String name = principal.getName();
        UserDto userDto = userService.getUserByLogin(name);

        if (userDto == null) {
            throw NotFoundException.ownerNotFound(ownerId);
        }
        if (!userDto.getRoles().contains(UserRole.ADMIN) && !userDto.getCatOwnerId().equals(ownerId)) {
            throw new OtherOwnerDataException();
        }
        return ownerService.getCatsListByOwnerId(ownerId);
    }
}
