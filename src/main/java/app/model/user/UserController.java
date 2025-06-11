package app.model.user;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    private final UserDAO userDao;

    public UserController(UserDAO userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userDao.createUser(user);
    }


    @GetMapping("/users/username/{username}")
    public User getUserByUsername(@PathVariable("username") String username) {
        return userDao.findByUsername(username);
    }
    
    @GetMapping("/users/id/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userDao.findById(id);
    }

    @DeleteMapping("/users/id/{id}")
    public void deleteUserById(@PathVariable("id") Long id) {
        userDao.delete(id);
    }
    
}
