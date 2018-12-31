package com.fitnessapp.service;

import com.fitnessapp.domain.entities.User;
import com.fitnessapp.domain.models.CardCreateBindingModel;
import com.fitnessapp.domain.models.ClientRegisterBindingModel;
import com.fitnessapp.domain.models.UserRegisterBindingModel;
import com.fitnessapp.domain.models.UserViewModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.List;

public interface UserService extends UserDetailsService {

    void registerAdmin();

    boolean registerClient(ClientRegisterBindingModel bindingModel, Principal principal);

    boolean registerInstructor(UserRegisterBindingModel bindingModel, Principal principal);

    boolean addCard(Integer userId, CardCreateBindingModel bindingModel, Principal principal);

    User findById(Integer id);

    User findByUsername(String username);

    List<User> findAll();

    List<UserViewModel> allUserModels();

    UserViewModel getClientDetails(Integer id);
}
