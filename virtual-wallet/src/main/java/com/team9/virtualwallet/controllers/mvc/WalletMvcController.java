package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.dtos.WalletDto;
import com.team9.virtualwallet.services.contracts.WalletService;
import com.team9.virtualwallet.services.mappers.WalletModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/panel/wallets")
public class WalletMvcController extends BaseAuthenticationController {

    private final AuthenticationHelper authenticationHelper;
    private final WalletService service;
    private final WalletModelMapper modelMapper;

    public WalletMvcController(AuthenticationHelper authenticationHelper, WalletService service, WalletModelMapper modelMapper) {
        super(authenticationHelper);
        this.authenticationHelper = authenticationHelper;
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showWalletsPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Wallet> wallets = service.getAll(user);
            model.addAttribute("wallets", wallets);
            model.addAttribute("walletsExist", !wallets.isEmpty());
            return "wallets";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        }
    }

    @GetMapping("/add")
    public String showAddWalletPage(HttpSession session, Model model) {
        try {
            authenticationHelper.tryGetUser(session);
            model.addAttribute("wallet", new WalletDto());
            return "wallet-add";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        }
    }

    @PostMapping("/add")
    public String addWallet(@ModelAttribute("wallet") @Valid WalletDto walletDto, BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "wallet-add";
        }

        try {
            User user = authenticationHelper.tryGetUser(session);
            Wallet wallet = modelMapper.fromDto(user, walletDto);
            service.create(user, wallet);
            return "redirect:/panel/wallets";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        } catch (DuplicateEntityException e) {
            result.rejectValue("name", "duplicate_name", e.getMessage());
            return "wallet-add";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditWalletPage(HttpSession session, Model model, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Wallet wallet = service.getById(user, id);
            WalletDto walletDto = modelMapper.toDto(wallet);
            model.addAttribute("walletId", id);
            model.addAttribute("wallet", walletDto);
            return "wallet-update";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/wallets";
        }
    }

    @PostMapping("/{id}/update")
    public String editWallet(@Valid @ModelAttribute("wallet") WalletDto walletDto, BindingResult result, HttpSession session, Model model, @PathVariable int id) {
        if (result.hasErrors()) {
            model.addAttribute("walletId", id);
            return "wallet-update";
        }

        try {
            User user = authenticationHelper.tryGetUser(session);
            Wallet wallet = modelMapper.fromDto(walletDto, id);
            service.update(user, wallet);
            return "redirect:/panel/wallets";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/wallets";
        } catch (DuplicateEntityException e) {
            result.rejectValue("name", "duplicate_name", e.getMessage());
            return "wallet-update";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteWallet(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.delete(user, id);
            return "redirect:/panel/wallets";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/wallets";
        }
    }

    @GetMapping("/{id}/default")
    public String setDefaultWallet(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Wallet wallet = service.getById(user, id);
            service.setDefaultWallet(user, wallet);
            return "redirect:/panel/wallets";
        } catch (AuthenticationFailureException e) {
            return LOGIN_REDIRECT_CONSTANT;
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/wallets";
        }
    }

}
