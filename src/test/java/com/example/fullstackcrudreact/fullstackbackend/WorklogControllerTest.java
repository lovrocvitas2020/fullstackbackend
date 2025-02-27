package com.example.fullstackcrudreact.fullstackbackend;



import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.example.fullstackcrudreact.fullstackbackend.controller.WorklogController;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.model.Worklog;
import com.example.fullstackcrudreact.fullstackbackend.model.WorklogDTO;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.WorklogRepository;

@ExtendWith(MockitoExtension.class)
public class WorklogControllerTest {

     @Mock
    private WorklogRepository worklogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorklogController worklogController;

    private MockMvc mockMvc;


    private Worklog worklog;
    private WorklogDTO worklogDTO;
    private User testUser;
    private Worklog testWorklog;

    @BeforeEach
    public void setup() {

        mockMvc = standaloneSetup(worklogController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");

        testWorklog = new Worklog(testUser, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), "Worklog Description");
        worklogDTO = new WorklogDTO(testWorklog);
    }



    @Test
    void shouldGetAllWorklogs() throws Exception {
        when(worklogRepository.findAllByOrderByWorkDateDesc()).thenReturn(List.of(testWorklog));

        mockMvc.perform(get("/viewworklog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].workDescription").value("Worklog Description"));

        verify(worklogRepository, times(1)).findAllByOrderByWorkDateDesc();
    }

    @Test
    void shouldGetWorklogById() throws Exception {
        when(worklogRepository.findById(1L)).thenReturn(Optional.of(testWorklog));

        mockMvc.perform(get("/worklog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workDescription").value("Worklog Description"));

        verify(worklogRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundForNonExistingWorklog() throws Exception {
        when(worklogRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/worklog/99"))
                .andExpect(status().isNotFound());

        verify(worklogRepository, times(1)).findById(99L);
    }

    @Test
    void shouldCreateWorklog() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(worklogRepository.save(any(Worklog.class))).thenReturn(testWorklog);

        String worklogJson = """
                {
                    "user": 1,
                    "workDate": "2024-02-27",
                    "startHour": "09:00",
                    "endHour": "17:00",
                    "workDescription": "Worked on project"
                }
                """;

        mockMvc.perform(post("/add_worklog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(worklogJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workDescription").value("Worklog Description"));

        verify(worklogRepository, times(1)).save(any(Worklog.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidTimeRange() throws Exception {
        String worklogJson = """
                {
                    "user": 1,
                    "workDate": "2024-02-27",
                    "startHour": "17:00",
                    "endHour": "09:00",
                    "workDescription": "Invalid time range"
                }
                """;

        mockMvc.perform(post("/add_worklog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(worklogJson))
                .andExpect(status().isBadRequest());

        verify(worklogRepository, never()).save(any(Worklog.class));
    }

    @Test
    void shouldUpdateWorklog() throws Exception {
        when(worklogRepository.findById(1L)).thenReturn(Optional.of(testWorklog));
        when(worklogRepository.save(any(Worklog.class))).thenReturn(testWorklog);

        String updateJson = """
                {
                    "workDate": "2025-02-27",
                    "startHour": "10:00",
                    "endHour": "18:00",
                    "workDescription": "Updated work"
                }
                """;

        mockMvc.perform(put("/update_worklog/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workDescription").value("Updated work"));

        verify(worklogRepository, times(1)).findById(1L);
        verify(worklogRepository, times(1)).save(any(Worklog.class));
    }

    @Test
    void shouldDeleteWorklog() throws Exception {
        when(worklogRepository.findById(1L)).thenReturn(Optional.of(testWorklog));

        mockMvc.perform(delete("/delete_worklog/1"))
                .andExpect(status().isNoContent());

        verify(worklogRepository, times(1)).delete(testWorklog);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentWorklog() throws Exception {
        when(worklogRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/delete_worklog/99"))
                .andExpect(status().isNotFound());

        verify(worklogRepository, times(1)).findById(99L);
        verify(worklogRepository, never()).delete(any(Worklog.class));
    }

       


}
