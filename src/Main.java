import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {


        WebDriverManager.chromedriver().setup();
        WebDriver driver =  new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://telugu.timesnownews.com/");
        String currentURL = driver.getCurrentUrl();
        System.out.println("Current URL = " + currentURL);
        String pageSource = driver.getPageSource();
        System.out.println("Page Source = " + pageSource);
        String title = driver.getTitle();
        System.out.println("Title =  " + title);
        List<WebElement> total_Links = driver.findElements(By.tagName("link"));
        int count = total_Links.size();
        System.out.println("Number of links available on this Website: " + count);
        List<String> linkList = new ArrayList<>();
        List<String> not200Links = new ArrayList<>();
        Map<String, Integer> linkStatus   = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String link = total_Links.get(i).getAttribute("href");
            linkList.add(link);
        }
        for (String link : linkList) {
            System.out.println(link);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(link))
                    .timeout(Duration.of(10, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            System.out.println("Status Code: " + statusCode);
            System.out.println("Headers" + response.headers());
            linkStatus.put(link, statusCode);

        }
        if(linkStatus.size() > 0){
            System.out.println("Links and Status Code");
            for(Map.Entry<String, Integer> entry: linkStatus.entrySet()){
                System.out.println("Links: " + entry.getKey() + ", Status Code: " + entry.getValue());
            }
        }else{
            System.out.println("All Links Have 200 Status Code");
        }

    driver.quit();
    }
}