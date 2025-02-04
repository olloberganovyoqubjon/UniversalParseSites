package uz.sites.universalparsesites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sites.universalparsesites.entity.Category;
import uz.sites.universalparsesites.entity.Inf;

import java.util.List;

public interface InfRepository extends JpaRepository<Inf, Integer> {

    Boolean existsByLink(String link);

    List<Inf> findByDownloaded(Boolean downloaded);

    Long countInfByCategory(Category category);
}
