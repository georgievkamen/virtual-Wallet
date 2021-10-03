package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CardDto;
import com.team9.virtualwallet.services.contracts.CardService;
import com.team9.virtualwallet.services.mappers.CardModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/panel/cards")
public class CardMvcController extends BaseAuthenticationController {

    private final AuthenticationHelper authenticationHelper;
    private final CardService service;
    private final CardModelMapper modelMapper;

    public CardMvcController(AuthenticationHelper authenticationHelper, CardService service, CardModelMapper modelMapper) {
        super(authenticationHelper);
        this.authenticationHelper = authenticationHelper;
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showCardsPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Card> cards = service.getAll(user);
            model.addAttribute("cards", cards);
            model.addAttribute("cardsExist", !cards.isEmpty());
            return "cards";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/add")
    public String showAddCardPage(HttpSession session, Model model) {
        try {
            authenticationHelper.tryGetUser(session);
            model.addAttribute("card", new CardDto());
            return "card-add";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/add")
    public String addCard(@ModelAttribute("card") @Valid CardDto cardDto, BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "card-add";
        }

        try {
            User user = authenticationHelper.tryGetUser(session);
            Card card = modelMapper.fromDto(user, cardDto);
            service.create(card);
            return "redirect:/panel/cards";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditCardPage(HttpSession session, Model model, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Card card = service.getById(user, id);
            CardDto cardDto = modelMapper.toDto(card);
            model.addAttribute("cardId", id);
            model.addAttribute("card", cardDto);
            return "card-update";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/cards";
        }
    }

    @PostMapping("/{id}/update")
    public String editCard(@Valid @ModelAttribute("card") CardDto cardDto, BindingResult result, HttpSession session, Model model, @PathVariable int id) {
        if (result.hasErrors()) {
            model.addAttribute("cardId", id);
            return "card-update";
        }

        try {
            User user = authenticationHelper.tryGetUser(session);
            Card card = modelMapper.fromDto(cardDto, id);
            service.update(user, card);
            return "redirect:/panel/cards";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/cards";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteCard(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.delete(user, id);
            return "redirect:/panel/cards";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/cards";
        }
    }

}
