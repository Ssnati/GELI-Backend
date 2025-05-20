package com.edu.uptc.gelibackend.services.users;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.entities.UserStatusHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsersServiceFindTest extends BaseUserServiceTest {

    @Test
    @DisplayName("findAll - Cuando no hay usuarios, debe retornar lista vacía")
    public void testGetAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Arrange (Preparar)
        when(userRepo.findAll()).thenReturn(Collections.emptyList());
        when(historyRepo.findAll()).thenReturn(Collections.emptyList());

        // Act (Actuar)
        List<UserResponseDTO> result = userService.findAll();

        // Assert (Verificar)
        assertNotNull(result, "La lista de usuarios no debe ser nula");
        assertTrue(result.isEmpty(), "La lista de usuarios debe estar vacía");

        // Verificar interacciones con los mocks
        verify(userRepo, times(1)).findAll();
        verify(historyRepo, times(1)).findAll();

        // Verificar que no hubo más interacciones con los mocks
        verifyNoMoreInteractions(userRepo, historyRepo);
        verifyNoInteractions(
                positionRepo,
                equipmentRepo,
                keyCloakUserService,
                mapper,
                userSpecification,
                positionHistoryRepo,
                authorizedUserEquipmentsRepo,
                mailSender
        );
    }

    @Test
    @DisplayName("findAll - Cuando existen usuarios, debe retornar lista de usuarios con todos los datos mapeados")
    public void testGetAllUsers_WhenUsersExist_ShouldReturnCompleteUserList() {
        // Arrange
        List<UserEntity> mockUsers = dataProvider.usersListMock();
        when(userRepo.findAll()).thenReturn(mockUsers);
        when(historyRepo.findAll()).thenReturn(Collections.emptyList());

        // Configurar el mock para que devuelva los usuarios con sus datos
        when(mapper.completeDTOWithEntity(any(UserResponseDTO.class), any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserResponseDTO dto = invocation.getArgument(0);
                    UserEntity user = invocation.getArgument(1);
                    dto.setId(user.getId());
                    dto.setKeycloakId(user.getKeycloakId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setIdentification(user.getIdentification());
                    dto.setEnabledStatus(user.getState());
                    dto.setRole(user.getRole());
                    dto.setCreationDate(user.getCreateDateUser());
                    if (user.getPosition() != null) {
                        dto.setPosition(new PositionDTO(user.getPosition().getId(), user.getPosition().getName()));
                    }
                    return dto;
                });

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertNotNull(result, "La lista de usuarios no debe ser nula");
        assertEquals(10, result.size(), "Deben retornarse 10 usuarios");

        // Verificar que se llamó a los repositorios
        verify(userRepo, times(1)).findAll();
        verify(historyRepo, times(1)).findAll();

        // Verificar que se mapearon todos los usuarios
        verify(mapper, times(mockUsers.size())).completeDTOWithEntity(any(UserResponseDTO.class), any(UserEntity.class));

        // Verificar datos de los usuarios
        assertUserData(mockUsers, result);
    }

    private void assertUserData(List<UserEntity> expectedUsers, List<UserResponseDTO> actualUsers) {
        // Verificar que todos los usuarios tengan los datos correctos
        for (int i = 0; i < expectedUsers.size(); i++) {
            UserEntity expected = expectedUsers.get(i);
            UserResponseDTO actual = actualUsers.get(i);

            assertEquals(expected.getId(), actual.getId(), "El ID debe coincidir");
            assertEquals(expected.getKeycloakId(), actual.getKeycloakId(), "El Keycloak ID debe coincidir");
            assertEquals(expected.getFirstName(), actual.getFirstName(), "El nombre debe coincidir");
            assertEquals(expected.getLastName(), actual.getLastName(), "El apellido debe coincidir");
            assertEquals(expected.getEmail(), actual.getEmail(), "El email debe coincidir");
            assertEquals(expected.getIdentification(), actual.getIdentification(), "La identificación debe coincidir");
            assertEquals(expected.getState(), actual.getEnabledStatus(), "El estado debe coincidir");
            assertEquals(expected.getRole(), actual.getRole(), "El rol debe coincidir");
            assertEquals(expected.getCreateDateUser(), actual.getCreationDate(), "La fecha de creación debe coincidir");

            // Verificar posición
            if (expected.getPosition() != null) {
                assertNotNull(actual.getPosition(), "La posición no debe ser nula");
                assertEquals(expected.getPosition().getId(), actual.getPosition().getId(), "El ID del cargo debe coincidir");
                assertEquals(expected.getPosition().getName(), actual.getPosition().getName(), "El nombre del cargo debe coincidir");
            } else {
                assertNull(actual.getPosition(), "La posición debe ser nula");
            }
        }
    }

    @Test
    @DisplayName("findAll - Cuando existen usuarios sin cargo, debe retornar lista de usuarios con null en la posición")
    public void testGetAllUsers_WithUsersWithoutPosition_ShouldReturnUsersWithNullPosition() {
        // Arrange
        List<UserEntity> mockUsers = dataProvider.usersListMock();
        // Set position to null for all users
        mockUsers.forEach(user -> user.setPosition(null));

        when(userRepo.findAll()).thenReturn(mockUsers);
        when(historyRepo.findAll()).thenReturn(Collections.emptyList());
        when(mapper.completeDTOWithEntity(any(UserResponseDTO.class), any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    UserEntity user = invocation.getArgument(1);
                    dto.setId(user.getId());
                    dto.setPosition(null); // Position should be null
                    return dto;
                });

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertNotNull(result, "La lista de usuarios no debe ser nula");
        assertFalse(result.isEmpty(), "La lista no debe estar vacía");
        assertNull(result.get(0).getPosition(), "La posición debe ser nula");
    }

    @Test
    @DisplayName("findAll - Cuando existen usuarios con cargo, debe retornar lista de usuarios con las posiciones correctas")
    public void testGetAllUsers_WithUsersWithPosition_ShouldReturnUsersWithCorrectPositions() {
        // Arrange
        List<UserEntity> mockUsers = dataProvider.usersListMock();
        when(userRepo.findAll()).thenReturn(mockUsers);
        when(historyRepo.findAll()).thenReturn(Collections.emptyList());

        // Configurar el mock para devolver el DTO con los datos del usuario incluyendo la posición
        when(mapper.completeDTOWithEntity(any(UserResponseDTO.class), any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    UserEntity user = invocation.getArgument(1);
                    dto.setId(user.getId());
                    dto.setKeycloakId(user.getKeycloakId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setIdentification(user.getIdentification());
                    dto.setEnabledStatus(user.getState());
                    dto.setRole(user.getRole());
                    dto.setCreationDate(user.getCreateDateUser());
                    if (user.getPosition() != null) {
                        dto.setPosition(new PositionDTO(user.getPosition().getId(), user.getPosition().getName()));
                    }
                    return dto;
                });

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertNotNull(result, "La lista de usuarios no debe ser nula");
        assertEquals(10, result.size(), "Deben retornarse 10 usuarios");

        // Verificar que los usuarios tienen las posiciones correctas según el data provider
        assertUserPositions(mockUsers, result);
    }

    private void assertUserPositions(List<UserEntity> expectedUsers, List<UserResponseDTO> actualUsers) {
        // Mapa de posiciones esperadas por ID de usuario
        Map<Long, String> expectedPositions = new HashMap<>();
        expectedUsers.forEach(user -> {
            if (user.getPosition() != null) {
                expectedPositions.put(user.getId(), user.getPosition().getName());
            }
        });

        // Verificar que cada usuario tenga la posición correcta
        for (UserResponseDTO user : actualUsers) {
            String expectedPosition = expectedPositions.get(user.getId());
            if (expectedPosition != null) {
                assertNotNull(user.getPosition(), "La posición no debe ser nula para el usuario " + user.getId());
                assertEquals(expectedPosition, user.getPosition().getName(),
                        "El cargo del usuario " + user.getId() + " no coincide");
            }
        }
    }

    @Test
    @DisplayName("findAll - Cuando existen usuarios con historial de estado, debe establecer la fecha de modificación más reciente")
    public void testGetAllUsers_WithUsersWithStatusHistory_ShouldSetLatestModificationDate() {
        // Arrange
        List<UserEntity> mockUsers = dataProvider.usersListMock();
        UserEntity testUser1 = mockUsers.get(0);
        UserEntity testUser2 = mockUsers.get(1);

        // Crear historial de estados para los usuarios
        UserStatusHistoryEntity statusHistory1 = new UserStatusHistoryEntity();
        statusHistory1.setModificationStatusDate(java.time.LocalDate.of(2023, 5, 20));
        statusHistory1.setUser(testUser1);

        // Una fecha más reciente para el mismo usuario
        UserStatusHistoryEntity statusHistory2 = new UserStatusHistoryEntity();
        statusHistory2.setModificationStatusDate(java.time.LocalDate.of(2023, 6, 15));
        statusHistory2.setUser(testUser1);

        // Historial para otro usuario
        UserStatusHistoryEntity statusHistory3 = new UserStatusHistoryEntity();
        statusHistory3.setModificationStatusDate(java.time.LocalDate.of(2023, 4, 10));
        statusHistory3.setUser(testUser2);

        List<UserStatusHistoryEntity> allHistories = Arrays.asList(statusHistory1, statusHistory2, statusHistory3);

        when(userRepo.findAll()).thenReturn(mockUsers);
        when(historyRepo.findAll()).thenReturn(allHistories);

        // Configurar el mock para devolver el DTO con los datos del usuario
        when(mapper.completeDTOWithEntity(any(UserResponseDTO.class), any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    UserEntity user = invocation.getArgument(1);
                    dto.setId(user.getId());
                    // El servicio debería establecer la fecha de modificación más reciente
                    return dto;
                });

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertNotNull(result, "La lista de usuarios no debe ser nula");
        assertFalse(result.isEmpty(), "La lista no debe estar vacía");

        // Encontrar los usuarios en el resultado
        UserResponseDTO resultUser1 = result.stream()
                .filter(u -> u.getId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Usuario 1 no encontrado en los resultados"));

        UserResponseDTO resultUser2 = result.stream()
                .filter(u -> u.getId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Usuario 2 no encontrado en los resultados"));

        // Verificar que se estableció la fecha de modificación más reciente para cada usuario
        assertEquals(
                java.time.LocalDate.of(2023, 6, 15),
                resultUser1.getModificationStatusDate(),
                "La fecha de modificación debe ser la más reciente para el usuario 1"
        );

        assertEquals(
                java.time.LocalDate.of(2023, 4, 10),
                resultUser2.getModificationStatusDate(),
                "La fecha de modificación debe ser la correcta para el usuario 2"
        );
    }

    @Test
    @DisplayName("findAll - Cuando el repositorio de usuarios lanza una excepción, debe propagarla")
    public void testGetAllUsers_WhenUserRepoThrowsException_ShouldPropagateException() {
        // Arrange
        when(userRepo.findAll()).thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.findAll(),
                "Debe lanzar RuntimeException"
        );

        assertEquals("Error de base de datos", exception.getMessage(), "El mensaje de error debe coincidir");
    }

    @Test
    @DisplayName("findAll - Cuando el repositorio de historial lanza una excepción, debe propagarla")
    public void testGetAllUsers_WhenHistoryRepoThrowsException_ShouldPropagateException() {
        // Arrange
        when(userRepo.findAll()).thenReturn(Collections.emptyList());
        when(historyRepo.findAll()).thenThrow(new RuntimeException("Error al consultar historial"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.findAll(),
                "Debe lanzar RuntimeException"
        );

        assertEquals("Error al consultar historial", exception.getMessage(), "El mensaje de error debe coincidir");
    }

}
