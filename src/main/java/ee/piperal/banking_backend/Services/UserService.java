package ee.piperal.banking_backend.Services;

import ee.piperal.banking_backend.Entities.Person;
import ee.piperal.banking_backend.Functions.TokenGenerator;
import ee.piperal.banking_backend.Repositories.UserRepository;
import ee.piperal.banking_backend.dto.AccountDto;
import ee.piperal.banking_backend.dto.LoginDto;
import ee.piperal.banking_backend.dto.RegisterDto;
import ee.piperal.banking_backend.dto.SearchDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //Checks db for id
    public void personValidator(Person person) {
        if (person.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Person id is null");
        }
    }

    //Compares tokens
    public void tokenValidator(Long id, String token) {
        String dbToken = userRepository.findById(id).orElseThrow().getToken();
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is null");
        }
        if (!dbToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized token");
        }
    }

    //Mainly for user personal account
    public AccountDto GetUser(Long id, String token) {
        Person person = userRepository.findById(id).orElseThrow();
        tokenValidator(id, token);
        AccountDto accountDto = new AccountDto();
        personValidator(person);
        accountDto.setId(person.getId());
        accountDto.setUsername(person.getUsername());
        accountDto.setEmail(person.getEmail());
        accountDto.setBalance(person.getBalance());
        return accountDto;
    }
    public SearchDto SearchAccount(Long id) {
        Person person = userRepository.findById(id).orElseThrow();
        SearchDto searchDto = new SearchDto();
        personValidator(person);
        searchDto.setId(person.getId());
        searchDto.setUsername(person.getUsername());
        return searchDto;
    }

    //Used for user search
    public List<SearchDto> searchUser(String username) {
        List<Person> people = userRepository.userSearch(username);
        List<SearchDto> searchDtos = new ArrayList<>();
        for (Person person : people) {
            searchDtos.add(new SearchDto());
            searchDtos.getLast().setUsername(person.getUsername());
            searchDtos.getLast().setId(person.getId());
        }
        return searchDtos;
    }

    public LoginDto userLogin(String username, String password) {
        Person person = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Incorrect username or password"
                        ));
        TokenGenerator tokenGenerator = new TokenGenerator();
        personValidator(person);
        if (person.getPassword().equals(password)) {
            LoginDto loginDto = new LoginDto();
            person.setLogged(true);
            String newToken = tokenGenerator.token();
            person.setToken(newToken);
            userRepository.save(person);
            loginDto.setId(person.getId());
            loginDto.setToken(newToken);
            return loginDto;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }
    }

    public void userLogout(Long id, String token) {
        Person person = userRepository.findById(id).orElseThrow();
        tokenValidator(id, token);
        person.setLogged(false);
        person.setToken(null);
        userRepository.save(person);
    }

    public boolean statusCheck(Long id) {
        Person person = userRepository.findById(id).orElseThrow();
        return person.isLogged();
    }

    public void CreateUser(Person person) {
        if (userRepository.findUsernameByUsername(person.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        } else {
            person.setBalance(0.0);
            person.setLogged(false);
            person.setTransfer_limit(100);
            person.setToken("0");
            userRepository.save(person);
        }

    }

    public void DeleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
