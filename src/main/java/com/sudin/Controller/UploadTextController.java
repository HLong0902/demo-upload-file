package com.sudin.Controller;

import com.sudin.configuration.Permission;
import com.sudin.sevice.UploadTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UploadTextController {

    @Autowired
    private UploadTextService uploadTextService;

      @Autowired
    private Map<String, String> loginData;


    @Permission
    @RequestMapping(value="/uploadText", method = RequestMethod.POST)
    public ModelAndView uploadText(@RequestParam(name = "data") String data, HttpServletRequest request){
        String username = loginData.get(request.getRemoteAddr());
        uploadTextService.saveTextToFile(data, username);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message",  uploadTextService.getTextForUser(username));
        modelAndView.addObject("viewText",  "show");
        modelAndView.setViewName("/admin/index");
        return modelAndView;
    }

//    @RequestMapping(value="/uploadText", method = RequestMethod.GET)
//    public ModelAndView getUploadText(){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("message",  uploadTextService.getTextForUser(""));
//        modelAndView.setViewName("/admin/view");
//        return modelAndView;
//    }


    @Permission
    @RequestMapping(value="/admin/view", method = RequestMethod.GET)
    public ModelAndView viewData(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message",  uploadTextService.getTextForUser(loginData.get(request.getRemoteAddr())));
        modelAndView.setViewName("/admin/index");
        return modelAndView;
    }
}
