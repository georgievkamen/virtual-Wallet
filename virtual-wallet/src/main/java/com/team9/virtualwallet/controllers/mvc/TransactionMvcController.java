package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.InsufficientBalanceException;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.ExternalTransactionDto;
import com.team9.virtualwallet.models.dtos.MoveToWalletTransactionDto;
import com.team9.virtualwallet.models.dtos.TransactionDto;
import com.team9.virtualwallet.services.contracts.*;
import com.team9.virtualwallet.services.mappers.TransactionModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/panel/transactions")
public class TransactionMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final TransactionService service;
    private final TransactionModelMapper modelMapper;
    private final CardService cardService;
    private final WalletService walletService;
    private final UserService userService;
    private final CategoryService categoryService;

    public TransactionMvcController(AuthenticationHelper authenticationHelper, TransactionService service, TransactionModelMapper modelMapper, CardService cardService, WalletService walletService, UserService userService, CategoryService categoryService) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
        this.modelMapper = modelMapper;
        this.cardService = cardService;
        this.walletService = walletService;
        this.userService = userService;
        this.categoryService = categoryService;
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
            model.addAttribute("cardService", cardService);
            model.addAttribute("walletService", walletService);
            return "transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/create/internal")
    public String showInternalTransactionPage(HttpSession session, Model model, @RequestParam(name = "fieldName", required = false) String fieldName, @RequestParam(name = "search-field", required = false) String searchTerm) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Category> categories = categoryService.getAll(user);
            model.addAttribute("transaction", new TransactionDto());
            model.addAttribute("userWallets", walletService.getAll(user));
            model.addAttribute("categories", categories);
            model.addAttribute("categoriesExist", !categories.isEmpty());
            if (fieldName != null && searchTerm != null) {
                model.addAttribute("user", userService.getByField(user, fieldName, searchTerm));
            }
            return "transaction-internal-create";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("notFound", "User not found!");
            return "transaction-internal-create";
        }
    }

    @PostMapping("/create/internal")
    public String createInternalTransaction(@Valid @ModelAttribute("transaction") TransactionDto transactionDto, BindingResult result, HttpSession session, Model model, @RequestParam(required = false) Optional<Integer> categoryId) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("userWallets", walletService.getAll(user));
            if (result.hasErrors()) {
                return "transaction-internal-create";
            }
            Optional<Integer> category = Optional.empty();
            if (categoryId.isPresent()) {
                category = categoryId.get() != -1 ? categoryId : Optional.empty();
            }
            Transaction transaction = modelMapper.fromDto(user, transactionDto);
            service.create(transaction, category);
            return "redirect:/panel/transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            result.rejectValue("recipientId", "not_found", e.getMessage());
            return "transaction-internal-create";
        } catch (InsufficientBalanceException e) {
            result.rejectValue("amount", "insufficient_balance", e.getMessage());
            return "transaction-internal-create";
        }
    }

    @GetMapping("/create/wallet")
    public String showWalletToWalletTransactionPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("transaction", new MoveToWalletTransactionDto());
            model.addAttribute("userWallets", walletService.getAll(user));
            return "transaction-wallet-create";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("notFound", "User not found!");
            return "transaction-wallet-create";
        }
    }

    @PostMapping("/create/wallet")
    public String createWalletToWalletTransaction(@Valid @ModelAttribute("transaction") MoveToWalletTransactionDto moveToWalletTransactionDto, BindingResult result, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("userWallets", walletService.getAll(user));
            if (result.hasErrors()) {
                return "transaction-wallet-create";
            }
            Transaction transaction = modelMapper.fromDtoMoveToWallet(user, moveToWalletTransactionDto);
            service.createWalletToWallet(transaction);
            return "redirect:/panel/transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (InsufficientBalanceException e) {
            result.rejectValue("amount", "insufficient_balance", e.getMessage());
            return "transaction-wallet-create";
        } catch (IllegalArgumentException e) {
            result.rejectValue("walletToMoveToId", "same_wallet", e.getMessage());
            return "transaction-wallet-create";
        }
    }

    @GetMapping("/create/deposit")
    public String showDepositTransactionPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("transaction", new ExternalTransactionDto());
            model.addAttribute("userWallets", walletService.getAll(user));
            model.addAttribute("userCards", cardService.getAll(user));
            return "transaction-deposit-create";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("notFound", "User not found!");
            return "transaction-deposit-create";
        }
    }

    @PostMapping("/create/deposit")
    public String createDepositTransaction(@Valid @ModelAttribute("transaction") ExternalTransactionDto externalTransactionDto, BindingResult result, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("userWallets", walletService.getAll(user));
            model.addAttribute("userCards", cardService.getAll(user));
            if (result.hasErrors()) {
                return "transaction-deposit-create";
            }
            Transaction transaction = modelMapper.fromExternalDepositDto(user, externalTransactionDto);
            service.createExternalDeposit(transaction);
            return "redirect:/panel/transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (InsufficientBalanceException e) {
            result.rejectValue("amount", "insufficient_balance", e.getMessage());
            return "transaction-deposit-create";
        }
    }

    @GetMapping("/create/withdraw")
    public String showWithdrawTransactionPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("transaction", new ExternalTransactionDto());
            model.addAttribute("userWallets", walletService.getAll(user));
            model.addAttribute("userCards", cardService.getAll(user));
            return "transaction-withdraw-create";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("notFound", "User not found!");
            return "transaction-withdraw-create";
        }
    }

    @PostMapping("/create/withdraw")
    public String createWithdrawTransaction(@Valid @ModelAttribute("transaction") ExternalTransactionDto externalTransactionDto, BindingResult result, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("userWallets", walletService.getAll(user));
            model.addAttribute("userCards", cardService.getAll(user));
            if (result.hasErrors()) {
                return "transaction-withdraw-create";
            }
            Transaction transaction = modelMapper.fromExternalWithdrawDto(user, externalTransactionDto);
            service.createExternalWithdraw(transaction);
            return "redirect:/panel/transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (InsufficientBalanceException e) {
            result.rejectValue("amount", "insufficient_balance", e.getMessage());
            return "transaction-withdraw-create";
        }
    }

}
