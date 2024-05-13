package org.kosandron.services;

import lombok.RequiredArgsConstructor;
import org.kosandron.dao.CatDao;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.entities.Cat;
import org.kosandron.enums.Color;
import org.kosandron.exceptions.NotFoundException;
import org.kosandron.exceptions.SameCatException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaCatService implements CatService {
    private final CatDao catDao;

    @Override
    public CatMainDataDto create(String name, LocalDate birtdayDate, String breed, Color color, Long ownerId) {
        return CatMainDataDto.fromEntity(catDao.saveAndFlush(new Cat(name, birtdayDate, breed, color, ownerId)));
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
    public List<CatMainDataDto> getAllWithFilter(String color, String breed, String name) {
        return getAll()
                .stream()
                .filter(cat -> color == null ||  cat.getColor().equals(Color.valueOf(color)))
                .filter(cat -> breed == null ||  cat.getBreed().equals(breed))
                .filter(cat -> name == null ||  cat.getName().equals(name))
                .toList();
    }

    @Override
    public List<CatMainDataDto> getCatsListByOwnerId(Long id) {
        return getAll().stream().filter(cat -> cat.getOwnerId().equals(id)).toList();
    }
}

