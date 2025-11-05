package de.szut.lf8_starter.integration;

import static de.szut.lf8_starter.integration.ProjectIntegrationTest.createMockEmployee;
import static de.szut.lf8_starter.integration.ProjectIntegrationTest.createTestProject;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.client.EmployeeServiceClient;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto;
import de.szut.lf8_starter.entities.Project;
import de.szut.lf8_starter.entities.ProjectEmployee;
import de.szut.lf8_starter.repositories.ProjectEmployeeRepository;
import de.szut.lf8_starter.repositories.ProjectRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeServiceClient employeeServiceClient;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectEmployeeRepository projectEmployeeRepository;

  @BeforeEach
  void setUp() {
    projectEmployeeRepository.deleteAll();
    projectRepository.deleteAll();
  }

  @Test
  void testGetAllProjectsByEmployeeId() throws Exception {
    GetEmployeeDto employee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(employee);

    Project project = createTestProject("Test Project");
    Project savedProject = projectRepository.save(project);

    ProjectEmployee pe = new ProjectEmployee();
    pe.setProject(savedProject);
    pe.setEmployeeId(1L);
    pe.setStartDate(LocalDate.now());
    pe.setEndDate(LocalDate.now().plusMonths(6));
    pe.setQualification("Java Developer");
    projectEmployeeRepository.save(pe);

    mockMvc.perform(get("/api/v1/employees/1/projects"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.employeeId").value(1))
        .andExpect(jsonPath("$.projects", hasSize(1)))
        .andExpect(jsonPath("$.projects[0].projectDescription").value("Test Project"));
  }

    @Test
    void testGetAllProjectsByEmployeeIdNoProjects() throws Exception {
        GetEmployeeDto employee = createMockEmployee(1L);
        when(employeeServiceClient.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/v1/employees/1/projects"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Could not find projects for employee with id: 1"));
    }
}
