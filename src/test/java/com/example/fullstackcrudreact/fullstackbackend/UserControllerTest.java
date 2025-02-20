package com.example.fullstackcrudreact.fullstackbackend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

import com.example.fullstackcrudreact.fullstackbackend.controller.UserController;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.example.fullstackcrudreact.fullstackbackend.service.ExcelExportService;
import com.example.fullstackcrudreact.fullstackbackend.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({UserController.class})
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ExcelExportService excelExportService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;

    @BeforeEach
    public void setup() {
        mockMvc = webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("lovro");
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("lovro");
    }

    @Test
    @WithMockUser(username = "lovro", roles = {"USER"})
    public void testCreateUser() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lovro"));
    }

    @Test
    @WithMockUser(username = "lovro", roles = {"USER"})
    public void testGetUserById() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lovro"));
    }

    @Test
    @WithMockUser(username = "lovro", roles = {"USER"})
    public void testUpdateUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User updated successfully"));
    }

    @Test
    @WithMockUser(username = "lovro", roles = {"USER"})
    public void testDeleteUser() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User with ID 1 has been deleted successfully."));
    }

    @Test
    @WithMockUser(username = "lovro", roles = {"USER"})
    public void testGetAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("lovro"));
    }
}
