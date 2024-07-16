/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author choijiwon
 */
@Local
public interface CustomerSessionLocal {
    public void createCustomer(Customer c);
    
    public void updateCustomer(Customer c) throws NoResultException;
    
    public Customer getCustomer(Long cId) throws NoResultException;

    public List<Event> getAllEvents(Long cId);

    public List<String> getAllEmails();

    public Customer getCustByEmail(String email);

    public void registerEvent(Long eId, Long cId);

    public void unregisterEvent(Long eId, Long cId);

    public void markPresent(Long eId, Long cId);

    public void markAbsent(Long eId, Long cId);

    public void setProfilePicFile(Long cId, String fileName);
    
}
