package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ServiceBooking {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "services_id", nullable = false)
//    private ServiceSpa serviceSpa;
//
//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private MemberAccount memberAccount;
//
//    @ManyToOne
//    @JoinColumn(name = "therapist_id", nullable = false)
//    private SkinTherapist skinTherapist;
//
//    @ManyToOne
//    @JoinColumn(name = "staff_id")
//    private Staff staff;
//
//    @Column(name = "time_booking")
//    private LocalDateTime timeBooking;
//
//    @Column(name = "price_booking")
//    private int priceBooking;
//
//    @ElementCollection
//    @CollectionTable(name = "service_booking_status")
//    @Column(name = "status_service")
//    private List<String> statusService = new ArrayList<>();
//
//    // Default Constructor
//    public ServiceBooking() {
//    }
//
//    // Constructor with required fields
//    public ServiceBooking(ServiceSpa serviceSpa, MemberAccount memberAccount, SkinTherapist skinTherapist, LocalDateTime timeBooking, int priceBooking) {
//        this.serviceSpa = serviceSpa;
//        this.memberAccount = memberAccount;
//        this.skinTherapist = skinTherapist;
//        this.timeBooking = timeBooking;
//        this.priceBooking = priceBooking;
//    }
//
//    // Getters for related entity fields
//    public Long getServicesId() {
//        return serviceSpa != null ? serviceSpa.getId() : null;
//    }
//
//    public String getNameServices() {
//        return serviceSpa != null ? serviceSpa.getNameServices() : null;
//    }
//
//    public Long getMemberId() {
//        return memberAccount != null ? memberAccount.getId() : null;
//    }
//
//    public String getNameMember() {
//        return memberAccount != null ? memberAccount.getUserName() : null;
//    }
//
//    public Long getTherapistId() {
//        return skinTherapist != null ? skinTherapist.getId() : null;
//    }
//
//    public String getTherapistName() {
//        return skinTherapist != null ? skinTherapist.getTherapistName() : null;
//    }
//
//    public Long getStaffId() {
//        return staff != null ? staff.getId() : null;
//    }
//
//    public String getStaffName() {
//        return staff != null ? staff.getStaffName() : null;
//    }
//
//    // Getters and Setters for direct fields
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public ServiceSpa getServiceSpa() {
//        return serviceSpa;
//    }
//
//    public void setServiceSpa(ServiceSpa serviceSpa) {
//        this.serviceSpa = serviceSpa;
//    }
//
//    public MemberAccount getMemberAccount() {
//        return memberAccount;
//    }
//
//    public void setMemberAccount(MemberAccount memberAccount) {
//        this.memberAccount = memberAccount;
//    }
//
//    public SkinTherapist getSkinTherapist() {
//        return skinTherapist;
//    }
//
//    public void setSkinTherapist(SkinTherapist skinTherapist) {
//        this.skinTherapist = skinTherapist;
//    }
//
//    public Staff getStaff() {
//        return staff;
//    }
//
//    public void setStaff(Staff staff) {
//        this.staff = staff;
//    }
//
//    public LocalDateTime getTimeBooking() {
//        return timeBooking;
//    }
//
//    public void setTimeBooking(LocalDateTime timeBooking) {
//        this.timeBooking = timeBooking;
//    }
//
//    public int getPriceBooking() {
//        return priceBooking;
//    }
//
//    public void setPriceBooking(int priceBooking) {
//        this.priceBooking = priceBooking;
//    }
//
//    public List<String> getStatusService() {
//        return statusService;
//    }
//
//    public void setStatusService(List<String> statusService) {
//        this.statusService = statusService;
//    }
}