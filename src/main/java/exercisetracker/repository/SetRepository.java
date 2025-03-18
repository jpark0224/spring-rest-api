package exercisetracker.repository;

import exercisetracker.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepository extends JpaRepository<Set, Long>{
}
