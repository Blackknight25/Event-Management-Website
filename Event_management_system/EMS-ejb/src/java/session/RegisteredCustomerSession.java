/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.RegisteredCustomer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author choijiwon
 */
@Stateless
public class RegisteredCustomerSession implements RegisteredCustomerSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RegisteredCustomer createRegisteredCustomer(Customer c) {
        RegisteredCustomer rc = new RegisteredCustomer();
        rc.setCustomerId(c.getId());
        rc.setName(c.getName());
        rc.setContact(c.getContact());
        rc.setEmail(c.getEmail());
        rc.setPresent(false);
        rc.setAbsent(true);
        em.persist(rc);
        return rc;
    }
    
    @Override
    public void deleteRegisteredCustomer(RegisteredCustomer rc){
        em.remove(rc);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
