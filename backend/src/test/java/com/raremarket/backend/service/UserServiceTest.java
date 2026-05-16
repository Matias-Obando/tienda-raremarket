package com.raremarket.backend.service;

import com.raremarket.backend.model.User;
import com.raremarket.backend.repository.UserRepository;
import com.raremarket.backend.dto.ProfileUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Inject dependencies manually
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);

        testUser = new User();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPasswordHash("encodedPassword");
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("Should register user successfully")
    void testRegister_Success() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        boolean result = userService.register(user);

        // Assert
        assertTrue(result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("encoded_password123", savedUser.getPassword());
    }

    @Test
    @DisplayName("Should fail registration with null user")
    void testRegister_NullUser() {
        // Act
        boolean result = userService.register(null);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail registration with null email")
    void testRegister_NullEmail() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail(null);
        user.setPassword("password123");

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail registration with blank email")
    void testRegister_BlankEmail() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("   ");
        user.setPassword("password123");

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail registration with null password")
    void testRegister_NullPassword() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword(null);

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail registration with blank password")
    void testRegister_BlankPassword() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("   ");

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail registration when username already exists")
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        User user = new User();
        user.setUsername("existinguser");
        user.setEmail("newuser@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail registration when email already exists")
    void testRegister_EmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should normalize email to lowercase")
    void testRegister_NormalizeEmailToLowercase() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("TEST@EXAMPLE.COM");
        user.setPassword("password123");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.register(user);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("test@example.com", userCaptor.getValue().getEmail());
    }

    @Test
    @DisplayName("Should generate UUID if user has no ID")
    void testRegister_GenerateUUID() {
        // Arrange
        User user = new User();
        user.setId(null);
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.register(user);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getId());
    }

    // ==================== AUTHENTICATE TESTS ====================

    @Test
    @DisplayName("Should authenticate user with username successfully")
    void testAuthenticate_WithUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // Act
        Optional<User> result = userService.authenticate("testuser", "password123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should authenticate user with email successfully")
    void testAuthenticate_WithEmail_Success() {
        // Arrange
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // Act
        Optional<User> result = userService.authenticate("test@example.com", "password123");

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with null identifier")
    void testAuthenticate_NullIdentifier() {
        // Act
        Optional<User> result = userService.authenticate(null, "password123");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with null password")
    void testAuthenticate_NullPassword() {
        // Act
        Optional<User> result = userService.authenticate("testuser", null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with non-existent user")
    void testAuthenticate_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.authenticate("nonexistent", "password");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with wrong password")
    void testAuthenticate_WrongPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act
        Optional<User> result = userService.authenticate("testuser", "wrongpassword");

        // Assert
        assertFalse(result.isPresent());
    }

    // ==================== GET ALL USERS TESTS ====================

    @Test
    @DisplayName("Should get all users")
    void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setId("1");
        User user2 = new User();
        user2.setId("2");
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("Should find user by ID successfully")
    void testFindById_Success() {
        // Arrange
        String userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void testFindById_NotFound() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findById("nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty when ID is null")
    void testFindById_NullId() {
        // Act
        Optional<User> result = userService.findById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, never()).findById(any());
    }

    // ==================== UPDATE PROFILE TESTS ====================

    @Test
    @DisplayName("Should update profile successfully")
    void testUpdateProfile_Success() {
        // Arrange
        String userId = testUser.getId();
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("updateduser");
        request.setEmail("updated@example.com");
        request.setLocation("New York");
        request.setPhone("1234567890");
        request.setBio("Updated bio");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Optional<User> result = userService.updateProfile(userId, request, false);

        // Assert
        assertTrue(result.isPresent());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("updateduser", savedUser.getUsername());
        assertEquals("updated@example.com", savedUser.getEmail());
    }

    @Test
    @DisplayName("Should fail updating profile with null user ID")
    void testUpdateProfile_NullUserId() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();

        // Act
        Optional<User> result = userService.updateProfile(null, request, false);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail updating profile with null request")
    void testUpdateProfile_NullRequest() {
        // Act
        Optional<User> result = userService.updateProfile(testUser.getId(), null, false);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail updating profile when user not found")
    void testUpdateProfile_UserNotFound() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.updateProfile("nonexistent", request, false);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testUpdateProfile_UsernameAlreadyExists() {
        // Arrange
        String userId = testUser.getId();
        User otherUser = new User();
        otherUser.setId("other-id");
        otherUser.setUsername("existinguser");

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("existinguser");
        request.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(userId, request, false));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testUpdateProfile_EmailAlreadyExists() {
        // Arrange
        String userId = testUser.getId();
        User otherUser = new User();
        otherUser.setId("other-id");
        otherUser.setEmail("existing@example.com");

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("updateduser");
        request.setEmail("existing@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(userId, request, false));
    }

    @Test
    @DisplayName("Should allow same user to update their own email")
    void testUpdateProfile_SameUserEmail() {
        // Arrange
        String userId = testUser.getId();
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("updateduser");
        request.setEmail("test@example.com"); // Same email

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser)); // Same user
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Optional<User> result = userService.updateProfile(userId, request, false);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Should clear avatar when clearAvatar is true")
    void testUpdateProfile_ClearAvatar() {
        // Arrange
        testUser.setAvatarUrl("http://example.com/avatar.jpg");
        String userId = testUser.getId();
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateProfile(userId, request, true);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNull(userCaptor.getValue().getAvatarUrl());
    }
}
