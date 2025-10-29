package de.szut.lf8_starter.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_starter.client.EmployeeServiceClient;
import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto;
import de.szut.lf8_starter.entities.Project;
import de.szut.lf8_starter.repositories.ProjectEmployeeRepository;
import de.szut.lf8_starter.repositories.ProjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectEmployeeRepository projectEmployeeRepository;

  @MockBean
  private EmployeeServiceClient employeeServiceClient;

  @BeforeEach
  void setUp() {
    projectEmployeeRepository.deleteAll();
    projectRepository.deleteAll();
  }

  @Test
  void testCreateProject() throws Exception {
    GetEmployeeDto responsibleEmployee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(responsibleEmployee);
    when(employeeServiceClient.employeeExists(1L)).thenReturn(true);

    CreateProjectDto createDto = new CreateProjectDto();
    createDto.setBezeichnung("Test Project");
    createDto.setVerantwortlicherMitarbeiterId(1L);
    createDto.setKundenId(1L);
    createDto.setKundenansprechpartner("Max Mustermann");
    createDto.setKommentar("Test Kommentar");
    createDto.setStartdatum(LocalDate.now());
    createDto.setGeplantesEnddatum(LocalDate.now().plusMonths(6));
    createDto.setRequiredSkillIds(Arrays.asList(1L, 2L));

    mockMvc.perform(post("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.bezeichnung").value("Test Project"))
        .andExpect(jsonPath("$.verantwortlicherMitarbeiterId").value(1))
        .andExpect(jsonPath("$.requiredSkillIds", hasSize(2)));
  }
  @Test
  void testGetAllProjects() throws Exception {
    Project project1 = createTestProject("Project 1");
    Project project2 = createTestProject("Project 2");
    projectRepository.saveAll(Arrays.asList(project1, project2));

    mockMvc.perform(get("/api/v1/projects"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].bezeichnung").value("Project 1"))
        .andExpect(jsonPath("$[1].bezeichnung").value("Project 2"));
  }

  @Test
  void testGetProjectById() throws Exception {
    Project project = createTestProject("Test Project");
    Project savedProject = projectRepository.save(project);

    mockMvc.perform(get("/api/v1/projects/{id}", savedProject.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bezeichnung").value("Test Project"))
        .andExpect(jsonPath("$.id").value(savedProject.getId()));
  }

  @Test
  void testGetProjectByIdNotFound() throws Exception {
    mockMvc.perform(get("/api/v1/projects/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  @Test
  void testAddEmployeeToProject() throws Exception {
    Project project = createTestProject("Test Project");
    Project savedProject = projectRepository.save(project);

    GetEmployeeDto employee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(employee);

    mockMvc.perform(post("/api/v1/projects/{projectId}/employees/{employeeId}",
            savedProject.getId(), 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bezeichnung").value("Test Project"));
  }

  @Test
  void testAddEmployeeWithMissingSkills() throws Exception {
    Project project = createTestProject("Test Project");
    project.setRequiredSkillIds(Arrays.asList(1L, 2L));
    Project savedProject = projectRepository.save(project);

    GetEmployeeDto employee = createMockEmployee(1L);
    employee.getSkillSet().clear();
    when(employeeServiceClient.getEmployeeById(1L)).thenReturn(employee);

    mockMvc.perform(post("/api/v1/projects/{projectId}/employees/{employeeId}",
            savedProject.getId(), 1L))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testDeleteProject() throws Exception {
    Project project = createTestProject("Test Project");
    Project savedProject = projectRepository.save(project);

    mockMvc.perform(delete("/api/v1/projects/{id}", savedProject.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/project/{id}", savedProject.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetAllEmployeesInProject() throws Exception {
    Project project = createTestProject("Test Project");
    Project savedProject = projectRepository.save(project);

    GetEmployeeDto employee = createMockEmployee(1L);
    when(employeeServiceClient.getEmployeeById(anyLong())).thenReturn(employee);

    mockMvc.perform(get("/api/v1/projects/{id}/employees", savedProject.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  public static Project createTestProject(String bezeichnung) {
    Project project = new Project();
    project.setBezeichnung(bezeichnung);
    project.setVerantwortlicherMitarbeiterId(1L);
    project.setKundenId(1L);
    project.setKundenansprechpartner("Test");
    project.setStartdatum(LocalDate.now());
    project.setGeplantesEnddatum(LocalDate.now().plusMonths(6));
    project.setRequiredSkillIds(Arrays.asList());
    return project;
  }

  public static GetEmployeeDto createMockEmployee(Long id) {
    GetEmployeeDto employee = new GetEmployeeDto();
    employee.setId(id);
    employee.setFirstName("Max");
    employee.setLastName("Mustermann");

    GetEmployeeDto.SkillSetDto skill = new GetEmployeeDto.SkillSetDto();
    skill.setId(1L);
    skill.setName("Java");
    employee.setSkillSet(new ArrayList<>(Arrays.asList(skill))); // ArrayList statt Arrays.asList()

    return employee;
  }
}