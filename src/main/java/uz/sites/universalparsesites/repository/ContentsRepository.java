package uz.sites.universalparsesites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sites.universalparsesites.entity.Contents;

public interface ContentsRepository extends JpaRepository<Contents, Integer> {
}
