package com.fitnessapp.domain.models;

import java.util.List;

public class UserViewModel {

    private Integer id;

    private String username;

    private List<String> roles;

    private List<CardViewModel> cards;

    private Integer registeredById;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<CardViewModel> getCards() {
        return cards;
    }

    public void setCards(List<CardViewModel> cards) {
        this.cards = cards;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Integer getRegisteredById() {
        return registeredById;
    }

    public void setRegisteredById(Integer registeredById) {
        this.registeredById = registeredById;
    }
}
