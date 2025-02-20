package uz.sites.universalparsesites.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uz.sites.universalparsesites.dtos.ResultPars;
import uz.sites.universalparsesites.entity.*;
import uz.sites.universalparsesites.helpers.ParsDto;
import uz.sites.universalparsesites.helpers.RandomUserAgent;
import uz.sites.universalparsesites.helpers.Selenium;
import uz.sites.universalparsesites.helpers.WriteErrors;
import uz.sites.universalparsesites.repository.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Service
public class ServicePars {

    @Autowired
    private InfRepository infRepository;

    @Autowired
    private SitesRepository sitesRepository;

    @Autowired
    private ContentsRepository contentsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RadeRepository radeRepository;

    @Autowired
    private WeatherService weatherService;

    public void getAllSite() {
//        new Thread(() -> {
        try {
            while (true) {
//                getAllRade();
                for (Category category : categoryRepository.findAll()) {
                    Long countInf = infRepository.countInfByCategory(category);
                    boolean countBool;
                    if (countInf > 100) {
                        countBool = true;
                    }
                    if (category.getError() == null) {
                        RandomUserAgent randomUserAgent = new RandomUserAgent();
                        String userAgent = randomUserAgent.getRandomUserAgent();
                        try {
                            Map<String, String> mapAllSiteAndImgLinks = getAllSiteAndImgLinks(category.getName(), category.getLengthBaseUrl(), true);
                            if (mapAllSiteAndImgLinks.size() < 5) {
                                Map<String, String> presidentSite = getPresidentSite(category.getName(), userAgent);
                                mapAllSiteAndImgLinks.putAll(presidentSite);
                            }
                            if (mapAllSiteAndImgLinks.isEmpty()) {
                                WriteErrors writeErrors = new WriteErrors();
                                writeErrors.write("category.txt", new Date() + "\t" + category.getId() + "\t" + category.getName() + "\t" + category.getCategoryName() + "\t" + category.getLengthBaseUrl() + "\t" + "hech qanday liklarni yuklab ololmadi (getAllSiteAndImgLinks(String url, int lengthBaseUrl) dan map bo'sh qaytdi)" + "\t" + category.getSites() + "\t" + userAgent);
//                            categoryRepository.save(new Category(category.getId(), category.getName(), category.getCategoryName(), category.getLengthBaseUrl(), "hech qanday liklarni yuklab ololmadi (getAllSiteAndImgLinks(String url, int lengthBaseUrl) dan map bo'sh qaytdi)", category.getSites()));
                            }
                            for (Map.Entry<String, String> mapLink : mapAllSiteAndImgLinks.entrySet()) {
                                Inf inf = new Inf();
                                userAgent = randomUserAgent.getRandomUserAgent();
                                try {
                                    if (!infRepository.existsByLink(mapLink.getKey())) {
                                        ParsDto parsDto = getTitleAndBodyByLink(mapLink.getKey(), userAgent);
                                        inf.setTitle(parsDto.getTitle());
                                        String[] contents = parsDto.getContent().split("\n");
                                        List<Contents> contentsList = new ArrayList<>();
                                        for (String content : contents) {
                                            contentsList.add(new Contents(null, content));
                                        }
                                        List<Contents> contents1 = contentsRepository.saveAll(contentsList);
                                        inf.setContents(contents1);
                                        inf.setLink(mapLink.getKey());
                                        inf.setCreateDate("" + LocalDateTime.now());
                                        inf.setCategory(category);
                                        try {
                                            byte[] imgByLink = getImgByLink(mapLink.getValue());
                                            inf.setImg(imgByLink);
                                            infRepository.save(inf);


                                        } catch (Exception e) {
                                            infRepository.save(new Inf(null, mapLink.getKey(), parsDto.getTitle(), null, contentsList, "" + LocalDateTime.now(), category, "olingan rasm linkidan rasm yuklay olmadi (getImgByLink(String linkImg) IOException qaytardi)", false));
                                        }
                                    }
                                } catch (Exception e) {
                                    WriteErrors writeErrors = new WriteErrors();
                                    writeErrors.write("inf.txt", new Date() + "\t" + mapLink.getKey() + "\t" + LocalDateTime.now() + "\t" + category + "\t" + "berilgan linkdan saytga kira olmadi (getTitleAndBodyByLink(String linkSite) IOException qaytardi)" + "\t" + userAgent);
                                    System.gc();

//                                infRepository.save(new Inf(null, mapLink.getKey(), null, null, null, "" + LocalDateTime.now(), category, "berilgan linkdan saytga kira olmadi (getTitleAndBodyByLink(String linkSite) IOException qaytardi)", false));
                                }
                                System.gc();
                                Thread.sleep(1000 * 5);
                            }
                        } catch (IOException e) {
                            WriteErrors writeErrors = new WriteErrors();
                            writeErrors.write("category.txt", new Date() + "\t" + category.getId() + "\t" + category.getName() + "\t" + category.getCategoryName() + "\t" + category.getLengthBaseUrl() + "\t" + "asosiy saytga kira olmadi (getAllSiteAndImgLinks(String url, int lengthBaseUrl) IOException qaytardi)" + "\t" + category.getSites() + "\t" + userAgent);
                            System.gc();
//                        categoryRepository.save(new Category(category.getId(), category.getName(), category.getCategoryName(), category.getLengthBaseUrl(), "asosiy saytga kira olmadi (getAllSiteAndImgLinks(String url, int lengthBaseUrl) IOException qaytardi)", category.getSites()));
                        }
                    }
                    System.gc();
                    Thread.sleep(1000 * 60 * 10);
                }
                System.out.println("***********************************************************************************************************************************************************************");
                System.gc();
            }
        } catch (IOException ignored) {
            System.gc();
        } catch (InterruptedException e) {
            System.gc();
            System.out.println("Thread.sleep(1000 * 60 * 10) qaytardi: " + e.getMessage());
        }

//        }).start();

    }

    private static Map<String, String> getAllSiteAndImgLinks(String url, int lengthBaseUrl, boolean b) throws IOException {
        String[] split = url.split("/");
        StringBuilder baseUrl = new StringBuilder();
        for (int i = 0; i < lengthBaseUrl; i++) {
            baseUrl.append(split[i]).append("/");
        }
        System.out.println(baseUrl);
        Selenium selenium = new Selenium();
        Document doc;
        if (b) {
            doc = Jsoup.connect(url).get();
        } else {
            doc = Jsoup.parse(selenium.getOneSite(url));
        }
        Elements links = doc.select("a");
        Map<String, String> linksMap = new HashMap<>();
        baseUrl.deleteCharAt(baseUrl.length() - 1);
        for (Element link : links) {
            Element img = link.selectFirst("img");
            if (img != null) {
                String hrefA = link.attr("abs:href");
                if (hrefA.isEmpty()) {
                    hrefA = link.attr("href");
                    if (hrefA.length() > 5) {
                        hrefA = baseUrl + hrefA;
                    }
                }
                if (hrefA.split("/").length > lengthBaseUrl) {
                    if (hrefA.startsWith(baseUrl.toString())) {
                        String attr = img.attr("abs:src");
                        if (!attr.isEmpty()) {
                            String attrImg = img.attr("abs:src");
                            linksMap.put(hrefA, attrImg);
                        } else {
                            String attrImg = img.attr("abs:data-src");
                            linksMap.put(hrefA, attrImg);
                        }
                    }
                }
            }
        }
        if (linksMap.isEmpty() && b) {
            getAllSiteAndImgLinks(url, 4, false);
        }
        System.gc();
        return linksMap;
    }

    private Map<String, String> getPresidentSite(String link, String userAgent) throws IOException {
        Document doc = Jsoup.connect(link).userAgent(userAgent).get();
        Map<String, String> linksMap = new HashMap<>();
        for (Element div : doc.select("div")) {
            Elements a = div.select("a");
            if (!a.isEmpty()) {
                Elements img = div.select("img");
                String attrImg = img.attr("abs:src");
                if (attrImg.contains("uploads")) {
                    String hrefA = a.get(0).attr("abs:href");
                    linksMap.put(hrefA, attrImg);
                }
            }
        }
        System.gc();
        return linksMap;
    }

    private ParsDto getTitleAndBodyByLink(String linkSite, String userAgent) throws IOException, BoilerpipeProcessingException {
        Document doc = Jsoup.connect(linkSite).userAgent(userAgent).get();
        ParsDto parsDto = new ParsDto();
        parsDto.setTitle(doc.title());
        String mainContent = ArticleExtractor.INSTANCE.getText(doc.outerHtml());
        parsDto.setContent(mainContent);
        System.gc();
        return parsDto;
    }

    private byte[] getImgByLink(String linkImg) throws IOException {
        URL url = new URL(linkImg);
        URLConnection connection = url.openConnection();
        connection.connect();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = connection.getInputStream();
        byte[] buffer = new byte[16384];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        System.gc();
        return byteArrayOutputStream.toByteArray();
    }

    public ResponseEntity<Resource> downloadNewInf() {

        Weather currentWeather = weatherService.getCurrentWeather();

        Optional<Rade> radeByDateAndUSD = radeRepository.findRadeByCcy("USD");
        Optional<Rade> radeByDateAndRUB = radeRepository.findRadeByCcy("RUB");
        Optional<Rade> radeByDateAndEUR = radeRepository.findRadeByCcy("EUR");
        List<Inf> byDownloadedAndSitesId = infRepository.findByDownloaded(false);
        List<Rade> radeList = new ArrayList<>();
        if (radeByDateAndUSD.isEmpty() || radeByDateAndRUB.isEmpty() || radeByDateAndEUR.isEmpty()) {
            System.out.println("log");
            return null;
        }
        radeList.add(radeByDateAndUSD.get());
        radeList.add(radeByDateAndRUB.get());
        radeList.add(radeByDateAndEUR.get());

        ResultPars resultPars = new ResultPars(currentWeather, byDownloadedAndSitesId, radeList);

        new Thread(() -> {
            for (Inf inf : byDownloadedAndSitesId) {
                infRepository.save(new Inf(inf.getId(), inf.getLink(), inf.getTitle(), inf.getImg(), inf.getContents(), inf.getCreateDate(), inf.getCategory(), inf.getError(), true));
                System.gc();
            }
            System.gc();
        }).start();

//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String jsonString = objectMapper.writeValueAsString(resultPars);
//            ByteArrayResource resource = new ByteArrayResource(jsonString.getBytes());
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//            headers.add("Pragma", "no-cache");
//            headers.add("Expires", "0");
//            headers.add("Content-Disposition", "attachment; filename=\"pars_data.json\"");
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentLength(resource.contentLength())
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);
//        } catch (IOException e) {
//            System.gc();
//            System.out.println("json yoza olmadi: " + e.getMessage());
//        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Specify UTF-8 encoding
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultPars);
            ByteArrayResource resource = new ByteArrayResource(jsonString.getBytes(StandardCharsets.UTF_8));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment; filename=\"pars_data.json\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            System.gc();
            System.out.println("json yoza olmadi: " + e.getMessage());
        }

        System.gc();
        return null;
    }
}
