package org.kosandron.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kosandron.data.InputCatData;
import org.kosandron.dto.CatMainDataDto;

import java.security.Principal;
import java.util.List;

public interface CatController {
    CatMainDataDto create(InputCatData inputCatData, Principal principal) throws Exception;
    void removeById(Long id, Principal principal) throws Exception;
    CatMainDataDto getById(Long id, Principal principal) throws Exception;
    List<CatMainDataDto> getAll(Principal principal) throws Exception;

    void makeFriend(Long ourId, Long friendId, Principal principal) throws Exception;
    void removeFriend(Long ourId, Long friendId, Principal principal) throws Exception;
    List<CatMainDataDto> getFriends(Long id, Principal principal) throws Exception;
}
