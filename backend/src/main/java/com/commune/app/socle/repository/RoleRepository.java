
package com.commune.app.socle.repository;

import com.commune.app.socle.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByNom(String nom);



}
