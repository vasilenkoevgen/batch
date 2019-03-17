package hello;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Evjen on 17.03.2019.
 */
public interface PersonRepository extends JpaRepository<Person, String> {
}
