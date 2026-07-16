package ee.piperal.banking_backend.Repositories;

import ee.piperal.banking_backend.Entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@Repository
public interface UserRepository extends JpaRepository<Person,Long> {
    Optional<Person> findByUsername(String username);

    Optional<Person> findUsernameByUsername(String username);

    @Query("SELECT p FROM Person p WHERE LOWER(p.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Person> userSearch(@Param("username") String username);

    Optional<Person> findByEmail(String email);
}
