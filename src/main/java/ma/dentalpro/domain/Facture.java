
package ma.dentalpro.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facture {

    private Long id;
    private Long consultationId;
    private String numero;
    private LocalDateTime dateFacture;
    private BigDecimal total;
    private StatutFacture statut;
    private String motifAnnulation;
}
