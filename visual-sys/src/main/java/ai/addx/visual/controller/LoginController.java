package ai.addx.visual.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.ServerSocket;
import java.nio.channels.Selector;

@RestController
public class LoginController {
    @Value("${addx.username}")
    String username;
    @Value("${addx.password}")
    String password;
    @PostMapping("login")
    public ModelAndView login(String username, String password, HttpServletResponse response) {
        if (username.isEmpty() || password.isEmpty()) {
            ModelAndView mv = new ModelAndView("page/index");
            mv.addObject("error","用户名或密码不能为空");
            return mv;
        }
        if (!(this.username.equals(username) && this.password.equals(password))){
            ModelAndView mv = new ModelAndView("page/index");
            mv.addObject("error", "用户名或密码有误");
            return mv;
        }
        ModelAndView mv = new ModelAndView("redirect:battery-query");
        Cookie cookie = new Cookie("user", username);
        cookie.setPath("/");
        cookie.setMaxAge(60*30);
        response.addCookie(cookie);

        Selector
        return mv;
    }

}
