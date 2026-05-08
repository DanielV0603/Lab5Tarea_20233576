package com.example.lab5.repository;

import com.example.lab5.dto.InvoiceDto;
import com.example.lab5.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    @Query(value = "SELECT i.id as id, i.type as type, i.date as date, c.name as customerName, " +
            "SUM(d.subtotal) as total " +
            "FROM invoice i " +
            "JOIN customer c ON i.customer_id = c.id " +
            "JOIN invoice_detail d ON i.id = d.invoice_id " +
            "GROUP BY i.id", nativeQuery = true)
    List<InvoiceDto> listarComprobantes();
}