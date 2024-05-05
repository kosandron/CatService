package kosandron.controllers;

import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;
import kosandron.data.InputOwnerData;

import java.security.Principal;
import java.util.List;

public interface OwnerController {
    OwnerMainDataDto getOwnerById(Long ownerId);
    List<OwnerMainDataDto> getAllOwners();
    Long createOwner(InputOwnerData ownerData);
    void deleteOwner(Long ownerId);
    List<CatMainDataDto> getCats(Long ownerId, Principal principal);
}
