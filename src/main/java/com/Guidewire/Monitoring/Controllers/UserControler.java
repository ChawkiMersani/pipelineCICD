package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.User;
import com.Guidewire.Monitoring.Services.Implementations.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@CrossOrigin(exposedHeaders = {"Authorization"},origins = "http://localhost:62344")
@Controller
@RequestMapping("user")
public class UserControler {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerController(@RequestBody User user){
        String token=userService.register(user);
        if(token=="user exists"){
            return new ResponseEntity<String>("email already registered", HttpStatus.BAD_REQUEST);
        }
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.set("Authorization",token);
        return new ResponseEntity<String>("added", httpHeaders,HttpStatus.CREATED);

    }
    @GetMapping("/signIn")
    public ResponseEntity<Object> signInController(@RequestParam String email,String password){
        User user= new User();
        user.setEmail(email);
        user.setPassword(password);
        List<Object> response=userService.login(user);
        String token=response.get(1).toString();
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.set("Authorization",token);
        return new ResponseEntity<Object>(response.get(0), httpHeaders, HttpStatus.ACCEPTED);
    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(@RequestParam String id){

        return ResponseEntity.ok(userService.getUserByID(UUID.fromString(id)));
    }
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @PostMapping("/updateUser")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(user));
    }
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam String id){
        System.out.println(id);
        userService.deleteUser(UUID.fromString(id));
        return ResponseEntity.ok("deleted");
    }
}
