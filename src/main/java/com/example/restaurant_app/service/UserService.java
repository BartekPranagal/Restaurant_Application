package com.example.restaurant_app.service;

import com.example.restaurant_app.model.dao.users.UserEntity;
import com.example.restaurant_app.model.dto.user.UserRequest;
import com.example.restaurant_app.model.dto.user.UserResponse;
import com.example.restaurant_app.repository.AuthorityRepository;
import com.example.restaurant_app.repository.UserRepository;
import com.example.restaurant_app.service.converters.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    public UserEntity saveUser(UserRequest userRequest) {
        UserEntity user = new UserEntity();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setMail(userRequest.getMail());
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setStreet(userRequest.getStreet());
        user.setStreetNumber(userRequest.getStreetNumber());
        user.setCity(userRequest.getCity());
        user.setPostalCode(userRequest.getPostalCode());
        user.setActive(true);
        user.setAuthorities(Arrays.asList(authorityRepository.findByName("ROLE_USER").orElseThrow(
                () -> new RuntimeException()
        )));
        return userRepository.save(user);
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .accountLocked(!user.isActive())
                .authorities(user.getAuthorities()
                        .stream().map(x -> x.getName())
                        .toArray(String[]::new))
                .build();
    }

    @Transactional
    public UserResponse updateUser(String callerName, Long id, UserRequest request){
        userRepository.findByUsername(callerName)
                .filter(x -> isOwner(id, x) || isAdmin(x))
                .orElseThrow();

        UserEntity existingEntity = userRepository.findById(id).orElseThrow();
        existingEntity.setName(request.getName().isEmpty()? existingEntity.getName() : request.getName());
        existingEntity.setSurname(request.getSurname().isEmpty()? existingEntity.getSurname() : request.getSurname());
        existingEntity.setUsername(request.getUsername().isEmpty()? existingEntity.getUsername() : request.getUsername());
        existingEntity.setPhoneNumber(request.getPhoneNumber().isEmpty()? existingEntity.getPhoneNumber() : request.getPhoneNumber());
        existingEntity.setMail(request.getMail().isEmpty()? existingEntity.getMail() : request.getMail());
        existingEntity.setCity(request.getCity().isEmpty()? existingEntity.getCity() : request.getCity());
        existingEntity.setPostalCode(request.getPostalCode().isEmpty()? existingEntity.getPostalCode() : request.getPostalCode());
        existingEntity.setStreet(request.getStreet().isEmpty()? existingEntity.getStreet() : request.getStreet());
        existingEntity.setStreetNumber(request.getStreetNumber().isEmpty()? existingEntity.getStreetNumber() : request.getStreetNumber());
        return userConverter.convertUserEntityToDto(existingEntity);
    }

    private boolean isAdmin(UserEntity x) {
        return x.getAuthorities().stream().anyMatch(auth -> auth.getName().equals("ROLE_ADMIN"));
    }

    private boolean isOwner(Long id, UserEntity x) {
        return x.getId().equals(id);
    }

    public UserEntity getUser(String name) {
        return userRepository.findByUsername(name).orElseThrow();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
