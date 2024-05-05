package kosandron.services;

import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;

import java.time.LocalDate;
import java.util.List;

public interface OwnerService {
    Long create(String name, LocalDate birtdayDate);
    void removeById(Long id);
    OwnerMainDataDto getById(Long id);
    List<OwnerMainDataDto> getAll();
    List<CatMainDataDto> getCatsListByOwnerId(Long id);
}
