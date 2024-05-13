package org.kosandron.services;

import org.kosandron.dto.OwnerMainDataDto;

import java.time.LocalDate;
import java.util.List;

public interface OwnerService {
    OwnerMainDataDto create(String name, LocalDate birtdayDate);
    void removeById(Long id);
    OwnerMainDataDto getById(Long id);
    List<OwnerMainDataDto> getAll();
    void addCatByOwnerId(Long ownerId, Long catId);
    void deleteCatByOwnerId(Long ownerId, Long catId);
    List<Long> getCatsByOwnerId(Long id);
}
