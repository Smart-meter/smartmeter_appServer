import org.cmpe295.user.entity.User;
import org.cmpe295.user.entity.enums.ROLE;
import org.cmpe295.user.model.AuthenticationRequest;
import org.cmpe295.user.model.AuthenticationResponse;
import org.cmpe295.user.model.RegisterRequest;
import org.cmpe295.user.repository.UserRepository;
import org.cmpe295.user.security.service.JWTService;
import org.cmpe295.user.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class AutheticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        /*
        // Given
        RegisterRequest request = RegisterRequest.builder().build();
        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password("encodedPassword") // Mocked encoded password
                .role(request.getRole())
                .build();
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("mockedJWTToken");

        // When
        AuthenticationResponse response = null;
        try {
            response = authenticationService.register(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Then
        assertEquals("mockedJWTToken", response.getAccessToken());
        verify(userRepository, times(1)).save(any());
        verify(jwtService, times(1)).generateToken(any());

         */
    }

    @Test
    public void testAuthenticate() {
        /*
        // Given
        AuthenticationRequest request = new AuthenticationRequest("john@example.com", "password");
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(ROLE.ADMIN)
                .build();
        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("mockedJWTToken");

        // When
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Then
        assertEquals("mockedJWTToken", response.getAccessToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(any());
        verify(jwtService, times(1)).generateToken(any());

         */
    }
}
