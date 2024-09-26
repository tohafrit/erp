package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Printer;

public interface PrinterRepository extends JpaRepository<Printer, Long> {}