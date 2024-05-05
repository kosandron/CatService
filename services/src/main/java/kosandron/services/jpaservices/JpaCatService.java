package kosandron.services.jpaservices;

import kosandron.dao.CatDao;
import kosandron.dao.OwnerDao;
import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;
import kosandron.entities.Cat;
import kosandron.entities.Owner;
import kosandron.enums.Color;
import kosandron.exceptions.NotFoundException;
import kosandron.exceptions.SameCatException;
import kosandron.services.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaCatService implements CatService {
    private final CatDao catDao;
    private final OwnerDao ownerDao;

    @Override
    public Long create(String name, LocalDate birtdayDate, String breed, Color color, Long ownerId) {

        Owner owner = ownerDao.findById(ownerId).orElseThrow(() -> NotFoundException.ownerNotFound(ownerId));
        return catDao.saveAndFlush(new Cat(name, birtdayDate, breed, color, owner)).getId();
    }

    @Override
    public void removeById(Long id) {
        Cat cat = catDao.findById(id).orElseThrow(() -> NotFoundException.catNotFound(id));
        for (Cat friend : cat.getFriends()) {
            removeFriend(cat.getId(), friend.getId());
        }
        catDao.deleteById(id);
    }

    @Override
    public CatMainDataDto getById(Long id) {
        return CatMainDataDto.fromEntity(catDao.findById(id).orElseThrow(() -> NotFoundException.catNotFound(id)));
    }

    @Override
    public List<CatMainDataDto> getAll() {
        return catDao.findAll().stream().map(CatMainDataDto::fromEntity).toList();
    }

    @Override
    public void makeFriend(Long cat1, Long cat2) {
        if (cat1.equals(cat2)) {
            throw new SameCatException();
        }

        Cat firstCat = catDao.findById(cat1).orElseThrow(() -> NotFoundException.catNotFound(cat1));
        Cat secondCat = catDao.findById(cat2).orElseThrow(() -> NotFoundException.catNotFound(cat2));

        firstCat.addFriend(secondCat);
        catDao.save(firstCat);
        catDao.save(secondCat);
    }

    @Override
    public void removeFriend(Long cat1, Long cat2) {
        if (cat1.equals(cat2)) {
            throw new SameCatException();
        }

        Cat firstCat = catDao.findById(cat1).orElseThrow(() -> NotFoundException.catNotFound(cat1));
        Cat secondCat = catDao.findById(cat2).orElseThrow(() -> NotFoundException.catNotFound(cat2));

        firstCat.removeFriend(secondCat);
        catDao.save(firstCat);
        catDao.save(secondCat);
    }

    @Override
    public List<CatMainDataDto> getFriends(Long id) {
        return catDao.findById(id).orElseThrow(() -> NotFoundException.catNotFound(id)).getFriends().stream().map(CatMainDataDto::fromEntity).toList();
    }

    @Override
    public List<CatMainDataDto> getAllWithFilter(Optional<String> color, Optional<String> breed, Optional<String> name) {
        return getAll()
                .stream()
                .filter(cat -> color.isEmpty() ||  cat.getColor().equals(Color.valueOf(color.get())))
                .filter(cat -> breed.isEmpty() ||  cat.getBreed().equals(breed.get()))
                .filter(cat -> name.isEmpty() ||  cat.getName().equals(name.get()))
                .toList();
    }
}
