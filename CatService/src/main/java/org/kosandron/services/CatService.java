package org.kosandron.services;

import org.kosandron.dto.CatMainDataDto;
import org.kosandron.enums.Color;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CatService {
    CatMainDataDto create(String name, LocalDate birthdayDate, String breed, Color color, Long ownerId);
    void removeById(Long id);
    CatMainDataDto getById(Long id);
    List<CatMainDataDto> getAll();

    void makeFriend(Long ourId, Long friendId);
    void removeFriend(Long ourId, Long friendId);
    List<CatMainDataDto> getFriends(Long id);
    List<CatMainDataDto> getAllWithFilter(String color, String breed, String name);
    List<CatMainDataDto> getCatsListByOwnerId(Long id);
}
