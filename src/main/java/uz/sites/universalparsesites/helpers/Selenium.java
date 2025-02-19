package uz.sites.universalparsesites.helpers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Selenium {

    public String getOneSite(String url) throws MalformedURLException {
        WebDriver driver = getDriver();
        driver.get(url);
        String pageSource = driver.getPageSource();
        driver.quit();
        return pageSource;
    }

    public List<String> getMoreSite(List<String> urls) throws MalformedURLException {
        List<String> pageSources = new ArrayList<>();
        WebDriver driver = getDriver();
        for (String url : urls) {
            driver.get(url);
            pageSources.add(driver.getPageSource());
        }
        driver.quit();
        return pageSources;
    }

    private WebDriver getDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");  // Yangi headless rejim
        options.addArguments("--disable-gpu");  // GPU kerak emas
        options.addArguments("--no-sandbox");  // Sandbox ishlatmaslik
        options.addArguments("--disable-dev-shm-usage");  // Shared memory limit oldini olish
        options.addArguments("--disable-features=WebGL");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-extensions");   // Kengaytmalarni o'chirish
        return new RemoteWebDriver(new URL("http://localhost:4444"), options);
    }
}
