package com.example.fullstackcrudreact.fullstackbackend;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.fullstackcrudreact.fullstackbackend.controller.UserController;
import com.example.fullstackcrudreact.fullstackbackend.model.LoginRequest;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;



class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PagedResourcesAssembler<User> pagedResourcesAssembler;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setUsername("admin700");
        user.setPassword("admin700");

        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUser_Fail_DuplicateUsername() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUser_Success() throws Exception {
        User user = new User();
        user.setUsername("admin700");
        user.setPassword("admin700");

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setPassword("admin700");
        loginRequest.setUsername("admin700");

        when(userRepository.findByUsername("admin700")).thenReturn(user);
        when(passwordEncoder.matches("admin700", "encodedPassword")).thenReturn(true);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/loginuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testLoginUser_Fail_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setPassword("password");
        loginRequest.setUsername("username");

        when(userRepository.findByUsername("testuser")).thenReturn(null);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/loginuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Page<User> userPage = new PageImpl<>(Arrays.asList(user), PageRequest.of(0, 10), 1);
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/users")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/user/1");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById_Fail_NotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/user/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("olduser");

        User updatedUser = new User();
        updatedUser.setUsername("newuser");
        updatedUser.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .delete("/user/1");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_Fail_NotFound() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);

        RequestBuilder request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .delete("/user/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
