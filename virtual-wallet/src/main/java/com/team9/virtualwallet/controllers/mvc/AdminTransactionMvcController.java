package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.services.contracts.TransactionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/panel/admin/transactions")
public class AdminTransactionMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final TransactionService service;

    public AdminTransactionMvcController(AuthenticationHelper authenticationHelper, TransactionService service) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
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

    @ModelAttribute("direction")
    public List<Direction> populateDirection() {
        return List.of(Direction.values());
    }

    @GetMapping
    public String showAdminTransactionsPage(HttpSession session, Model model,
                                            @RequestParam(name = "direction", required = false) String direction,
                                            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
                                            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate,
                                            @RequestParam(name = "username", required = false) Optional<String> username,
                                            @RequestParam(name = "counterparty", required = false) Optional<String> counterparty,
                                            @PageableDefault(page = 1) Pageable pageable) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (username.isPresent()) {
                if (username.get().isBlank()) {
                    model.addAttribute("error", "You must provide an username!");
                    return "transactions-admin";
                }
            }
            Pages<Transaction> filtered = service.employeeFilter(user,
                    username.get(),
                    Direction.getEnum(direction),
                    startDate,
                    endDate,
                    counterparty.isEmpty() ? counterparty : Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    pageable);
            model.addAttribute("transactions", filtered);
            model.addAttribute("transactionsExist", !filtered.getContent().isEmpty());
            model.addAttribute("pagination", filtered);
            return "transactions-admin";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "transactions-admin";
        }
    }

}