package com.example.lab5.controller;

import com.example.lab5.entity.Invoice;
import com.example.lab5.entity.InvoiceDetail;
import com.example.lab5.entity.Product;
import com.example.lab5.repository.CustomerRepository;
import com.example.lab5.repository.InvoiceDetailRepository;
import com.example.lab5.repository.InvoiceRepository;
import com.example.lab5.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comprobantes")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final ProductoRepository productoRepository;

    public InvoiceController(InvoiceRepository invoiceRepository, InvoiceDetailRepository detailRepository, CustomerRepository customerRepository, ProductoRepository productoRepository) {
        this.invoiceRepository = invoiceRepository;
        this.detailRepository = detailRepository;
        this.customerRepository = customerRepository;
        this.productoRepository = productoRepository;
    }
    @GetMapping({"","/"})
    public String listarComprobantes(Model model) {
        model.addAttribute("listaComprobantes", invoiceRepository.listarComprobantes());
        return "listaComprobantes";
    }

    @GetMapping("/nuevo")
    public String nuevoComprobante(@ModelAttribute("invoice") Invoice invoice, Model model) {
        model.addAttribute("listaClientes", customerRepository.findAll());
        model.addAttribute("listaProductos", productoRepository.findAll());

        return "crearComprobante";
    }

    @PostMapping("/guardar")
    public String guardarComprobante(
            @ModelAttribute("invoice") @Valid Invoice invoice,
            BindingResult bindingResult,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds,
            @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
            Model model, RedirectAttributes attr) {

        //Validación para documentos
        if (invoice.getCustomer() != null && invoice.getType() != null) {
            String tipoDoc = invoice.getCustomer().getDocumentoTipo();
            if (invoice.getType().equalsIgnoreCase("Factura") && !tipoDoc.equalsIgnoreCase("RUC")) {
                bindingResult.rejectValue("type", "error.type", "Factura solo permite clientes con RUC");
            }
            if (invoice.getType().equalsIgnoreCase("Boleta") && !tipoDoc.equalsIgnoreCase("DNI")) {
                bindingResult.rejectValue("type", "error.type", "Boleta solo permite clientes con DNI");
            }
        }

        boolean tieneProductos = false;
        if (productIds != null && cantidades != null) {
            for (int i = 0; i < productIds.size(); i++) {
                Integer cant = cantidades.get(i);
                if (cant != null && cant < 0) {
                    bindingResult.reject("globalError", "Las cantidades no pueden ser negativas.");
                }
                if (cant != null && cant > 0) {
                    tieneProductos = true;

                    //Stock disponible
                    Optional<Product> proOpt = productoRepository.findById(productIds.get(i));
                    Product p=proOpt.get();
                    if (cant > p.getStock()) {
                        bindingResult.reject("globalError", "Stock insuficiente para el producto: " + p.getNombre());
                    }
                }
            }
        }
        //Al menos un producto
        if (!tieneProductos) {
            bindingResult.reject("globalError", "El comprobante debe tener al menos un producto seleccionado");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("listaClientes", customerRepository.findAll());
            model.addAttribute("listaProductos", productoRepository.findAll());
            return "crearComprobante";
        }

        // 4. Proceso de Guardado y Cálculos (Regla 105)
        invoiceRepository.save(invoice); // Guardamos la cabecera primero

        for (int i = 0; i < productIds.size(); i++) {
            Integer cant = cantidades.get(i);
            if (cant != null && cant > 0) {
                Product p = productoRepository.findById(productIds.get(i)).get();

                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoice(invoice);
                detail.setProduct(p);
                detail.setQuantity(cant);
                detail.setPrice(p.getPrecio());
                detail.setSubtotal(cant * p.getPrecio());

                detailRepository.save(detail);

                //actualizar stock
                p.setStock(p.getStock() - cant);
                productoRepository.save(p);
            }
        }

        attr.addFlashAttribute("msg", "Comprobante generado exitosamente");
        return "redirect:/comprobantes";
    }
}