package com.fitnessapp.service;

import com.fitnessapp.common.constants.RoleConstants;
import com.fitnessapp.domain.entities.Card;
import com.fitnessapp.domain.entities.Role;
import com.fitnessapp.domain.entities.User;
import com.fitnessapp.domain.models.*;
import com.fitnessapp.repository.CardRepository;
import com.fitnessapp.repository.RoleRepository;
import com.fitnessapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CardRepository cardRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CardRepository cardRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerAdmin() {
        if (this.roleRepository.findAll().size() == 0) {
            Role clientRole = new Role();
            clientRole.setAuthority(RoleConstants.CLIENT);
            Role adminRole = new Role();
            adminRole.setAuthority(RoleConstants.ADMIN);
            Role instructorRole = new Role();
            instructorRole.setAuthority(RoleConstants.INSTRUCTOR);

            this.roleRepository.save(clientRole);
            this.roleRepository.save(adminRole);
            this.roleRepository.save(instructorRole);
        }

        if (this.userRepository.findByUsername("admin") == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(this.passwordEncoder.encode("admin"));

            user.addRole(this.roleRepository.findByAuthority(RoleConstants.ADMIN));

            this.userRepository.saveAndFlush(user);
        }
    }

    @Override
    public boolean registerInstructor(UserRegisterBindingModel bindingModel, Principal principal) {
        User admin = this.userRepository.findByUsername(principal.getName());
        if (!admin.isAdmin()) {
            return false;
        }

        User user = this.modelMapper.map(bindingModel, User.class);
        user.setRegisteredBy(admin);
        user.setPassword(this.passwordEncoder.encode(bindingModel.getPassword()));

        user.addRole(this.roleRepository.findByAuthority(RoleConstants.INSTRUCTOR));

        this.userRepository.saveAndFlush(user);
        return true;
    }

    @Override
    public boolean registerClient(ClientRegisterBindingModel bindingModel, Principal principal) {
        User instructor = this.userRepository.findByUsername(principal.getName());
        if (!instructor.isInstructor()) {
            return false;
        }

        User user = this.modelMapper.map(bindingModel, User.class);
        user.setRegisteredBy(instructor);
        user.setPassword(this.passwordEncoder.encode(bindingModel.getPassword()));

        user.addRole(this.roleRepository.findByAuthority(RoleConstants.CLIENT));

        this.userRepository.saveAndFlush(user);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Card card = new Card();
        card.setClient(user);
        card.setStartDate(LocalDate.parse(bindingModel.getStartDate(), formatter));
        card.setEndDate(LocalDate.parse(bindingModel.getEndDate(), formatter));
        this.cardRepository.saveAndFlush(card);

        return true;
    }

    @Override
    public boolean addCard(Integer userId, CardCreateBindingModel bindingModel, Principal principal) {
        User instructor = this.userRepository.findByUsername(principal.getName());
        if (!instructor.isInstructor()) {
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Optional<User> user = this.userRepository.findById(userId);
        if (user.isPresent()) {
            Card card = new Card();
            card.setClient(user.get());
            card.setStartDate(LocalDate.parse(bindingModel.getStartDate(), formatter));
            card.setEndDate(LocalDate.parse(bindingModel.getEndDate(), formatter));
            this.cardRepository.saveAndFlush(card);
        }

        return true;
    }

    @Override
    public User findById(Integer id) {
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<UserViewModel> allUserModels() {
        List<UserViewModel> userViewModels = new ArrayList<>();
        for (User user : this.userRepository.findAll()) {
            List<CardViewModel> cardViewModels = new ArrayList<>();
            List<String> roles = new ArrayList<>();
            for (Card card : user.getCards().stream().sorted(Comparator.comparing(Card::getEndDate)).collect(Collectors.toList())) {
                CardViewModel cardViewModel = this.modelMapper.map(card, CardViewModel.class);
                cardViewModels.add(cardViewModel);
            }
            for (Role role : user.getAuthorities()) {
                roles.add(role.getAuthority());
            }
            UserViewModel userViewModel = this.modelMapper.map(user, UserViewModel.class);
            userViewModel.setCards(cardViewModels);
            userViewModel.setRoles(roles);
            userViewModel.setRegisteredById(user.getRegisteredBy() != null ? user.getRegisteredBy().getId() : null);

            userViewModels.add(userViewModel);
        }

        return userViewModels;
    }

    @Override
    public UserViewModel getClientDetails(Integer id) {
        Optional<User> client = this.userRepository.findById(id);

        if (client.isPresent()) {
            List<CardViewModel> cardViewModels = new ArrayList<>();
            UserViewModel userViewModel = this.modelMapper.map(client.get(), UserViewModel.class);
            for (Card card : client.get().getCards().stream().sorted(Comparator.comparing(Card::getEndDate)).collect(Collectors.toList())) {
                CardViewModel cardViewModel = this.modelMapper.map(card, CardViewModel.class);
                cardViewModels.add(cardViewModel);
            }
            List<String> roles = new ArrayList<>();
            for (Role role : client.get().getAuthorities()) {
                roles.add(role.getAuthority());
            }
            userViewModel.setCards(cardViewModels);
            userViewModel.setRoles(roles);
            userViewModel.setRegisteredById(client.get().getRegisteredBy() != null ? client.get().getRegisteredBy().getId() : null);
            return userViewModel;
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(s);

        if (user == null) {
            throw new UsernameNotFoundException("Wrong username!");
        }

        return user;
    }
}
