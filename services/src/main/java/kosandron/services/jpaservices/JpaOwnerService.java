package kosandron.services.jpaservices;

import kosandron.dao.CatDao;
import kosandron.dao.OwnerDao;
import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;
import kosandron.entities.Cat;
import kosandron.entities.Owner;
import kosandron.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import kosandron.services.OwnerService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaOwnerService implements OwnerService {
    private final OwnerDao ownerDao;
    private final CatDao catDao;

    @Override
    public Long create(String name, LocalDate birthdayDate) {
        return ownerDao.saveAndFlush(new Owner(name, birthdayDate)).getId();
    }

    @Override
    public void removeById(Long id) {
        for (Cat cat : ownerDao.findById(id).orElseThrow(() -> NotFoundException.ownerNotFound(id)).getCats()) {
            for (Long friendId : cat.getFriends().stream().map(Cat::getId).toList()) {
                Cat friend = catDao.findById(friendId).orElseThrow(() -> NotFoundException.catNotFound(id));
                cat.removeFriend(friend);
                catDao.save(cat);
                catDao.save(friend);
            }
        }
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
    public List<CatMainDataDto> getCatsListByOwnerId(Long id) {
        return ownerDao.findById(id).orElseThrow(() -> NotFoundException.ownerNotFound(id)).getCats().stream().map(CatMainDataDto::fromEntity).toList();
    }
}
