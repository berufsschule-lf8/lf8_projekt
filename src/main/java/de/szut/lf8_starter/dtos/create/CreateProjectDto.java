package de.szut.lf8_starter.dtos.create;

import de.szut.lf8_starter.validation.ValidCustomer;
import de.szut.lf8_starter.validation.ValidEmployee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateProjectDto {

  @NotBlank(message = "Bezeichnung ist erforderlich")
  private String bezeichnung;

  @NotNull(message = "Verantwortlicher Mitarbeiter ID ist erforderlich")
  @ValidEmployee
  private Long verantwortlicherMitarbeiterId;

  @NotNull(message = "Kunden ID ist erforderlich")
  @ValidCustomer
  private Long kundenId;

  @NotBlank(message = "Kundenansprechpartner ist erforderlich")
  private String kundenansprechpartner;

  private String kommentar;

  @NotNull(message = "Startdatum ist erforderlich")
  private LocalDate startdatum;

  private LocalDate geplantesEnddatum;

}
