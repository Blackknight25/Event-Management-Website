/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Event;
import error.NoResultException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author choijiwon
 */
@Local
public interface EventSessionLocal {
    public void createEvent(Event e, Long cId);
    
    public Event getEvent(Long eId) throws NoResultException;
    
    public void deleteEvent(Long eId) throws NoResultException;
    
    public List<Event> searchEvents(String title);
    
    public List<Event> searchEventsByDate(Date date);
    
    public List<Event> searchEventsByLocation(String location);

    public List<Event> getAllEvents();

    public List<Event> searchEventsByCat(String cat);

    /**public void markPresent(Long cId, Long eId) throws NoResultException;

    public void markAbsent(Long cId, Long eId) throws NoResultException;**/
    
}
