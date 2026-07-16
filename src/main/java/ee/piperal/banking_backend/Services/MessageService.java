package ee.piperal.banking_backend.Services;

import ee.piperal.banking_backend.Entities.Message;
import ee.piperal.banking_backend.Repositories.MessageRepository;
import ee.piperal.banking_backend.Repositories.UserRepository;
import ee.piperal.banking_backend.dto.MessageDto;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public void sendMessage(@RequestBody Message message) {
        String name = userRepository.findById(message.getReceiver())
                .orElseThrow(() -> new RuntimeException("User not found")).getUsername();
        message.setSender(message.getSender());
        message.setReceiver(message.getReceiver());
        message.setMessage(message.getMessage());
        message.setAmount(message.getAmount());
        message.setSenderName(name);
        messageRepository.save(message);
    }

    public List<MessageDto> getMessages(@RequestBody Long id, String token){
        String dbToken = userRepository.findById(id).get().getToken();
        String bodyToken = token.replaceAll("^\"|\"$", "");
        List<MessageDto> messageDtoList = new ArrayList<MessageDto>();
        userService.tokenValidator(id, dbToken);
        if(!dbToken.equals(bodyToken)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized token");
        }
        for( Message message : messageRepository.findBySenderId(id)){
            MessageDto messageDto = new MessageDto();
            messageDto.setId(message.getId());
            messageDto.setSenderId(message.getSender());
            messageDto.setReceiverId(message.getReceiver());
            messageDto.setMessage(message.getMessage());
            messageDto.setAmount(message.getAmount());
            messageDto.setSenderName(message.getSenderName());
            messageDtoList.add(messageDto);
        }
        return messageDtoList;
    }

    public void confirmMessage(@RequestBody Long id){
        Message message = messageRepository.findById(id).orElseThrow();
        message.setConfirmed(true);
        messageRepository.save(message);
    }

    public void deleteMessage(@RequestBody Long id) {
        messageRepository.deleteById(id);
    }


}
