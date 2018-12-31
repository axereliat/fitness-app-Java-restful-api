package com.fitnessapp.domain.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class Card {

    private Integer id;

    private User client;

    private LocalDate startDate;

    private LocalDate endDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
