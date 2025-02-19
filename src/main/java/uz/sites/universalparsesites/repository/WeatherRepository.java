package uz.sites.universalparsesites.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sites.universalparsesites.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {


}
