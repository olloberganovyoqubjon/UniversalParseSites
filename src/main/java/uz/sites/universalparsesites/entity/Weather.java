package uz.sites.universalparsesites.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double tempMin;

    private Double tempMax;

    private String weatherNameFirst;
    private String weatherNameSecond;

    private Timestamp updateDate = new Timestamp(System.currentTimeMillis());
}
