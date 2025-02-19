package uz.sites.universalparsesites.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.sites.universalparsesites.entity.Inf;
import uz.sites.universalparsesites.entity.Rade;
import uz.sites.universalparsesites.entity.Weather;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultPars {
    private Weather weather;
    private List<Inf> infList;
    private List<Rade> radeList;
}
