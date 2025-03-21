package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ServiceSpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)

    private Long id;

    @Column(name = "name_services")
    private String nameServices;

    @Column(name = "description_services")
    private String descriptionServices;

    @Column(name = "price_services")
    private int priceServices;

    @Column(name = "duration")
    private String duration;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updateAt;

    // Default Constructor
    public ServiceSpa() {
    }

    // Constructor with required fields
    public ServiceSpa(String nameServices, int priceServices) {
        this.nameServices = nameServices;
        this.priceServices = priceServices;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameServices() {
        return nameServices;
    }

    public void setNameServices(String nameServices) {
        this.nameServices = nameServices;
    }

    public String getDescriptionServices() {
        return descriptionServices;
    }

    public void setDescriptionServices(String descriptionServices) {
        this.descriptionServices = descriptionServices;
    }

    public int getPriceServices() {
        return priceServices;
    }

    public void setPriceServices(int priceServices) {
        this.priceServices = priceServices;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}