package org.kosandron.controllers;

import org.kosandron.data.InputOwnerData;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.dto.OwnerMainDataDto;

import java.security.Principal;
import java.util.List;

public interface OwnerController {
    OwnerMainDataDto getOwnerById(Long ownerId) throws Exception;
    List<OwnerMainDataDto> getAllOwners() throws Exception;
    OwnerMainDataDto createOwner(InputOwnerData ownerData) throws Exception;
    void deleteOwner(Long ownerId) throws Exception;
    List<Long> getCats(Long ownerId, Principal principal) throws Exception;
}
