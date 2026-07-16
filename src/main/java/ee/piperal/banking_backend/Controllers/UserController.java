package ee.piperal.banking_backend.Controllers;

import ee.piperal.banking_backend.Entities.Person;
import ee.piperal.banking_backend.Services.UserService;
import ee.piperal.banking_backend.dto.AccountDto;
import ee.piperal.banking_backend.dto.LoginDto;
import ee.piperal.banking_backend.dto.SearchDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    //Mainly meant for personal user account
    @RateLimiter(name = "apiLimiter")
    @GetMapping("account/{id}")
    public AccountDto getUser(@PathVariable Long id, String token){
        return userService.GetUser(id, token);
    }

    @RateLimiter(name = "apiLimiter")
    @GetMapping("search/found/{id}")
    public SearchDto searchAccount(@PathVariable Long id){
        return userService.SearchAccount(id);
    }

    //Meant for user search
    @RateLimiter(name = "apiLimiter")
    @GetMapping("search/{username}")
    public List<SearchDto> postUser(@PathVariable String username){
       return userService.searchUser(username);
    }

    //User login
    @PostMapping("login/{username}")
    public LoginDto login(@PathVariable String username, @RequestBody Map<String, String> body){
        String password = body.get("password");
        return userService.userLogin(username, password);
    }

    //User logout
    @RateLimiter(name = "apiLimiter")
    @PostMapping("logout/{id}")
    public void logout(@PathVariable Long  id, String token){
        userService.userLogout(id, token);
    }

    //Check user logged status
    @PostMapping("status/{id}")
    public boolean status(@PathVariable Long id){
        return userService.statusCheck(id);
    }
    //User creation
    @RateLimiter(name = "apiLimiter")
    @PostMapping("newuser")
    public void newUser(@RequestBody Person user){
        userService.CreateUser(user);
    }

    @RateLimiter(name = "apiLimiter")
    @DeleteMapping("delete/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.DeleteUser(id);
    }


}
