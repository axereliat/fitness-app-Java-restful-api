package com.fitnessapp.web.controllers;

import com.fitnessapp.domain.models.CardCreateBindingModel;
import com.fitnessapp.domain.models.ClientRegisterBindingModel;
import com.fitnessapp.domain.models.UserRegisterBindingModel;
import com.fitnessapp.domain.models.UserViewModel;
import com.fitnessapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = WebConstants.ALLOWED_PORT)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/registerAdmin", produces = "application/json")
    public Map<String, String> registerAdmin() {
        Map<String, String> map = new HashMap<>();
        map.put("message", "success");

        this.userService.registerAdmin();

        return map;
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/registerInstructor", produces = "application/json")
    public Map<String, String> registerInstructor(@RequestBody UserRegisterBindingModel bindingModel, Principal principal) {
        Map<String, String> map = new HashMap<>();

        if (this.userService.findByUsername(bindingModel.getUsername()) != null) {
            map.put("message", "Username is already taken.");
            return map;
        }

        if (this.userService.registerInstructor(bindingModel, principal)) {
            map.put("message", "success");
            return map;
        } else {
            map.put("message", "You are not admin.");
            return map;
        }
    }

    //@PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping(value = "/registerClient", produces = "application/json")
    public Map<String, String> registerClient(@RequestBody ClientRegisterBindingModel bindingModel, Principal principal) {
        Map<String, String> map = new HashMap<>();

        if (this.userService.findByUsername(bindingModel.getUsername()) != null) {
            map.put("message", "Username is already taken.");
            return map;
        }

        if (this.userService.registerClient(bindingModel, principal)) {
            map.put("message", "success");
            return map;
        } else {
            map.put("message", "You are not instructor.");
            return map;
        }
    }

    @PostMapping(value = "/addCard", produces = "application/json")
    public Map<String, String> addCard(@RequestBody CardCreateBindingModel bindingModel, @RequestParam Integer userId, Principal principal) {
        Map<String, String> map = new HashMap<>();

        map.put("message", "success");

        if (!this.userService.addCard(userId, bindingModel, principal)) {
            map.put("message", "You are not instructor!");
        }

        return map;
    }

    @GetMapping(value = "/allUsers", produces = "application/json")
    public List<UserViewModel> getAllUsers() {
        return this.userService.allUserModels();
    }

    @GetMapping(value = "/client/{id}", produces = "application/json")
    public UserViewModel getAllUsers(@PathVariable Integer id) {
        return this.userService.getClientDetails(id);
    }
}
