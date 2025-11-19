package ignis.repositorio.institucional.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ignis.repositorio.institucional.exception.ResourceAlreadyExistsException;
import ignis.repositorio.institucional.exception.ResourceNotFoundException;
import ignis.repositorio.institucional.user.dto.CreateUserRequest;
import ignis.repositorio.institucional.user.dto.UpdateUserRequest;
import ignis.repositorio.institucional.user.dto.UserResponse;
import ignis.repositorio.institucional.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .active(true)
                .roleIds(Set.of(1L))
                .build();

        updateRequest = UpdateUserRequest.builder()
                .username("updateduser")
                .email("updated@example.com")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/users - Should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.active").value(true));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should return 400 when username is blank")
    void shouldReturn400WhenUsernameIsBlank() throws Exception {
        // Given
        createRequest.setUsername("");

        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given
        createRequest.setEmail("invalid-email");

        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should return 400 when password is too short")
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {
        // Given
        createRequest.setPassword("short");

        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Should return 404 when user not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Given
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException("User", "id", 999L));

        // When/Then
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("GET /api/v1/users/username/{username} - Should get user by username successfully")
    void shouldGetUserByUsernameSuccessfully() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser")).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    @DisplayName("GET /api/v1/users - Should get all users with pagination")
    void shouldGetAllUsersWithPagination() throws Exception {
        // Given
        UserResponse user1 = UserResponse.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .active(true)
                .build();
        
        UserResponse user2 = UserResponse.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .active(true)
                .build();

        Page<UserResponse> usersPage = new PageImpl<>(
                Arrays.asList(user1, user2),
                PageRequest.of(0, 20),
                2
        );

        when(userService.getAllUsers(any())).thenReturn(usersPage);

        // When/Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].username").value("user1"))
                .andExpect(jsonPath("$.content[1].username").value("user2"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(userService, times(1)).getAllUsers(any());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .username("updateduser")
                .email("updated@example.com")
                .active(true)
                .build();

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(updatedResponse);

        // When/Then
        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService, times(1)).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Should delete user successfully")
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When/Then
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/activate - Should activate user successfully")
    void shouldActivateUserSuccessfully() throws Exception {
        // Given
        UserResponse activatedUser = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .active(true)
                .build();

        when(userService.activateUser(1L)).thenReturn(activatedUser);

        // When/Then
        mockMvc.perform(patch("/api/v1/users/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        verify(userService, times(1)).activateUser(1L);
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/deactivate - Should deactivate user successfully")
    void shouldDeactivateUserSuccessfully() throws Exception {
        // Given
        UserResponse deactivatedUser = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .active(false)
                .build();

        when(userService.deactivateUser(1L)).thenReturn(deactivatedUser);

        // When/Then
        mockMvc.perform(patch("/api/v1/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        verify(userService, times(1)).deactivateUser(1L);
    }
}
