/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playerentity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Tim
 */
@Entity
@Table(name = "LOCATIONS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Locations.findAll", query = "SELECT l FROM Locations l"),
    @NamedQuery(name = "Locations.findByLocationId", query = "SELECT l FROM Locations l WHERE l.locationId = :locationId"),
    @NamedQuery(name = "Locations.findByName", query = "SELECT l FROM Locations l WHERE l.name = :name"),
    @NamedQuery(name = "Locations.findByLattitude", query = "SELECT l FROM Locations l WHERE l.lattitude = :lattitude"),
    @NamedQuery(name = "Locations.findByLongitude", query = "SELECT l FROM Locations l WHERE l.longitude = :longitude")})
public class Locations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "LOCATION_ID")
    private Integer locationId;
    @Column(name = "NAME")
    private Character name;
    @Column(name = "LATTITUDE")
    private Character lattitude;
    @Column(name = "LONGITUDE")
    private Character longitude;
    @OneToMany(mappedBy = "locationId")
    private Collection<Scores> scoresCollection;

    public Locations() {
    }

    public Locations(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Character getName() {
        return name;
    }

    public void setName(Character name) {
        this.name = name;
    }

    public Character getLattitude() {
        return lattitude;
    }

    public void setLattitude(Character lattitude) {
        this.lattitude = lattitude;
    }

    public Character getLongitude() {
        return longitude;
    }

    public void setLongitude(Character longitude) {
        this.longitude = longitude;
    }

    @XmlTransient
    public Collection<Scores> getScoresCollection() {
        return scoresCollection;
    }

    public void setScoresCollection(Collection<Scores> scoresCollection) {
        this.scoresCollection = scoresCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (locationId != null ? locationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Locations)) {
            return false;
        }
        Locations other = (Locations) object;
        if ((this.locationId == null && other.locationId != null) || (this.locationId != null && !this.locationId.equals(other.locationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.playerentity.Locations[ locationId=" + locationId + " ]";
    }
    
}
