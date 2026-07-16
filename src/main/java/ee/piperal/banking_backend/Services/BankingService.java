package ee.piperal.banking_backend.Services;


import ee.piperal.banking_backend.Controllers.UserController;
import ee.piperal.banking_backend.Entities.Message;
import ee.piperal.banking_backend.Entities.Person;
import ee.piperal.banking_backend.Entities.Transaction;
import ee.piperal.banking_backend.Repositories.TransactionHistoryRepository;
import ee.piperal.banking_backend.Repositories.UserRepository;
import ee.piperal.banking_backend.dto.TransferDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "*")
@Service
public class BankingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private UserService userService;


    public void moneyTransfer(@RequestBody Long from, Long to, double amount) {
        Person fromPerson = userRepository.findById(from).orElseThrow();
        Person toPerson = userRepository.findById(to).orElseThrow();
        //Create transaction
        Transaction transaction = new Transaction();
        transaction.setReceiverId(to);
        transaction.setSenderId(from);
        transaction.setAmount(amount);

        userService.personValidator(fromPerson);
        userService.personValidator(toPerson);
        userService.tokenValidator(from, fromPerson.getToken());
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't transfer 0 or less than 0");
        }
        if (fromPerson.getBalance() < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance");
        }
        if (amount > fromPerson.getTransfer_limit()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't transfer more than " + fromPerson.getTransfer_limit());
        } else {
            transactionHistoryRepository.save(transaction);
            fromPerson.setBalance(fromPerson.getBalance() - amount);
            toPerson.setBalance(toPerson.getBalance() + amount);
            userRepository.save(fromPerson);
            userRepository.save(toPerson);
        }
    }


    public void addFunds(@RequestBody Long id, double amount) {
        Person person = userRepository.findById(id).orElseThrow();
        userService.personValidator(person);
        userService.tokenValidator(id, person.getToken());
        //Create transaction
        Transaction transaction = new Transaction();
        transaction.setReceiverId(id);
        transaction.setSenderId(id);
        transaction.setAmount(amount);

        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't add negative amount");
        } else {
            transactionHistoryRepository.save(transaction);
            person.setBalance(person.getBalance() + amount);
            userRepository.save(person);
        }
    }

    public void removeFunds(@RequestBody Long id, double amount) {
        Person person = userRepository.findById(id).orElseThrow();
        userService.personValidator(person);
        userService.tokenValidator(id, person.getToken());
        //Create transaction
        Transaction transaction = new Transaction();
        transaction.setReceiverId(id);
        transaction.setSenderId(id);
        transaction.setAmount(amount);

        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove negative amount");
        }
        if (amount > person.getBalance()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove more than balance");
        } else {
            transactionHistoryRepository.save(transaction);
            person.setBalance(person.getBalance() - amount);
            userRepository.save(person);
        }
    }

}
