package com.example.fullstackcrudreact.fullstackbackend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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

    private Worklog worklog;
    private WorklogDTO worklogDTO;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);

        worklog = new Worklog(user, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), "Worklog Description");
        worklogDTO = new WorklogDTO(worklog);
    }


     @Test
    public void testGetAllWorklogs() {

        System.out.println("START testGetAllWorklogs");

        when(worklogRepository.findAllByOrderByWorkDateDesc()).thenReturn(Collections.singletonList(worklog));

        var result = worklogController.getAllWorklogs();

        System.out.println("Result size: " + result.size());
        System.out.println("Result first element: " + result.get(0));

        assertEquals(1, result.size());
        //assertEquals(worklogDTO, result.get(0));
    }

    @Test
    public void testGetWorklogById_Success() {
        when(worklogRepository.findById(anyLong())).thenReturn(Optional.of(worklog));

        var result = worklogController.getWorklogById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());     
    }

    @Test
    public void testGetWorklogById_NotFound() {
        when(worklogRepository.findById(anyLong())).thenReturn(Optional.empty());

        var result = worklogController.getWorklogById(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

   /* 
    public void testCreateWorklog_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(worklogRepository.save(any(Worklog.class))).thenReturn(worklog);

        var worklogMap = Map.of(
                "user", 1,
                "workDate", LocalDate.now().toString(),
                "startHour", "09:00",
                "endHour", "17:00",
                "workDescription", "Worklog Description"
        );

        var result = worklogController.createWorklog(worklogMap);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(worklogDTO, result.getBody());
    }

        */


}
