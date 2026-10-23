
package com.JDBC.prac.Banking;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class BankingController {

    private final CustomerService customerService;
    private final AuthService authService;
    private final TransactionService transactionService;
    private final BeneficiaryService beneficiaryService;

    public BankingController(
            CustomerService customerService,
            AuthService authService,
            TransactionService transactionService,
            BeneficiaryService beneficiaryService) {

        this.customerService = customerService;
        this.authService = authService;
        this.transactionService = transactionService;
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        User user = authService.login(username, password);

        if (user != null) {
            session.setAttribute("loggedInUserId", user.getUserID());
            session.setAttribute("customerID", user.getCustomerID());
            session.setAttribute("username", user.getUsername());

            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute(
                "message", "Invalid username or password!");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String contact,
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        if (username == null || username.isBlank()
                || password == null || password.length() < 8) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Enter a username and a password of at least 8 characters.");
            return "redirect:/register";
        }

        // Check username before creating a customer record.
        if (authService.usernameExists(username)) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Username already exists. Please choose another.");
            return "redirect:/register";
        }

        Customer customer = new Customer(0, name, address, contact);
        Customer savedCustomer = customerService.registerCustomer(customer);

        boolean registered = authService.register(
                savedCustomer.getCustomerID(), username, password);

        if (!registered) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Username already exists. Please try another.");
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute(
                "message", "Registration successful! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }
        return "dashboard";
    }

    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Customer customer = customerService.getCustomer(customerID(session));
        model.addAttribute("customer", customer);
        return "account";
    }

    @GetMapping("/deposit")
    public String depositPage(HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam BigDecimal amount,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/login";

        try {
            customerService.deposit(customerID(session), amount);
            redirectAttributes.addFlashAttribute(
                    "message", "Deposit successful!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "message", ex.getMessage());
        }

        return "redirect:/deposit";
    }

    @GetMapping("/withdraw")
    public String withdrawPage(HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam BigDecimal amount,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/login";

        try {
            customerService.withdraw(customerID(session), amount);
            redirectAttributes.addFlashAttribute(
                    "message", "Withdrawal successful!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "message", ex.getMessage());
        }

        return "redirect:/withdraw";
    }

    @GetMapping("/transactions")
    public String transactions(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute(
                "transactions",
                transactionService.getTransactions(customerID(session)));

        return "transactions";
    }

    @GetMapping("/beneficiaries")
    public String beneficiaries(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute(
                "beneficiaries",
                beneficiaryService.getBeneficiaries(customerID(session)));

        return "beneficiaries";
    }

    @PostMapping("/beneficiaries/add")
    public String addBeneficiary(
            @RequestParam String name,
            @RequestParam String accountNumber,
            @RequestParam(required = false) String bankDetails,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/login";

        try {
            beneficiaryService.addBeneficiary(
                    customerID(session), name, accountNumber, bankDetails);

            redirectAttributes.addFlashAttribute(
                    "message", "Beneficiary added successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "message", ex.getMessage());
        }

        return "redirect:/beneficiaries";
    }

    @PostMapping("/beneficiaries/delete")
    public String deleteBeneficiary(
            @RequestParam int beneficiaryID,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/login";

        try {
            beneficiaryService.deleteBeneficiary(
                    beneficiaryID, customerID(session));

            redirectAttributes.addFlashAttribute(
                    "message", "Beneficiary deleted successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "message", ex.getMessage());
        }

        return "redirect:/beneficiaries";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUserId") != null
                && session.getAttribute("customerID") != null;
    }

    private int customerID(HttpSession session) {
        return (Integer) session.getAttribute("customerID");
    }
}
