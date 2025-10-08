package de.szut.lf8_starter.controller;

import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projektverwaltung", description = "API zur Verwaltung von Projekten")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }



  @Operation(summary = "Erstellt ein neues Projekt")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Projekt erfolgreich erstellt"),
      @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten")
  })
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
  
  @Operation(summary = "Löschen von einem Projekt")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Projekt wurde ohne Fehler gelöscht"),
          @ApiResponse(responseCode = "404", description = "Projekt wurde nicht gefunden")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(code=HttpStatus.NO_CONTENT)
  public void deleteProjectById(@PathVariable final Long id){
    this.projectService.delete(id);
  }

  @Operation(summary = "Gibt alle Projekte zurück")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Liste aller Projekte")
  })
  @GetMapping
  public ResponseEntity<List<GetProjectDto>> getAllProjects() {
    return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects());
  }

  @Operation(summary = "Gibt ein einzelnes Projekt zurück")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ein einzelnes Projekt")
  })
  @GetMapping(value = "/{id}")
  public ResponseEntity<GetProjectDto> getProjectById(@PathVariable long id) {
    return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectById(id));
  }

}
