package de.szut.lf8_starter.dtos.create;

import de.szut.lf8_starter.validation.ValidCustomer;
import de.szut.lf8_starter.validation.ValidEmployee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CreateProjectDto {

  @NotBlank(message = "Bezeichnung is required")
  private String bezeichnung;

  @NotNull(message = "Verantwortlicher Mitarbeiter ID is required")
  @ValidEmployee
  private Long verantwortlicherMitarbeiterId;

  @NotNull(message = "Kunden ID is required")
  @ValidCustomer
  private Long kundenId;

  @NotBlank(message = "Kundenansprechpartner is required")
  private String kundenansprechpartner;

  private String kommentar;

  @NotNull(message = "Startdatum is required")
  private LocalDate startdatum;

  private LocalDate geplantesEnddatum;

  private List<Long> requiredSkillIds = new ArrayList<>();

}
