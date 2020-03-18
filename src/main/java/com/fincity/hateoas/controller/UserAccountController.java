package com.fincity.hateoas.controller;

import com.fincity.hateoas.model.ConfirmationToken;
import com.fincity.hateoas.model.User;
import com.fincity.hateoas.repository.ConfirmationTokenRepository;
import com.fincity.hateoas.repository.UserRepository;
import com.fincity.hateoas.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@RestController
public class UserAccountController {

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping(value = "/signin")
    public ModelAndView displaySignInPage(ModelAndView modelAndView,User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("signIn");
        return modelAndView;
    }

    @PostMapping(value="/signin")
    public ModelAndView validateSignIn(ModelAndView modelAndView, User user,HttpServletRequest request){
        User existingUser = userRepository.findByEmailIdIgnoreCase(user.getEmailId());
        if(existingUser == null){
            modelAndView.addObject(MESSAGE,"Incorrect mail id");
            modelAndView.setViewName(ERROR);
        }
        else if(existingUser.getPassword().equalsIgnoreCase(user.getPassword()) && existingUser.isEnabled()){
            modelAndView.addObject(MESSAGE,"Successfull Login");
            modelAndView.setViewName(ERROR);
            request.getSession().setAttribute("token",confirmationTokenRepository.findByUser(existingUser).getConfirmationToken());
        }
        else if(existingUser.getPassword().equalsIgnoreCase(user.getPassword()) && !existingUser.isEnabled()) {
            modelAndView.addObject(MESSAGE, "Verify Your mail id and then proceed");
            modelAndView.setViewName(ERROR);
        }
        return modelAndView;
    }

    @GetMapping(value = "/register")
    public ModelAndView displayRegistration(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping(value = "/register")
    public ModelAndView registerUser(ModelAndView modelAndView, User user) {

        User existingUser = userRepository.findByEmailIdIgnoreCase(user.getEmailId());
        if (existingUser != null) {
            modelAndView.addObject(MESSAGE, "This email already exists!!!");
            modelAndView.setViewName(ERROR);
        } else {
            /**
             * save the user and the confirmation-token
             * isUserEnabled is false here
             */

            userRepository.save(user);

            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmailId());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("sudarcool.prabu36@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());
            emailSenderService.sendEmail(mailMessage);

            modelAndView.addObject("emailId", user.getEmailId());
            modelAndView.setViewName("successfulRegisteration");
        }

        return modelAndView;
    }

    /**
     * Enable the user boolean if the token matches
     * @param modelAndView
     * @param confirmationToken
     * @return
     */
    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token != null) {
            User user = userRepository.findByEmailIdIgnoreCase(token.getUser().getEmailId());
            user.setEnabled(true);
            userRepository.save(user);
            modelAndView.setViewName("accountVerified");
        } else {
            modelAndView.addObject(MESSAGE, "The link is invalid or broken!");
            modelAndView.setViewName(ERROR);
        }

        return modelAndView;
    }

    public EmailSenderService getEmailSenderService() {
        return emailSenderService;
    }

    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

}
