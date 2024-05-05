package kosandron.dao;

import kosandron.entities.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatDao extends JpaRepository<Cat, Long> {
}
