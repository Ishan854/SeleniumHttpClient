import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


import java.io.FileWriter;
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
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {


        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
//        driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
//        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
//        driver.get("https://telugu.timesnownews.com/");
          driver.get("https://www.zoomtventertainment.com/");
        String currentURL = driver.getCurrentUrl();
        System.out.println("Current URL = " + currentURL);
        String pageSource = driver.getPageSource();
        System.out.println("Page Source = " + pageSource);
        String title = driver.getTitle();
        System.out.println("Title =  " + title);
        List<WebElement> total_Links = driver.findElements(By.tagName("a"));
        int count = total_Links.size();
        System.out.println("Number of links available on this Website: " + count);
        List<String> linkList = new ArrayList<>();
        List<String> not200Links = new ArrayList<>();
        Map<String, Integer> linkStatus = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String link = total_Links.get(i).getAttribute("href");
            linkList.add(link);
        }

        try (FileWriter fileWriter = new FileWriter("ZoomEntertainmentStatus.txt")) {
            for (String link : linkList) {
                System.out.println(link);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(link))
                        .timeout(Duration.of(300, ChronoUnit.SECONDS))
                        .GET()
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                System.out.println("Status Code: " + statusCode);
                System.out.println("Headers" + response.headers());
                linkStatus.put(link, statusCode);

                fileWriter.write(link + "\t" + statusCode + "\n");
            }
        }
        if (linkStatus.size() > 0) {
            System.out.println("Links and Status Code");
            for (Map.Entry<String, Integer> entry : linkStatus.entrySet()) {
                System.out.println("Links: " + entry.getKey() + ", Status Code: " + entry.getValue());

            }

        } else {
            System.out.println("All Links Have 200 Status Code");
        }

        driver.quit();
    }
}