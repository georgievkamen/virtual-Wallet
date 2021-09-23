package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/panel/transactions")
public class TransactionMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final TransactionService service;

    public TransactionMvcController(AuthenticationHelper authenticationHelper, TransactionService service) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
    }

    //TODO Think about moving this to BaseAuthenticationController
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

    @GetMapping
    public String showTransactionsPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Transaction> transactions = service.getAll(user);
            model.addAttribute("transactions", transactions);
            model.addAttribute("transactionsExist", !transactions.isEmpty());
            return "transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

}
