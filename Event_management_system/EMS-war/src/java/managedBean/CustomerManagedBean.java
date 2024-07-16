/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedBean;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.jboss.weld.context.RequestContext;
import session.CustomerSessionLocal;
import session.EventSessionLocal;

/**
 *
 * @author choijiwon
 */
@Named(value = "customerManagedBean")
@ViewScoped
public class CustomerManagedBean implements Serializable {

    @EJB
    private EventSessionLocal eventSessionLocal;

    @EJB
    private CustomerSessionLocal customerSessionLocal;

    private String username;

    //retrieve customer
    private Customer selectedCustomer;

    private Long id;
    private String name;
    private String contact;
    //private String photoUrl;
    private String email;
    private String password;
    //private byte[] profilePicture;
    //private UploadedFile uploadedFile;
    //private boolean present;

    private Long eventId;
    private String title;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Temporal(TemporalType.TIME)
    private Date time;
    @Temporal(TemporalType.TIME)
    private Date endTime;
    private String location;
    private int capacity;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date deadline;
    private String category;

    private String selectedField;
    private String searchValue;
    private List<Event> searchResults;

    private Part uploadedfile;
    private String filename = "";

    /**
     * Creates a new instance of CustomerManagedBean
     */
    public CustomerManagedBean() {
        //searchResults = searchEventsByTitle(null);
    }

    public String addCustomer(ActionEvent evt) throws IOException {
        Customer c = new Customer();
        if (!customerSessionLocal.getAllEmails().contains(email)) {
            c.setName(name);
            c.setContact(contact);
            //profilePicture = upload();
            //c.setFileName(fileNamePicture);
            c.setEmail(email);
            c.setPassword(password);
            customerSessionLocal.createCustomer(c);
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            Long userId = c.getId();
            session.setAttribute("userId", userId);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Created new customer", null));
            return "/event/addPhoto.xhtml?faces-redirect=true";
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Customer already exists", null);
            context.addMessage(null, message);
            return null;
        }

    }

    public String upload(Long cId) throws IOException, NoResultException {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        FacesContext context = FacesContext.getCurrentInstance();

        //get the deployment path
        String UPLOAD_DIRECTORY = ctx.getRealPath("/") + "upload/";
        System.out.println("#UPLOAD_DIRECTORY : " + UPLOAD_DIRECTORY);

        if (uploadedfile != null) {
            setFilename(Paths.get(uploadedfile.getSubmittedFileName()).getFileName().toString());
            System.out.println("filename: " + getFilename());
            //---------------------
            customerSessionLocal.setProfilePicFile(cId, getFilename());
            //replace existing file
            Path path = Paths.get(UPLOAD_DIRECTORY + getFilename());
            InputStream bytes = uploadedfile.getInputStream();
            Files.copy(bytes, path, StandardCopyOption.REPLACE_EXISTING);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please upload a valid file", null);
            context.addMessage(null, message);
        }
        return null;
        //debug purposes

    }

    public String addEvent() throws NoResultException, ParseException {
        LocalDate currentDate = LocalDate.now();

        // Convert LocalDate to Date
        Date nowdate = java.sql.Date.valueOf(currentDate);
        FacesContext context = FacesContext.getCurrentInstance();
        Event e = new Event();
        if (deadline != null && date != null && deadline.after(date)) {
            System.out.println("deadline is after date");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Registration deadline must be before the event date.", null);
            context.addMessage(null, message);
            return null;
        } else if (nowdate.compareTo(date) >= 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please choose an event date after today's date", null);
            context.addMessage(null, message);
            return null;
        } else if (nowdate.compareTo(deadline) > 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please choose an event deadline on/after today's date", null);
            context.addMessage(null, message);
            return null;
        } else if (endTime.before(time)) {
            System.out.println("endtime is before startime");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "End time must be after the start time.", null);
            context.addMessage(null, message);
            return null;

        } else if (capacity <= 0) {
            System.out.println("capacity error");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Capacity should be more than 0.", null);
            context.addMessage(null, message);
            return null;

        } else {
            // Only assign the deadline if it passes the validation check
            e.setTitle(title);
            e.setCategory(category);
            e.setDate(date);
            e.setTime(time);
            e.setCapacity(capacity);
            e.setEndTime(endTime);
            e.setLocation(location);
            e.setDescription(description);
            e.setDeadline(deadline); // Set the deadline for the event
            eventSessionLocal.createEvent(e, selectedCustomer.getId());

        }
        return "/event/listEvents.xhtml?faces-redirect=true";

    }

    public String login() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        FacesContext context = FacesContext.getCurrentInstance();
        if (customerSessionLocal.getAllEmails().contains(username)) {
            //login successful
            Customer cust = customerSessionLocal.getCustByEmail(username);
            if (password.equals(cust.getPassword())) {
                Long userId = cust.getId();
                session.setAttribute("userId", userId);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully login", null));
                return "/event/about.xhtml?faces-redirect=true";

            } else {
                //Long userId = new Long(-1);
                //session.setAttribute("userId", userId);
                username = null;
                password = null;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Wrong username/password.", null);
                context.addMessage(null, message);
                return null;
            }

        } else {
            //Long userId = new Long(-1);
            //session.setAttribute("userId", userId);
            username = null;
            password = null;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Wrong username/password.", null);
            context.addMessage(null, message);
            return null;
        }
    }

    public String logout() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        Long userId = new Long(-1);
        session.setAttribute("userId", userId);
        return "/index.xhtml?faces-redirect=true";
    }

    public String editCustomer() throws NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        Customer c = selectedCustomer;
        if (!(name.length() > 0)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Name cannot be empty.", null);
            context.addMessage(null, message);
            return null;
        } else if (!(contact.length() == 8)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Contact cannot be empty.", null);
            context.addMessage(null, message);
            return null;
        } else if (password.length() > 0 && password.length() < 8) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Password of at least 8 characters.", null);
            context.addMessage(null, message);
            return null;

        } else {
            c.setName(name);
            c.setContact(contact);
            //profilePicture = upload();
            //c.setProfilePicture(profilePicture);
            if (password.length() == 0) {
                password = selectedCustomer.getPassword();
            }
            c.setPassword(password);
            customerSessionLocal.updateCustomer(c);
        }
        return "/event/viewProfile.xhtml?faces-redirect=true";
    }

    public void searchEvents() throws ParseException {
        init();
    }

    @PostConstruct
    public void init() {
        List<Event> results = new ArrayList<>();
        if (searchValue == null || searchValue.equals("")) {
            results = searchEventsByTitle(null);
            Collections.sort(results, Comparator.comparing(Event::getDate).reversed());
        } else {
            // Implement your search logic here based on the selected field
            // Example search logic
            if ("TITLE".equals(selectedField)) {
                // Search events by title
                results = searchEventsByTitle(searchValue);
                Collections.sort(results, Comparator.comparing(Event::getDate).reversed());
            } else if ("DATE".equals(selectedField)) {
                try {
                    // Search events by date
                    results = searchEventsByDate(searchValue);
                    Collections.sort(results, Comparator.comparing(Event::getDate).reversed());
                } catch (ParseException ex) {
                    System.out.println("Error occurred while parsing date: " + ex.getMessage());
                }
            } else if ("CATEGORY".equals(selectedField)) {

                results = searchEventsByCategory(searchValue);
                Collections.sort(results, Comparator.comparing(Event::getDate).reversed());

            } else if ("LOCATION".equals(selectedField)) {
                // Search events by location
                results = searchEventsByLocation(searchValue);
            }
            Collections.sort(results, Comparator.comparing(Event::getDate).reversed());
            // Do something with the search results, e.g., display them in the view
        }
        searchResults = results;
    }

    public List<Event> searchEventsByTitle(String title) {
        return eventSessionLocal.searchEvents(title);
    }

    public List<Event> searchEventsByCategory(String cat) {
        return eventSessionLocal.searchEventsByCat(cat);
    }

    public List<Event> searchEventsByDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date dated = dateFormat.parse(date);
        return eventSessionLocal.searchEventsByDate(dated);
    }

    public List<Event> searchEventsByLocation(String location) {
        return eventSessionLocal.searchEventsByLocation(location);
    }

    public void loadSelectedCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        try {
            long userId = (Long) session.getAttribute("userId");
            this.selectedCustomer
                    = customerSessionLocal.getCustomer(userId);
            id = this.selectedCustomer.getId();
            name = this.selectedCustomer.getName();
            contact = this.selectedCustomer.getContact();
            filename = this.selectedCustomer.getFileName();
            email = this.selectedCustomer.getEmail();
            password = this.selectedCustomer.getPassword();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    }

    public String registerEvent(Event e) throws NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        LocalDate currentDate = LocalDate.now();

        // Convert LocalDate to Date
        Date nowdate = java.sql.Date.valueOf(currentDate);
        Customer cust = customerSessionLocal.getCustomer(selectedCustomer.getId());
        Event event = eventSessionLocal.getEvent(e.getId());
        if (cust.getEvents().contains(event)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Customer is already registered", null);
            context.addMessage(null, message);
            return null;

        } else if (nowdate.compareTo(event.getDeadline()) > 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Registration deadline is over", null);
            context.addMessage(null, message);
            return null;
        } else if (event.getParticipants().size() == event.getCapacity()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Event is full", null);
            context.addMessage(null, message);
            return null;
        } else {
            customerSessionLocal.registerEvent(event.getId(), cust.getId());
            return "/event/searchEvents.xhtml?faces-redirect=true";
        }

    }

    public String unregisterEvent(Event e, Customer c) throws NoResultException {
        LocalDate currentDate = LocalDate.now();

        // Convert LocalDate to Date
        Date nowdate = java.sql.Date.valueOf(currentDate);
        FacesContext context = FacesContext.getCurrentInstance();
        Event event = eventSessionLocal.getEvent(e.getId());
        if (nowdate.compareTo(event.getDate()) >= 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Cannot unregister on/after event date", null);
            context.addMessage(null, message);
            return null;

        } else {
            customerSessionLocal.unregisterEvent(event.getId(), c.getId());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Unregistered from event", null);
            context.addMessage(null, message);
        }
        return null;

    }

    public void order(List<Event> eventList) {
        Collections.sort(eventList, Comparator.comparing(Event::getDate).reversed());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * public String getPhotoUrl() { return photoUrl; }
     *
     * public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }*
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * public UploadedFile getUploadedFile() { return uploadedFile; }
     *
     * public void setUploadedFile(UploadedFile uploadedFile) {
     * this.uploadedFile = uploadedFile; }*
     */
    /**
     * public void upload(FileUploadEvent event) throws IOException {
     * uploadedFile = event.getFile(); byte[] fileContent =
     * readUploadedFile(uploadedFile); profilePicture = fileContent; // Set the
     * byte array to the profilePicture field of the Customer entity // Save or
     * update the Customer entity in the database }
     *
     * private byte[] readUploadedFile(UploadedFile file) throws IOException {
     * try (InputStream inputStream = file.getInputStream()) { byte[] buffer =
     * new byte[(int) file.getSize()]; int bytesRead = inputStream.read(buffer);
     * if (bytesRead != -1) { return buffer; } else { throw new
     * IOException("Failed to read uploaded file."); } } }
     *
     * public String getProfilePictureAsBase64() { if (profilePicture != null) {
     * return Base64.getEncoder().encodeToString(profilePicture); } return null;
     * }
     *
     * public byte[] getProfilePicture() { return profilePicture; }
     *
     * public void setProfilePicture(byte[] profilePicture) {
     * this.profilePicture = profilePicture; }
     *
     * public String getUrl() { return url; }
     *
     * public void setUrl(String url) { this.url = url; }*
     */
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * public Part getUploadedfile() { return uploadedfile; }
     *
     * public void setUploadedfile(Part uploadedfile) { this.uploadedfile =
     * uploadedfile; }
     *
     * public String getFilename() { return filename; }
     *
     * public void setFilename(String filename) { this.filename = filename; }
     *
     * public String viewProfile(Long cId){ return
     * "/secret/customerView.xhtml?cId=" + cId; }*
     */
    public Part getUploadedfile() {
        return uploadedfile;
    }

    public void setUploadedfile(Part uploadedfile) {
        this.uploadedfile = uploadedfile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
