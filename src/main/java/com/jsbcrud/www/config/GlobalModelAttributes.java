package com.jsbcrud.www.config;

import com.jsbcrud.www.model.Account;
import com.jsbcrud.www.repository.AccountRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {
    private final Config config;
    private final AccountRepository accountRepository;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("copyright", config.getCopyright());
        model.addAttribute("sitename", config.getHeaderName());
        model.addAttribute("logo", config.getLogo());

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("account".equals(cookie.getName())) {
                    Integer userId = Integer.parseInt(cookie.getValue());
                    Optional<Account> userOpt = accountRepository.findById(userId);
                    if (userOpt.isPresent() && userOpt.get().getStatus() == Account.Status.ON) {
                        System.out.println("----------\n\n\n" + userOpt.get().getName() + "\n\n\n----------");
                        model.addAttribute("loggedUser", userOpt.get());
                    } else {
                        Cookie removeCookie = new Cookie("account", "");
                        removeCookie.setMaxAge(0);
                        removeCookie.setPath("/");
                        removeCookie.setHttpOnly(true);
                        response.addCookie(removeCookie);
                    }
                    break;
                }
            }
        }

    }
}