/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.Event;
import entity.RegisteredCustomer;
import error.NoResultException;
import java.util.Date;
import java.util.List;
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
public class EventSession implements EventSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createEvent(Event e, Long cId) {
        Customer c = em.find(Customer.class, cId);
        e.setOrganizer(c); // Set the customer as the organizer of the event
        c.getOrganisedEvents().add(e); // Add the event to the list of organised events for the customer
        em.persist(e); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Event getEvent(Long eId) throws NoResultException {
        Event event = em.find(Event.class, eId);
        if (event != null) {
            return event;
        } else {
            throw new NoResultException("Customer not found");
        } //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Event> getAllEvents(){
        String jpql = "SELECT e FROM Event e";

        // Create a typed query
        TypedQuery<Event> query = em.createQuery(jpql, Event.class);

        // Execute the query and return the result list
        return query.getResultList();
    }

    @Override
    public void deleteEvent(Long eId) throws NoResultException {
        Event event = getEvent(eId);
        Customer c = event.getOrganizer();
        c.getOrganisedEvents().remove(event);
        event.getAbsentCustomers().clear();
        event.getPresentCustomers().clear();
        for(RegisteredCustomer rc : event.getParticipants()){
            Customer cust = em.find(Customer.class, rc.getCustomerId());
            cust.getEvents().remove(event);
            em.remove(rc);
        }
        event.getParticipants().clear();
        
        em.remove(event); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Event> searchEvents(String title) {
        Query q;
        if (title != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.title) LIKE :title");
            q.setParameter("title", "%" + title.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }
        return q.getResultList(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Event> searchEventsByCat(String cat) {
        Query q;
        if (cat != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.category) LIKE :category");
            q.setParameter("category", "%" + cat.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }
        return q.getResultList(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Event> searchEventsByDate(Date date) {
        if (date == null) {
            return em.createQuery("SELECT e FROM Event e", Event.class)
                    .getResultList();
        } else {
            return em.createQuery("SELECT e FROM Event e WHERE e.date = :date", Event.class)
                    .setParameter("date", date)
                    .getResultList();
        } //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Event> searchEventsByLocation(String location) {
        Query q;
        if (location != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.location) LIKE :location");
            q.setParameter("location", "%" + location.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }
        return q.getResultList(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @Override public void registerEvent(Long cId, Long eId) throws
     * NoResultException{ Event event = em.find(Event.class, eId); Customer
     * customer = em.find(Customer.class, cId); if
     * (!event.getCustomers().contains(customer)) {
     * event.getCustomers().add(customer); customer.getEvents().add(event); }
     * else { throw new NoResultException("Customer already registered"); } }
     *
     * @Override public void unRegisterEvent(Long cId, Long eId) throws
     * NoResultException{ Event event = em.find(Event.class, eId); Customer
     * customer = em.find(Customer.class, cId); if
     * (event.getCustomers().contains(customer)) {
     * event.getCustomers().remove(customer);
     * customer.getEvents().remove(event); } else { throw new
     * NoResultException("Customer already not in event"); } }
     *
     * @Override public void markPresent(Long cId, Long eId) throws
     * NoResultException{ Event event = em.find(Event.class, eId); Customer
     * customer = em.find(Customer.class, cId); if
     * (event.getCustomers().contains(customer)) {
     * event.getPresentCustomers().add(customer); } else { throw new
     * NoResultException("Customer not registered for this event"); }
     * if(event.getAbsentCustomers().contains(customer)){
     * event.getAbsentCustomers().remove(customer); } }
     *
     * @Override public void markAbsent(Long cId, Long eId) throws
     * NoResultException{ Event event = em.find(Event.class, eId); Customer
     * customer = em.find(Customer.class, cId); if
     * (event.getCustomers().contains(customer)) {
     * event.getAbsentCustomers().add(customer); } else { throw new
     * NoResultException("Customer not registered for this event"); }
     * if(event.getPresentCustomers().contains(customer)){
     * event.getPresentCustomers().remove(customer); }
    }*
     */
}
