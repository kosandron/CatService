package kosandron.controllers;

import kosandron.dto.CatMainDataDto;
import kosandron.data.InputCatData;

import java.security.Principal;
import java.util.List;

public interface CatController {
    Long create(InputCatData inputCatData, Principal principal);
    void removeById(Long id, Principal principal);
    CatMainDataDto getById(Long id, Principal principal);
    List<CatMainDataDto> getAll(Principal principal);

    void makeFriend(Long ourId, Long friendId, Principal principal);
    void removeFriend(Long ourId, Long friendId, Principal principal);
    List<CatMainDataDto> getFriends(Long id, Principal principal);
}
