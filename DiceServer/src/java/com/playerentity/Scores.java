/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playerentity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Tim
 */
@Entity
@Table(name = "SCORES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Scores.findAll", query = "SELECT s FROM Scores s"),
    @NamedQuery(name = "Scores.findByScoreId", query = "SELECT s FROM Scores s WHERE s.scoreId = :scoreId"),
    @NamedQuery(name = "Scores.findByValue", query = "SELECT s FROM Scores s WHERE s.value = :value"),
    @NamedQuery(name = "Scores.findByTimestamp", query = "SELECT s FROM Scores s WHERE s.timestamp = :timestamp")})
public class Scores implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SCORE_ID")
    private Integer scoreId;
    @Column(name = "VALUE")
    private Integer value;
    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIME)
    private Date timestamp;
    @JoinColumn(name = "ANDROID_ID", referencedColumnName = "ANDROID_ID")
    @ManyToOne
    private Users androidId;
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID")
    @ManyToOne
    private Locations locationId;

    public Scores() {
    }

    public Scores(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Users getAndroidId() {
        return androidId;
    }

    public void setAndroidId(Users androidId) {
        this.androidId = androidId;
    }

    public Locations getLocationId() {
        return locationId;
    }

    public void setLocationId(Locations locationId) {
        this.locationId = locationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scoreId != null ? scoreId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Scores)) {
            return false;
        }
        Scores other = (Scores) object;
        if ((this.scoreId == null && other.scoreId != null) || (this.scoreId != null && !this.scoreId.equals(other.scoreId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.playerentity.Scores[ scoreId=" + scoreId + " ]";
    }
    
}
