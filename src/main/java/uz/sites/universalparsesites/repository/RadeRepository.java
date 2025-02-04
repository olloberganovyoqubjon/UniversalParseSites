package uz.sites.universalparsesites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sites.universalparsesites.entity.Rade;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RadeRepository extends JpaRepository<Rade, Integer> {

    List<Rade> findRadeByDate(String date);
    List<Rade> findRadeByDateAndCode(String date, String code);
}
