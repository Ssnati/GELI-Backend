package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.AuthorizedUserEquipmentsEntity;
import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AuthorizedUserEquipmentsRepo extends JpaRepository<AuthorizedUserEquipmentsEntity, AuthorizedUserEquipmentsId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM AuthorizedUserEquipmentsEntity e WHERE e.user.id = :userId")
    void deleteByUserId(Long userId);


}
