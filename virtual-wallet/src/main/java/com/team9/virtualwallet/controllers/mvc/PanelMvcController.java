package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.TransactionService;
import com.team9.virtualwallet.services.contracts.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/panel")
public class PanelMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final WalletService walletService;
    private final TransactionService transactionService;

    public PanelMvcController(AuthenticationHelper authenticationHelper, WalletService walletService, TransactionService transactionService) {
        this.authenticationHelper = authenticationHelper;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @ModelAttribute("currentLoggedUser")
    public String populateCurrentLoggedUser(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("currentLoggedUser", user);
            return "";
        } catch (AuthenticationFailureException e) {
            return "/auth/login";
        }
    }

    @ModelAttribute("isEmployee")
    public boolean isEmployee(HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            return user.isEmployee();
        } catch (AuthenticationFailureException e) {
            return false;
        }
    }

    @GetMapping
    public String showPanelPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("totalBalance", walletService.getTotalBalanceByUser(user));
            model.addAttribute("lastTransactions", transactionService.getLastTransactions(user, 10));
            return "panel";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

}
