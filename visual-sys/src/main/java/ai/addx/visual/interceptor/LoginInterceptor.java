package ai.addx.visual.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            logger.info("Need to Login First");
            response.sendRedirect("/");
            return false;
        }
        String user = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user")) {
                user = cookie.getValue();
            }
        }
        if (user == null) {
            logger.info("Need to Login First");
            response.sendRedirect("/");
            return false;
        }
        if (!(user.equals("addx"))){
            logger.info("No authorization for the user named " + user);
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
