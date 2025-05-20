package com.edu.uptc.gelibackend.services.users;

import com.edu.uptc.gelibackend.entities.PositionEntity;
import com.edu.uptc.gelibackend.entities.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsersDataProvider {

    public List<UserEntity> usersListMock(){
        List<UserEntity> users = new ArrayList<>();
        
        // Crear Cargos para asociar a los usuarios
        // Cargos
        PositionEntity positionAdmin = new PositionEntity(1L, "ANALISTA JUNIOR", new ArrayList<>());
        PositionEntity positionTech = new PositionEntity(2L, "ANALISTA SENIOR", new ArrayList<>());
        PositionEntity positionLab = new PositionEntity(3L, "DESARROLLADOR FRONTEND", new ArrayList<>());
        PositionEntity positionTeacher = new PositionEntity(4L, "DESARROLLADOR BACKEND", new ArrayList<>());
        PositionEntity positionDirector = new PositionEntity(5L, "ARQUITECTO DE SOFTWARE", new ArrayList<>());
        
        // Roles
        String roleAdmin = "ADMIN";
        String roleTechnician = "TECHNICIAN";
        String roleLabAssistant = "LAB_ASSISTANT";
        String roleTeacher = "TEACHER";
        String roleDirector = "DIRECTOR";
        
        // Usuario 1
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setKeycloakId("kc-001-abc-def");
        user1.setFirstName("Carlos");
        user1.setLastName("Rodríguez");
        user1.setEmail("carlos.rodriguez@uptc.edu.co");
        user1.setIdentification("1015789456");
        user1.setState(Boolean.TRUE);
        user1.setRole(roleAdmin);
        user1.setCreateDateUser(LocalDate.of(2023, 1, 15));
        user1.setPosition(positionAdmin);
        user1.setStatusHistory(new ArrayList<>());
        user1.setAuthorizedUserEquipments(new ArrayList<>());
        user1.setPositionHistory(new ArrayList<>());
        users.add(user1);
        
        // Usuario 2
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setKeycloakId("kc-002-ghi-jkl");
        user2.setFirstName("Laura");
        user2.setLastName("Gómez");
        user2.setEmail("laura.gomez@uptc.edu.co");
        user2.setIdentification("1020456789");
        user2.setState(Boolean.TRUE);
        user2.setRole(roleTechnician);
        user2.setCreateDateUser(LocalDate.of(2023, 2, 20));
        user2.setPosition(positionTech);
        user2.setStatusHistory(new ArrayList<>());
        user2.setAuthorizedUserEquipments(new ArrayList<>());
        user2.setPositionHistory(new ArrayList<>());
        users.add(user2);
        
        // Usuario 3
        UserEntity user3 = new UserEntity();
        user3.setId(3L);
        user3.setKeycloakId("kc-003-mno-pqr");
        user3.setFirstName("Andrés");
        user3.setLastName("Martínez");
        user3.setEmail("andres.martinez@uptc.edu.co");
        user3.setIdentification("79845632");
        user3.setState(Boolean.TRUE);
        user3.setRole(roleLabAssistant);
        user3.setCreateDateUser(LocalDate.of(2023, 3, 10));
        user3.setPosition(positionLab);
        user3.setStatusHistory(new ArrayList<>());
        user3.setAuthorizedUserEquipments(new ArrayList<>());
        user3.setPositionHistory(new ArrayList<>());
        users.add(user3);
        
        // Usuario 4
        UserEntity user4 = new UserEntity();
        user4.setId(4L);
        user4.setKeycloakId("kc-004-stu-vwx");
        user4.setFirstName("María");
        user4.setLastName("Pérez");
        user4.setEmail("maria.perez@uptc.edu.co");
        user4.setIdentification("52369874");
        user4.setState(Boolean.TRUE);
        user4.setRole(roleTeacher);
        user4.setCreateDateUser(LocalDate.of(2023, 4, 5));
        user4.setPosition(positionTeacher);
        user4.setStatusHistory(new ArrayList<>());
        user4.setAuthorizedUserEquipments(new ArrayList<>());
        user4.setPositionHistory(new ArrayList<>());
        users.add(user4);
        
        // Usuario 5
        UserEntity user5 = new UserEntity();
        user5.setId(5L);
        user5.setKeycloakId("kc-005-yza-bcd");
        user5.setFirstName("Juan");
        user5.setLastName("López");
        user5.setEmail("juan.lopez@uptc.edu.co");
        user5.setIdentification("1012345678");
        user5.setState(Boolean.FALSE); // Usuario inactivo
        user5.setRole(roleTechnician);
        user5.setCreateDateUser(LocalDate.of(2023, 5, 12));
        user5.setPosition(positionTech);
        user5.setStatusHistory(new ArrayList<>());
        user5.setAuthorizedUserEquipments(new ArrayList<>());
        user5.setPositionHistory(new ArrayList<>());
        users.add(user5);
        
        // Usuario 6
        UserEntity user6 = new UserEntity();
        user6.setId(6L);
        user6.setKeycloakId("kc-006-efg-hij");
        user6.setFirstName("Carolina");
        user6.setLastName("Sánchez");
        user6.setEmail("carolina.sanchez@uptc.edu.co");
        user6.setIdentification("1030789456");
        user6.setState(Boolean.TRUE);
        user6.setRole(roleDirector);
        user6.setCreateDateUser(LocalDate.of(2023, 6, 18));
        user6.setPosition(positionDirector);
        user6.setStatusHistory(new ArrayList<>());
        user6.setAuthorizedUserEquipments(new ArrayList<>());
        user6.setPositionHistory(new ArrayList<>());
        users.add(user6);
        
        // Usuario 7
        UserEntity user7 = new UserEntity();
        user7.setId(7L);
        user7.setKeycloakId("kc-007-klm-nop");
        user7.setFirstName("Diego");
        user7.setLastName("Torres");
        user7.setEmail("diego.torres@uptc.edu.co");
        user7.setIdentification("80123456");
        user7.setState(Boolean.TRUE);
        user7.setRole(roleTeacher);
        user7.setCreateDateUser(LocalDate.of(2023, 7, 22));
        user7.setPosition(positionTeacher);
        user7.setStatusHistory(new ArrayList<>());
        user7.setAuthorizedUserEquipments(new ArrayList<>());
        user7.setPositionHistory(new ArrayList<>());
        users.add(user7);
        
        // Usuario 8
        UserEntity user8 = new UserEntity();
        user8.setId(8L);
        user8.setKeycloakId("kc-008-qrs-tuv");
        user8.setFirstName("Paula");
        user8.setLastName("Ramírez");
        user8.setEmail("paula.ramirez@uptc.edu.co");
        user8.setIdentification("52123987");
        user8.setState(Boolean.FALSE); // Usuario inactivo
        user8.setRole(roleLabAssistant);
        user8.setCreateDateUser(LocalDate.of(2023, 8, 14));
        user8.setPosition(positionLab);
        user8.setStatusHistory(new ArrayList<>());
        user8.setAuthorizedUserEquipments(new ArrayList<>());
        user8.setPositionHistory(new ArrayList<>());
        users.add(user8);
        
        // Usuario 9
        UserEntity user9 = new UserEntity();
        user9.setId(9L);
        user9.setKeycloakId("kc-009-wxy-zab");
        user9.setFirstName("Javier");
        user9.setLastName("Gutiérrez");
        user9.setEmail("javier.gutierrez@uptc.edu.co");
        user9.setIdentification("1022456789");
        user9.setState(Boolean.TRUE);
        user9.setRole(roleAdmin);
        user9.setCreateDateUser(LocalDate.of(2023, 9, 5));
        user9.setPosition(positionAdmin);
        user9.setStatusHistory(new ArrayList<>());
        user9.setAuthorizedUserEquipments(new ArrayList<>());
        user9.setPositionHistory(new ArrayList<>());
        users.add(user9);
        
        // Usuario 10
        UserEntity user10 = new UserEntity();
        user10.setId(10L);
        user10.setKeycloakId("kc-010-cde-fgh");
        user10.setFirstName("Valentina");
        user10.setLastName("Díaz");
        user10.setEmail("valentina.diaz@uptc.edu.co");
        user10.setIdentification("53789456");
        user10.setState(Boolean.TRUE);
        user10.setRole(roleDirector);
        user10.setCreateDateUser(LocalDate.of(2023, 10, 30));
        user10.setPosition(positionDirector);
        user10.setStatusHistory(new ArrayList<>());
        user10.setAuthorizedUserEquipments(new ArrayList<>());
        user10.setPositionHistory(new ArrayList<>());
        users.add(user10);
        
        return users;
    }
}
