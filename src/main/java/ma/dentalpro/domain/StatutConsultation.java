package ma.dentalpro.domain;

/**
 * Statut d'une consultation clinique (table consultation).
 *
 * <p>Conforme à la décision D-08 du projet :
 * <ul>
 *   <li>BROUILLON : consultation en cours de saisie par le praticien ;</li>
 *   <li>VALIDEE : consultation validée et clôturée (déclenche la facturation).</li>
 * </ul>
 * </p>
 */
public enum StatutConsultation {
    BROUILLON,
    VALIDEE
}
