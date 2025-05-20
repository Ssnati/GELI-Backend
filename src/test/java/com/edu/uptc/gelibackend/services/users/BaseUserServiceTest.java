package com.edu.uptc.gelibackend.services.users;

import com.edu.uptc.gelibackend.mappers.UserMapper;
import com.edu.uptc.gelibackend.repositories.*;
import com.edu.uptc.gelibackend.services.KeyCloakUserService;
import com.edu.uptc.gelibackend.services.UserService;
import com.edu.uptc.gelibackend.specifications.UserSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUserServiceTest {
    // Configuración común de mocks y setup
    @Mock
    protected PositionRepository positionRepo;
    
    @Mock
    protected UserStatusHistoryRepository historyRepo;
    
    @Mock
    protected EquipmentRepository equipmentRepo;
    
    @Mock
    protected UserRepository userRepo;
    
    @Mock
    protected KeyCloakUserService keyCloakUserService;
    
    @Mock
    protected UserMapper mapper;
    
    @Mock
    protected UserSpecification userSpecification;
    
    @Mock
    protected UserPositionHistoryRepository positionHistoryRepo;
    
    @Mock
    protected AuthorizedUserEquipmentsRepo authorizedUserEquipmentsRepo;
    
    @Mock
    protected JavaMailSender mailSender;
    
    @InjectMocks
    protected UserService userService;
    
    protected UsersDataProvider dataProvider;

    @BeforeEach
    void setUp() {
        // Inicializar proveedor de datos de prueba
        dataProvider = new UsersDataProvider();
    }
}
