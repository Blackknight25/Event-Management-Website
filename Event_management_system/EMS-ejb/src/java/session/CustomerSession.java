/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.Event;
import entity.RegisteredCustomer;
import error.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author choijiwon
 */
@Stateless
public class CustomerSession implements CustomerSessionLocal {
    
    @EJB
    private RegisteredCustomerSessionLocal registeredCustomerSession;
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void createCustomer(Customer c) {
        em.persist(c);
    }
    
    @Override
    public void updateCustomer(Customer c) throws NoResultException {
        Customer oldC = getCustomer(c.getId());
        
        oldC.setName(c.getName());
        oldC.setContact(c.getContact());
        //oldC.setProfilePicture(c.getProfilePicture());
        oldC.setPassword(c.getPassword());
    }
    
    @Override
    public Customer getCustomer(Long cId) throws NoResultException {
        Customer cust = em.find(Customer.class, cId);
        if (cust != null) {
            return cust;
        } else {
            throw new NoResultException("Customer not found");
        }
    }
    
    @Override
    public void setProfilePicFile(Long cId, String fileName) {
        Customer cust = em.find(Customer.class, cId);
        cust.setFileName(fileName);
    }
    
    @Override
    public List<Event> getAllEvents(Long cId) {
        Customer cust = em.find(Customer.class, cId);
        return cust.getEvents();
    }
    
    @Override
    public List<String> getAllEmails() {
        List<String> customerEmailList = new ArrayList<>();
        try {
            Query query = em.createQuery("SELECT c.email FROM Customer c");
            List<String> customersEmail = query.getResultList();
            for (String email : customersEmail) {
                customerEmailList.add(email);
            }
        } catch (Exception e) {
            // Handle exception (e.g., log error, throw custom exception)
            e.printStackTrace();
        }
        return customerEmailList;
    }
    
    @Override
    public Customer getCustByEmail(String email) {
        
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
        query.setParameter("email", email);
        return (Customer) query.getSingleResult();
        
    }
    
    @Override
    public void registerEvent(Long eId, Long cId) {
        Customer c = em.find(Customer.class, cId);
        Event e = em.find(Event.class, eId);
        boolean contains = false;
        for (RegisteredCustomer rc : e.getParticipants()) {
            if (rc.getCustomerId().equals(c.getId())) {
                contains = true;
            }
        }
        if (contains == false) {
            c.getEvents().add(e);
            RegisteredCustomer rc = registeredCustomerSession.createRegisteredCustomer(c);
            e.getParticipants().add(rc);
            e.getAbsentCustomers().add(rc);
        }
    }
    
    @Override
    public void unregisterEvent(Long eId, Long cId) {
        Customer c = em.find(Customer.class, cId);
        Event e = em.find(Event.class, eId);
        c.getEvents().remove(e);
        List<RegisteredCustomer> participantsCopy = new ArrayList<>(e.getParticipants());
        for (RegisteredCustomer rc : participantsCopy) {
            if (rc.getCustomerId().equals(c.getId())) {
                e.getParticipants().remove(rc);
                if (e.getAbsentCustomers().contains(rc)) {
                    e.getAbsentCustomers().remove(rc);
                } else if (e.getPresentCustomers().contains(rc)) {
                    e.getPresentCustomers().remove(rc);
                }
                RegisteredCustomer rc2 = em.find(RegisteredCustomer.class, rc.getId());
                registeredCustomerSession.deleteRegisteredCustomer(rc2);
            }
        }
    }
    
    @Override
    public void markPresent(Long eId, Long rcId) {
        Event e = em.find(Event.class, eId);
        for (RegisteredCustomer rc : e.getParticipants()) {
            if (rc.getId().equals(rcId)) {
                RegisteredCustomer rc2 = em.find(RegisteredCustomer.class, rc.getId());
                rc2.setPresent(true);
                rc2.setAbsent(false);
                e.getPresentCustomers().add(rc2);
                e.getAbsentCustomers().remove(rc2);
            }
        }
    }
    
    @Override
    public void markAbsent(Long eId, Long rcId) {
        Event e = em.find(Event.class, eId);
        for (RegisteredCustomer rc : e.getParticipants()) {
            if (rc.getId().equals(rcId)) {
                RegisteredCustomer rc2 = em.find(RegisteredCustomer.class, rc.getId());
                rc2.setPresent(false);
                rc2.setAbsent(true);
                e.getPresentCustomers().remove(rc2);
                e.getAbsentCustomers().add(rc2);
            }
        }
    }
    
}
