package model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "services")
public class Services {

    @Id
    @Column(length = 36, nullable = false)
    private String servicesId;

    @Column(nullable = false, length = 255)
    private String nameServices;

    @Lob
    private String descriptionServices;

    private int priceServices;

    private LocalTime duration;

    // Getters and Setters

    public String getServicesId() {
        return servicesId;
    }

    public void setServicesId(String servicesId) {
        this.servicesId = servicesId;
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

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    // Constructor (No-args)
    public Services() {
    }
}