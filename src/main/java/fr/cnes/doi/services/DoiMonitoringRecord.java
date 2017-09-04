package fr.cnes.doi.services;

import fr.cnes.doi.utils.Requirement;

/**
 * Monitoring record containing:
 * <ul>
 * <li>Description of the record</li>
 * <li>Average</li>
 * <li>Nb total of recorded values (to compute the average)</li>
 * </ul>
 *
 * @author Claire
 *
 */
@Requirement(
        reqId = "DOI_SRV_130",
        reqName = "Monitoring des temps de réponse"
)
public class DoiMonitoringRecord {

    /**
     * Description (name) of the service to record *
     */
    private String description;

    /**
     * Current average time to access this service *
     */
    private float average = 0.0f;

    /**
     * Nb of values to compute the average *
     */
    private int nbAccess = 0;

    /**
     * Constructor.
     *
     * @param description Service description
     * @param average Average speed of the request
     * @param nbAccess Number of access
     */
    public DoiMonitoringRecord(final String description, float average, int nbAccess) {
        super();
        this.description = description;
        this.average = average;
        this.nbAccess = nbAccess;
    }

    /**
     * Returns the service description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the service description.
     *
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the average speed of the request.
     *
     * @return the average
     */
    public float getAverage() {
        return average;
    }

    /**
     * Sets the average speed of the request.
     *
     * @param average the average to set
     */
    public void setAverage(float average) {
        this.average = average;
    }

    /**
     * Returns the number of access.
     *
     * @return the nbAccess
     */
    public int getNbAccess() {
        return nbAccess;
    }

    /**
     * Sets the number of access.
     *
     * @param nbAccess the nbAccess to set
     */
    public void setNbAccess(int nbAccess) {
        this.nbAccess = nbAccess;
    }

}