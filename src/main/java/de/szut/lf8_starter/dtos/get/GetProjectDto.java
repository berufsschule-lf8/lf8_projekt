package de.szut.lf8_starter.dtos.get;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GetProjectDto {

  private Long id;
  private String bezeichnung;
  private Long verantwortlicherMitarbeiterId;
  private Long kundenId;
  private String kundenansprechpartner;
  private String kommentar;
  private LocalDate startdatum;
  private LocalDate geplantesEnddatum;
  private LocalDate tatsaechlichesEnddatum;

}
