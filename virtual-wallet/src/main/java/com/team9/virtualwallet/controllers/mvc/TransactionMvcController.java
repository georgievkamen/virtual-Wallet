package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.InsufficientBalanceException;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.ExternalTransactionDto;
import com.team9.virtualwallet.models.dtos.MoveToWalletTransactionDto;
import com.team9.virtualwallet.models.dtos.TransactionDto;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.Sort;
import com.team9.virtualwallet.services.contracts.*;
import com.team9.virtualwallet.services.mappers.TransactionModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.configs.ApplicationConstants.LARGE_TRANSACTION_AMOUNT;
import static com.team9.virtualwallet.utils.DummyHelper.validateDummy;

@Controller
@RequestMapping("/panel/transactions")
public class TransactionMvcController extends BaseAuthenticationController {

    private final AuthenticationHelper authenticationHelper;
    private final TransactionService service;
    private final TransactionModelMapper modelMapper;
    private final CardService cardService;
    private final WalletService walletService;
    private final UserService userService;
    private final CategoryService categoryService;

    public TransactionMvcController(AuthenticationHelper authenticationHelper, TransactionService service, TransactionModelMapper modelMapper, CardService cardService, WalletService walletService, UserService userService, CategoryService categoryService) {
        super(authenticationHelper);
        this.authenticationHelper = authenticationHelper;
        this.service = service;
        this.modelMapper = modelMapper;
        this.cardService = cardService;
        this.walletService = walletService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("direction")
    public List<Direction> populateDirection() {
        return Arrays.asList(Direction.values());
    }

    @ModelAttribute("sort")
    public List<Sort> populateSort() {
        return Arrays.asList(Sort.values());
    }

    @ModelAttribute("largeTransactionAmount")
    public int populateLargeTransactionAmount() {
        return LARGE_TRANSACTION_AMOUNT;
    }

    @GetMapping
    public String showTransactionsPage(HttpSession session, Model model, @PageableDefault(page = 1) Pageable pageable,
                                       @RequestParam(name = "direction", required = false) Optional<String> direction,
                                       @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
                                       @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate,
                                       @RequestParam(name = "sortAmount", required = false) Optional<String> sortAmount,
                                       @RequestParam(name = "sortDate", required = false) Optional<String> sortDate,
                                       @RequestParam(name = "counterparty", required = false) Optional<String> counterparty) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Pages<Transaction> transactions;
            if (direction.isEmpty() && startDate.isEmpty() && endDate.isEmpty() && counterparty.isEmpty()) {
                transactions = service.getAll(user, pageable);
            } else {
                transactions = service.filter(user,
                        direction.filter(s -> !s.equals("-1")).map(Direction::getEnum),
                        startDate,
                        endDate,
                        counterparty.isPresent() && counterparty.get().isBlank() ? Optional.empty() : counterparty,
                        sortAmount.filter(s -> !s.equals("-1")).map(Sort::getEnum),
                        sortDate.filter(s -> !s.equals("-1")).map(Sort::getEnum),
                        pageable);
            }
            model.addAttribute("transactions", transactions.getContent());
            model.addAttribute("transactionsExist", !transactions.getContent().isEmpty());
            model.addAttribute("pagination", transactions);
            model.addAttribute("pages", transactions.getTotalPages());
            model.addAttribute("cardService", cardService);
            model.addAttribute("walletService", walletService);
            return "transactions";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "transactions";
        }
    }

    @GetMapping("/create/internal")
    public String showInternalTransactionPage(HttpSession session, Model model,
                                              @RequestParam(name = "fieldName", required = false) String fieldName,
                                              @RequestParam(name = "search-field", required = false) String searchTerm) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (user.isBlocked() || !user.isVerified()) {
                return "redirect:/panel/transactions";
            }
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
            List<Category> categories = categoryService.getAll(user);
            model.addAttribute("categories", categories);
            model.addAttribute("categoriesExist", !categories.isEmpty());
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
            if (user.isBlocked() || !user.isVerified()) {
                return "redirect:/panel/transactions";
            }
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
            if (user.isBlocked() || !user.isVerified()) {
                return "redirect:/panel/transactions";
            }
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
            validateDummy();
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
            if (user.isBlocked() || !user.isVerified()) {
                return "redirect:/panel/transactions";
            }
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
    public String createWithdrawTransaction(@Valid
                                            @ModelAttribute("transaction")
                                                    ExternalTransactionDto externalTransactionDto,
                                            BindingResult result, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("userWallets", walletService.getAll(user));
            model.addAttribute("userCards", cardService.getAll(user));
            if (result.hasErrors()) {
                return "transaction-withdraw-create";
            }
            validateDummy();
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

    @GetMapping("/verify-transaction")
    public String verifyLargeTransaction(@RequestParam("token") String token, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.confirmLargeTransaction(user, token);
            return "redirect:/panel";
        } catch (IllegalArgumentException e) {
            return "redirect:/panel";
        }
    }

}