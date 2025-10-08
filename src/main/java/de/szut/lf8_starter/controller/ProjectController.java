package de.szut.lf8_starter.controller;

import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }


  @PostMapping
  public ResponseEntity<GetProjectDto> createProject(@Valid @RequestBody CreateProjectDto createProjectDto) {
    GetProjectDto createProject = projectService.createProject(createProjectDto);
    return new ResponseEntity<>(createProject, HttpStatus.CREATED);
  }


    @Operation(summary = "Fügt einen Mitarbeiter anhand der ID zu einem bestimmten Projekt hinzu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter wurde erfolgreich dem Projekt hinzugefügt."),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten!")
    })
  @PostMapping("/{projectId}/employees/{employeeId}")
  public ResponseEntity<GetProjectDto> addEmployeeToProject(@PathVariable Long projectId, @PathVariable Long employeeId) {
      GetProjectDto updatedProject = projectService.addEmployeeToProject(projectId, employeeId);
      return new ResponseEntity<>(updatedProject, HttpStatus.OK);
  }

}
