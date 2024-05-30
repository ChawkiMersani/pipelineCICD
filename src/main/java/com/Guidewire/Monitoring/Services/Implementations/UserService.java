package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.User;
import com.Guidewire.Monitoring.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;
    public String register(User user){

        Optional<User> userOptional=userRepository.findByEmail(user.getEmail());
        if(userOptional.isPresent()){
            return "user exists";
        }else{
            User userEntity= new User();
            userEntity.setFirstName(user.getEmail().substring(0,user.getEmail().indexOf(".")));
            userEntity.setLastName(user.getEmail().substring(user.getEmail().indexOf(".")+1,user.getEmail().indexOf("@")));
            userEntity.setEmail(user.getEmail());
            userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
            userEntity.setRole(user.getRole());
            User newUser = userRepository.save(userEntity);
            String token= jwtService.generateToken(newUser);
            return token;
        }
    }

    public List<Object> login(User user){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        User userEntity= userRepository.findByEmail(user.getEmail()).get();
        String token = jwtService.generateToken(userEntity);
        List<Object> response=new ArrayList<>();
        response.add(userEntity);
        response.add(token);
        return response;
    }

    public User getUserByID(UUID id) {
        Optional<User> user=userRepository.findById(id);
        return user.orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        Optional<User> userOptional=userRepository.findById(user.getId());
        if(userOptional.isPresent()){
            User updatedUser=userOptional.get();
            if(!(user.getEmail()==null)){
                updatedUser.setEmail(user.getEmail());
            }
            if(!(user.getFirstName()==null)){
                updatedUser.setFirstName(user.getFirstName());
            }
            if(!(user.getLastName()==null)){
                updatedUser.setLastName(user.getLastName());
            }
            if(!(user.getPassword()==null)) {
                updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.save(updatedUser);
        }
        return  null;
    }

    public void deleteUser(UUID id) {
         userRepository.deleteById(id);
    }
}
