package com.sudin.Controller;

/**
 * Created by Sudin Ranjitkar on 5/17/2017.
 */
import javax.servlet.http.HttpServletRequest;

import com.jcraft.jsch.*;
import com.sudin.CommonService;
import com.sudin.configuration.Permission;
import com.sudin.dto.FileDTO;
import com.sudin.sevice.UploadTextService;
import com.sudin.storage.StorageProperties;
import com.sudin.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class LoginController {

//    @Autowired
//    private UserService userService;

    @Value("${mock.login}")
    private Boolean mockLogin;

    @Value("${login.host}")
    private String host;

    @Value("${login.port}")
    private Integer port;

    @Autowired
    private Map<String, String> loginData;

    @Autowired
    private StorageProperties properties;


    @Autowired
    private UploadTextService uploadTextService;

    @Autowired
    private StorageService storageService;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @Permission
    @PostMapping("/login")
    public ModelAndView login(@RequestParam(value = "password", required = false) String pass,
                              @RequestParam(value = "username", required = false) String username,
                              HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();
        if (mockLogin) {
            loginData.put(request.getRemoteAddr(), username);
            Path rootLocation = Paths.get(properties.getLocation(), username, "files");
            properties.setRootLocation(rootLocation);
            CommonService.newFolder(properties.getRootLocation());
            modelAndView.addObject("files", storageService.getFiles(request));
            modelAndView.addObject("username",username);
            modelAndView.addObject("message",uploadTextService.getTextForUser(username));
            modelAndView.setViewName("admin/index");
            return modelAndView;
        }

        try{
            checkLogin(username, pass, request);
            loginData.put(request.getRemoteAddr(), username);
            Path rootLocation = Paths.get(properties.getLocation(), username, "files");
            properties.setRootLocation(rootLocation);
            CommonService.newFolder(properties.getRootLocation());
            modelAndView.addObject("files", storageService.getFiles(request));
            modelAndView.addObject("message",uploadTextService.getTextForUser(username));
            modelAndView.addObject("username",username);
            modelAndView.setViewName("admin/index");
            return modelAndView;
        }catch(Exception e){
            e.printStackTrace();
        }
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping(value = {"/logout", "/log-out"})
    public ModelAndView logout(HttpServletRequest request){

        loginData.remove(request.getRemoteAddr());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
//        Users users = new Users();
//        modelAndView.addObject("user", users);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

//    @RequestMapping(value = "/registration", method = RequestMethod.POST)
//    public ModelAndView createNewUser(@Valid Users users, BindingResult bindingResult) {
//        ModelAndView modelAndView = new ModelAndView();
//        Users usersExists = userService.findUserByEmail(users.getEmail());
//        if (usersExists != null) {
//            bindingResult
//                    .rejectValue("email", "error.users",
//                            "There is already a users registered with the email provided");
//        }
//        if (bindingResult.hasErrors()) {
//            modelAndView.setViewName("registration");
//        } else {
//            userService.saveUser(users);
//            modelAndView.addObject("successMessage", "Users has been registered successfully");
//            modelAndView.addObject("user", new Users());
//            modelAndView.setViewName("registration");
//
//        }
//        return modelAndView;
//    }

    @Permission
    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Users users = userService.findUserByEmail(auth.getName());
//        modelAndView.addObject("userName", "Welcome " + users.getName() + " " + users.getLastName() + " (" + users.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("admin/index");
        return modelAndView;
    }

    void checkLogin(String username, String password, HttpServletRequest request) throws JSchException {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        Session session=jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig(config);
        session.connect();
        System.out.println("Connected");
    }

    @ExceptionHandler(Exception.class)
    public Object handleEx(Exception exc) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/index");
        return modelAndView;
    }
}