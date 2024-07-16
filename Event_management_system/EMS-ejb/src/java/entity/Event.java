/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author choijiwon
 */
@Entity
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Temporal(TemporalType.DATE)
    private Date date;
    private String location;
    private int capacity;
    private String description;
    private String category;
    @Temporal(TemporalType.DATE)
    private Date deadline;
    @Temporal(TemporalType.TIME)
    private Date time;
    @Temporal(TemporalType.TIME)
    private Date endTime;
    
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Customer organizer;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "EVENT_PARTICIPANTS")
    private List<RegisteredCustomer> participants;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "EVENT_PRESENT_CUSTOMERS")
    private List<RegisteredCustomer> presentCustomers;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "EVENT_ABSENT_CUSTOMERS")
    private List<RegisteredCustomer> absentCustomers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Event[ id=" + id + " ]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Customer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Customer organizer) {
        this.organizer = organizer;
    }

    public List<RegisteredCustomer> getParticipants() {
        return participants;
    }

    public void setParticipants(List<RegisteredCustomer> participants) {
        this.participants = participants;
    }

    public List<RegisteredCustomer> getPresentCustomers() {
        return presentCustomers;
    }

    public void setPresentCustomers(List<RegisteredCustomer> presentCustomers) {
        this.presentCustomers = presentCustomers;
    }

    public List<RegisteredCustomer> getAbsentCustomers() {
        return absentCustomers;
    }

    public void setAbsentCustomers(List<RegisteredCustomer> absentCustomers) {
        this.absentCustomers = absentCustomers;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
