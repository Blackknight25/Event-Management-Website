/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedBean;

import entity.Event;
import entity.RegisteredCustomer;
import error.NoResultException;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import session.CustomerSessionLocal;
import session.EventSessionLocal;

/**
 *
 * @author choijiwon
 */
@Named(value = "eventManagedBean")
@ViewScoped
public class EventManagedBean implements Serializable {

    @EJB
    private EventSessionLocal eventSessionLocal;

    private String selectedField;
    private String searchValue;
    private List<Event> searchResults;

    private Event currentEvent;

    /**private List<RegisteredCustomer> presentList = new ArrayList<>();
    private List<RegisteredCustomer> absentList = new ArrayList<>();**/

    @EJB
    private CustomerSessionLocal customerSessionLocal;

    /**
     * Creates a new instance of EventManagedBean
     */
    public EventManagedBean() {
    }

    public void loadSelectedEvent() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        try {
            long eventId = (Long) session.getAttribute("eventId");
            this.currentEvent
                    = eventSessionLocal.getEvent(eventId);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load event"));
        }
    }

    public void viewEventDetails(Long eventId) throws NoResultException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();

        session.setAttribute("eventId", eventId);
    }

    public String deleteEvent(Long eventId) throws NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        Event e = eventSessionLocal.getEvent(eventId);
        LocalDate currentDate = LocalDate.now();
        
        // Convert LocalDate to Date
        Date date = Date.valueOf(currentDate);
        
        if(date.compareTo(e.getDate()) < 0){
            eventSessionLocal.deleteEvent(e.getId());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event successfully deleted.", null);
            context.addMessage(null, message);
            
        }else{
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Unable to delete event on/after event date.", null);
            context.addMessage(null, message);
            return null;
            
        }
        return null;
        
    }

    public List<Event> getAllEvents() {
        return eventSessionLocal.getAllEvents();
    }

    public String getSelectedField() {
        return selectedField;
    }

    public void setSelectedField(String selectedField) {
        this.selectedField = selectedField;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public List<Event> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Event> searchResults) {
        this.searchResults = searchResults;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    /**public List<RegisteredCustomer> getPresentList() {
        return presentList;
    }

    public void setPresentList(List<RegisteredCustomer> presentList) {
        this.presentList = presentList;
    }

    public List<RegisteredCustomer> getAbsentList() {
        return absentList;
    }

    public void setAbsentList(List<RegisteredCustomer> absentList) {
        this.absentList = absentList;
    }**/

    public String markPresent(Long eventId, List<RegisteredCustomer> list) throws NoResultException {
        Event event = eventSessionLocal.getEvent(eventId);
        for (RegisteredCustomer rc : list) {
            if(rc.isPresent()==true){
                customerSessionLocal.markPresent(event.getId(), rc.getId());
                
                loadSelectedEvent();
            }
        }
        return null;
    }

    public String markAbsent(Long eventId, List<RegisteredCustomer> list) throws NoResultException {
        Event event = eventSessionLocal.getEvent(eventId);
        for (RegisteredCustomer rc : list) {
            if(rc.isAbsent()==true){
                customerSessionLocal.markAbsent(event.getId(), rc.getId());
                
                loadSelectedEvent();
            }
        }
        return null;
    }

}
