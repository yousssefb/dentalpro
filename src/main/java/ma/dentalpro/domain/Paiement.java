
package ma.dentalpro.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paiement {

    private Long id;
    private Long factureId;
    private Long auteurId;
    private UUID reference;
    private LocalDateTime datePaiement;
    private BigDecimal montant;
    private ModePaiement mode;
}
