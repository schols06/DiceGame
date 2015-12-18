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
@Table(name = "USERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByAndroidId", query = "SELECT u FROM Users u WHERE u.androidId = :androidId"),
    @NamedQuery(name = "Users.findByName", query = "SELECT u FROM Users u WHERE u.name = :name")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ANDROID_ID")
    private Integer androidId;
    @Column(name = "NAME")
    private Character name;
    @OneToMany(mappedBy = "androidId")
    private Collection<Scores> scoresCollection;

    public Users() {
    }

    public Users(Integer androidId) {
        this.androidId = androidId;
    }

    public Integer getAndroidId() {
        return androidId;
    }

    public void setAndroidId(Integer androidId) {
        this.androidId = androidId;
    }

    public Character getName() {
        return name;
    }

    public void setName(Character name) {
        this.name = name;
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
        hash += (androidId != null ? androidId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.androidId == null && other.androidId != null) || (this.androidId != null && !this.androidId.equals(other.androidId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.playerentity.Users[ androidId=" + androidId + " ]";
    }
    
}
