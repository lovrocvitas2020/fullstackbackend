package com.example.fullstackcrudreact.fullstackbackend;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.fullstackcrudreact.fullstackbackend.controller.UserDetailsController;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserDetailsRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class UserDetailsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsController userDetailsController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userDetailsController).build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUser(user);
        userDetails.setPhoneNumber("1234567890");
        userDetails.setAddress("123 Main St");
        userDetails.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userDetails.setSex(UserDetails.Sex.MALE);
        userDetails.setCountryOfBirth("USA");
        userDetails.setCity("New York");
        userDetails.setZipCode("10001");
    }

    @Test
    void testGetAllUserDetails() throws Exception {
        when(userDetailsRepository.findAll()).thenReturn(Arrays.asList(userDetails));

        mockMvc.perform(MockMvcRequestBuilders.get("/userdetails")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userDetailsRepository, times(1)).findAll();
    }

     // ✅ 2. Test getting user details by ID (found)
    @Test
    void testGetUserDetailsById_Found() throws Exception {
        when(userDetailsRepository.findUserDetailsById(1L)).thenReturn(Optional.of(userDetails));

        mockMvc.perform(MockMvcRequestBuilders.get("/userdetails/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userDetailsRepository, times(1)).findUserDetailsById(1L);
    }

    // ✅ 3. Test getting user details by ID (not found)
    @Test
    void testGetUserDetailsById_NotFound() throws Exception {
        when(userDetailsRepository.findUserDetailsById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/userdetails/1"))
                .andExpect(status().isNotFound());

        verify(userDetailsRepository, times(1)).findUserDetailsById(1L);
    }

    // ✅ 4. Test creating user details
    @Test
    void testCreateUserDetails() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userDetailsRepository.existsByUser(user)).thenReturn(false);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(userDetails);

        MockMultipartFile userImage = new MockMultipartFile("userImage", "test.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/adduserdetails/1")
                .file(userImage)
                .param("phoneNumber", "1234567890")
                .param("address", "123 Main St")
                .param("dateOfBirth", "1990-01-01")
                .param("sex", "MALE")
                .param("countryOfBirth", "USA")
                .param("city", "New York")
                .param("zipCode", "10001"))
                .andExpect(status().isOk());

        verify(userDetailsRepository, times(1)).save(any(UserDetails.class));
    }

    // ✅ 5. Test creating user details when user does not exist
    @Test
    void testCreateUserDetails_UserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/adduserdetails/1")
                .param("phoneNumber", "1234567890")
                .param("address", "123 Main St")
                .param("dateOfBirth", "1990-01-01")
                .param("sex", "MALE")
                .param("countryOfBirth", "USA")
                .param("city", "New York")
                .param("zipCode", "10001"))
                .andExpect(status().isInternalServerError());

        verify(userDetailsRepository, never()).save(any(UserDetails.class));
    }

    // ✅ 6. Test updating user details
    @Test
    void testUpdateUserDetails() throws Exception {
        when(userDetailsRepository.findUserDetailsById(1L)).thenReturn(Optional.of(userDetails));
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(userDetails);

        MockMultipartFile userImage = new MockMultipartFile("userImage", "test.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/userdetails/1")
                .file(userImage)
                .param("phoneNumber", "9876543210")
                .param("address", "456 Another St")
                .param("dateOfBirth", "1995-05-05")
                .param("sex", "FEMALE")
                .param("countryOfBirth", "Canada")
                .param("city", "Toronto")
                .param("zipCode", "20002")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk());

        verify(userDetailsRepository, times(1)).save(any(UserDetails.class));
    }

   

    // ✅ 8. Test deleting user details
    @Test
    void testDeleteUserDetails() throws Exception {
        when(userDetailsRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userDetailsRepository).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/userdetails/1"))
                .andExpect(status().isNoContent());

        verify(userDetailsRepository, times(1)).deleteById(1L);
    }

    // ✅ 9. Test deleting user details (not found)
    @Test
    void testDeleteUserDetails_NotFound() throws Exception {
        when(userDetailsRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/userdetails/1"))
                .andExpect(status().isNotFound());

        verify(userDetailsRepository, never()).deleteById(1L);
    }

}
