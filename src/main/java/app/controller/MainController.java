package app.controller;

import java.util.Date;
import java.util.List;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import app.config.Session;
import app.model.key.MyEncryption;
import app.model.key.MyKeyGenerator;
import app.model.user.User;
import app.model.user.UserController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class MainController {

	private final MyKeyGenerator s = new MyKeyGenerator();
	private final MyEncryption e = new MyEncryption();
	
	@Autowired
    private UserController userController;
	
	@Autowired
	private Session session;

	@GetMapping
    public String login(HttpSession session) {
		if (this.session.isSessionActive(session)) {
			String username = (String) session.getAttribute("username");
		 	return "redirect:/home?username=" + username;
	    }
		
        return "login.html";
    }
	
	@PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session, Model model) {
        
		
		if (isValidCredentials(username, password)) {
			String error = "Username o password non corretti.";
			model.addAttribute("error", error);
			return "redirect:/?error=" + error;
		}
		
		session.setAttribute("username", username);
		this.session.addSession(session);
		this.session.setSessionTimeout(session, 10);
		
        return "redirect:/home";
    }

	@GetMapping("/home")
    public String showHome(HttpSession session, Model model) {
		
		 if (!this.session.isSessionActive(session)) {
		        return "redirect:/";
		    }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        
        List<User> users = userController.getAllUsers();
        
        model.addAttribute("users", users);
        model.addAttribute("username", username);

        return "myhome.html";
    }
	
	@PostMapping("/logoutK")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		this.session.removeSession(session);
		session.invalidate();
        
        return "redirect:/";
    }

	@GetMapping("/registration")
    public String showRegistration(HttpSession session) {
		if (this.session.isSessionActive(session)) {
			String username = (String) session.getAttribute("username");
		 	return "redirect:/home?username=" + username;
	    }
		
        return "registration.html";
    }
	
	@PostMapping("/registration")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("password2") String password2, HttpSession session, Model model) {
        
		if (!password.equals(password2)) {
			String error = "Password diverse.";
			model.addAttribute("error", error);
			return "redirect:/registration?error=" + error;
		}
		
		User existingUser = userController.getUserByUsername(username);
		
		 if(existingUser != null)
	     {
			String error = "Utente gia' presente.";
			model.addAttribute("error", error);
			return "redirect:/registration?error=" + error;	
	     }	
		
		User u = new User();
		u.setUsername(username);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        u.setPassword(hashedPassword);
        u.setInfo("Data iscrizione: " + new Date());
        
        userController.createUser(u);
		
        session.setAttribute("username", username);
		this.session.addSession(session);
		this.session.setSessionTimeout(session, 10);
		
		
        return "redirect:/home";
    }

	@GetMapping("/user/{username}")
	public String getUserDetails(@PathVariable("username") String username, Model model, HttpSession session) {
		if (!this.session.isSessionActive(session)) {
	        return "redirect:/";
	    }
		
	    User user = userController.getUserByUsername(username);
	    
	    ECPublicKeyParameters chiavepub = s.generatePublicKey(username);
        ECPrivateKeyParameters chiavepriv = s.generatePrivateKey(username);
	    
	    model.addAttribute("user", user);
	    model.addAttribute("chiavepriv", chiavepriv.getD().toString());
	    model.addAttribute("chiavepubX", chiavepub.getQ().getAffineXCoord().toString());
	    model.addAttribute("chiavepubY", chiavepub.getQ().getAffineYCoord().toString());
	    
	    return "user"; 
	}
	
	@GetMapping("/cifra")
	public @ResponseBody String encrypt(@RequestParam("username") String username, @RequestParam("plaintext") String plaintext, Model model) {
	    ECPublicKeyParameters chiavepub = s.generatePublicKey(username);
	    ECPrivateKeyParameters chiavepriv = s.generatePrivateKey(username);
	    
	    byte[] cipher = e.cifra(plaintext, chiavepub, chiavepriv);
	    
	    return new String(cipher);
	}
	
	@GetMapping("/decifra")
	public @ResponseBody String decrypt(@RequestParam("username") String username, @RequestParam("ciphertext") String ciphertext, Model model) {
	    
		ECPublicKeyParameters chiavepub = s.generatePublicKey(username);
        ECPrivateKeyParameters chiavepriv = s.generatePrivateKey(username);
        
        byte[] ciphertextB = ciphertext.getBytes();
        
	    byte[] plaintext = e.decifra(ciphertextB, chiavepub, chiavepriv);
	    
	    return new String(plaintext);
	    
	}

	private boolean isValidCredentials(String username, String password) {
		boolean loginError = false;

		User existingUser = userController.getUserByUsername(username);

		if(existingUser == null)
		{
			loginError = true;
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (existingUser == null || !passwordEncoder.matches(password, existingUser.getPassword())) {
			loginError = true;
		}

		return loginError;
	}
}
