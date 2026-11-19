package ma.dentalpro.domain;

/**
 * Niveau de risque médical associé à un antécédent dans le dossier d'un patient.
 *
 * <p>Conforme à la contrainte de la table dossier_antecedent et à la décision D-13 :
 * <ul>
 *   <li>FAIBLE : simple signalement (ex. tabagisme modéré)</li>
 *   <li>MOYEN : précaution requise (ex. diabète équilibré)</li>
 *   <li>ELEVE : alerte critique pour les soins (ex. allergie sévère à la pénicilline)</li>
 * </ul>
 * </p>
 */
public enum NiveauRisque {
    FAIBLE,
    MOYEN,
    ELEVE
}
