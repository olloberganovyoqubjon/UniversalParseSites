package uz.sites.universalparsesites.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.sites.universalparsesites.entity.Rade;
import uz.sites.universalparsesites.repository.RadeRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class RadeService {

    private final RadeRepository radeRepository;

    public RadeService(RadeRepository radeRepository) {
        this.radeRepository = radeRepository;
    }


    @Scheduled(cron = "0 0 8 * * ?")  // Har kuni soat 08:00 da ishga tushadi
    public void getAllRade() {
        String strDate = getStrDate();
        List<Rade> radeOptional = radeRepository.findRadeByDate(strDate);
        if (radeOptional.isEmpty()) {
            JSONParser parser = new JSONParser();
            try {
                URL url = new URL("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
//                URL url = new URL("https://nbu.uz/exchange-rates/json/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                StringBuilder inline = new StringBuilder();
                Scanner scanner = null;
                try {
                    scanner = new Scanner(url.openStream());
                } catch (IOException e) {
                    System.gc();
//                    radeRepository.save(new Rade(null,null, new Date() + "", null, null, "url ni scanner qila olmadi IOException xato berdi"));
                }

                try {
                    try {
                        while (true) {
                            assert scanner != null;
                            if (!scanner.hasNext()) break;
                            inline.append(scanner.nextLine());
                        }
                        scanner.close();
                        for (Object o : (JSONArray) parser.parse(inline.toString())) {
                            JSONObject o1 = (JSONObject) o;
                            String ccy = o1.get("Ccy").toString();
                            if (ccy.equals("USD") || ccy.equals("RUB") || ccy.equals("EUR")) {
                                Optional<Rade> radeByCcy = radeRepository.findRadeByCcy(ccy);
                                if (radeByCcy.isEmpty()) {
                                    System.out.println("log");
                                    break;
                                }
                                Rade rade = radeByCcy.get();
                                rade.setName(o1.get("CcyNm_UZ").toString());
                                rade.setRade(Double.parseDouble(o1.get("Rate").toString()));
                                rade.setDif(Double.parseDouble(o1.get("Diff").toString()));
                                rade.setDate(o1.get("Date").toString());
                                radeRepository.save(rade);
                            }
                        }
                    } catch (NullPointerException ignored) {

                    }

                } catch (Exception e) {
                    System.gc();
//                    radeRepository.save(new Rade(null, null,new Date() + "", null, null, "pars qila olmadi Exception xato berdi"));
                }
            } catch (MalformedURLException e) {
                System.gc();
//                radeRepository.save(new Rade(null, null,new Date() + "", null, null, "https://nbu.uz/exchange-rates/json/ saytini ola olmadi MalformedURLException xato berdi"));
            } catch (ProtocolException e) {
                System.gc();
//                radeRepository.save(new Rade(null, null,new Date() + "", null, null, "https://nbu.uz/exchange-rates/json/ saytini ola olmadi ProtocolException xato berdi"));
            } catch (IOException e) {
                System.gc();
//                radeRepository.save(new Rade(null,null, new Date() + "", null, null, "https://nbu.uz/exchange-rates/json/ saytini ola olmadi IOException xato berdi"));
            }
            System.gc();
        }

    }

    private String getStrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(new Date());
    }
}
