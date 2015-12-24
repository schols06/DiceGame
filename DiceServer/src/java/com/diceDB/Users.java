/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diceDB;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

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
    @Size(min = 1, max = 16)
    @Column(name = "ANDROID_ID")
    private String androidId;
    @Size(max = 30)
    @Column(name = "NAME")
    private String name;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "users1")
    private Users users;
    @JoinColumn(name = "ANDROID_ID", referencedColumnName = "ANDROID_ID", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users users1;

    public Users() {
    }

    public Users(String androidId) {
        this.androidId = androidId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Users getUsers1() {
        return users1;
    }

    public void setUsers1(Users users1) {
        this.users1 = users1;
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
        return "com.diceDB.Users[ androidId=" + androidId + " ]";
    }
    
}
