package de.szut.lf8_starter.controller;

import de.szut.lf8_starter.dtos.get.EmployeeProjectsDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.services.EmployeeProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Mitarbeiterverwaltung", description = "API zur Verwaltung von Mitarbeitern")
public class EmployeeController {
    private final EmployeeProjectService employeeProjectService;

    public EmployeeController(EmployeeProjectService employeeProjectService) {
        this.employeeProjectService = employeeProjectService;
    }

    @Operation(summary = "Gibt alle Projekte eines Mitarbeiters anhand seiner ID zurück.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alle Projekte des Mitarbeiters wurden erfolgreich abgerufen."),
            @ApiResponse(responseCode = "404", description = "Es konnten keine Projekte für den Mitarbeiter gefunden werden.")
    })
    @GetMapping("/{employeeId}/projects")
    public ResponseEntity<EmployeeProjectsDto> getAllProjectsByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeProjectService.getEmployeeProjects(employeeId));
    }
}
