package com.example.lab5.controller;

import com.example.lab5.entity.Customer;
import com.example.lab5.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class CustomerController {
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
    }
    @GetMapping({"","/"})
    public String listarCustomers(Model model){
        model.addAttribute("listaClientes",customerRepository.findAll());
        return "listaClientes";
    }
    @GetMapping("/crearCliente")
    public String registrarUsuario(@ModelAttribute("cliente") Customer cliente) {
        return "crearClientes";
    }
    @PostMapping("/guardarCliente")
    public String guardarUsuario(@ModelAttribute("cliente") @Valid Customer cliente, BindingResult bindingResult, RedirectAttributes attr) {

        if (!bindingResult.hasFieldErrors("documento") && !bindingResult.hasFieldErrors("documentoTipo")) {

            String tipo = cliente.getDocumentoTipo();
            String doc = cliente.getDocumento();

            //Validaciones Regex
            if (tipo.equalsIgnoreCase("DNI")) {
                if (!doc.matches("\\d{8}")) {
                    bindingResult.rejectValue("documento", "error.documento", "El DNI debe tener exactamente 8 dígitos numéricos");
                }
            } else if (tipo.equalsIgnoreCase("RUC")) {
                if (!doc.matches("\\d{11}")) {
                    bindingResult.rejectValue("documento", "error.documento", "El RUC debe tener exactamente 11 dígitos numéricos");
                }
            } else {
                bindingResult.rejectValue("documentoTipo", "error.documentoTipo", "El tipo de documento debe ser DNI o RUC");
            }

            //Documento Único
            if (!bindingResult.hasFieldErrors("documento")) {
                Optional<Customer> clienteExistente = customerRepository.findByDocumento(doc);

                if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(cliente.getId())) {
                    bindingResult.rejectValue("documento", "error.documento", "Este documento ya se encuentra registrado");
                }
            }
        }

        if (bindingResult.hasErrors()) {
            return "crearClientes";
        }
        if (cliente.getId() == null) {
            attr.addFlashAttribute("msg", "Usuario creado exitosamente");
        } else {
            attr.addFlashAttribute("msg", "Usuario actualizado exitosamente");
        }
        customerRepository.save(cliente);
        return "redirect:/";
    }
    @GetMapping("/editarCliente")
    public String editarUsuario(@ModelAttribute("cliente") Customer cliente,
                                @RequestParam("id") Integer id,
                                Model model) {
        Optional<Customer> optUsuario = customerRepository.findById(id);

        if (optUsuario.isPresent()) {
            cliente = optUsuario.get();
            model.addAttribute("cliente", cliente);
            return "crearClientes";
        }
        return "redirect:/";
    }
    @GetMapping("/eliminarCliente")
    public String borrarCliente(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Customer> optional = customerRepository.findById(id);

        if (optional.isPresent()) {
            customerRepository.deleteById(id);
        }
        attr.addFlashAttribute("msg", "usuario borrado exitosamente");
        return "redirect:/";
    }
}
