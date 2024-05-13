package org.kosandron.services;

import lombok.RequiredArgsConstructor;
import org.kosandron.dao.OwnerDao;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.entities.Owner;
import org.kosandron.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaOwnerService implements OwnerService {
    private final OwnerDao ownerDao;

    @Override
    public OwnerMainDataDto create(String name, LocalDate birthdayDate) {
        return OwnerMainDataDto.fromEntity(ownerDao.save(new Owner(name, birthdayDate)));
    }

    @Override
    public void removeById(Long id) {
        ownerDao.deleteById(id);
    }

    @Override
    public OwnerMainDataDto getById(Long id) {
        return OwnerMainDataDto.fromEntity(ownerDao.findById(id).orElseThrow(() -> NotFoundException.ownerNotFound(id)));
    }

    @Override
    public List<OwnerMainDataDto> getAll() {
        return ownerDao.findAll().stream().map(OwnerMainDataDto::fromEntity).toList();
    }

    @Override
    public void addCatByOwnerId(Long ownerId, Long catId) {
        Owner owner = ownerDao.findById(ownerId).orElseThrow(() -> NotFoundException.ownerNotFound(ownerId));
        owner.addCat(catId);
        ownerDao.save(owner);
    }

    @Override
    public void deleteCatByOwnerId(Long ownerId, Long catId) {
        Owner owner = ownerDao.findById(ownerId).orElseThrow(() -> NotFoundException.ownerNotFound(ownerId));
        owner.removeCat(catId);
        ownerDao.save(owner);
    }

    @Override
    public List<Long> getCatsByOwnerId(Long id) {
        Owner owner = ownerDao.findById(id).orElseThrow(() -> NotFoundException.ownerNotFound(id));
        return new ArrayList<>(owner.getCats());
    }
}
