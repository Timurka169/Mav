package ru.yandex;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OneNewsPageTest {

    public static WebDriver browser;
    public static List<WebElement> cards;
    private static String h2;

    public static class LinkUtil {
        public static int getResponseCode(String link) {
            URL url;
            HttpURLConnection con = null;
            Integer responsecode = 0;
            try {
                url = new URL(link);
                con = (HttpURLConnection) url.openConnection();
                responsecode = con.getResponseCode();
            } catch (Exception e) {
                // skip
            } finally {
                if (null != con)
                    con.disconnect();
            }
            return responsecode;
        }
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/driver/chromedriver.exe"); //read me! to run, you need chrome version 86.0.42* if you have your own driver delete this line
        browser = new ChromeDriver();
        browser.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        browser.manage().window().maximize();
    }

    @Before
    public void before(){
        browser.get("https://yandex.ru/news/");
        WebElement a = browser.findElement(By.xpath("//span[text()='Технологии']"));
        a.click();
        //сохраняем все карточки с новостями в переменную cards
        cards = browser.findElements(By.xpath("//article[contains(@class,'news-card_single') or contains(@class,'news-card_double')]"));

    }

    @Test
    public void textComparison() {
        //сохраняем заголовок статьи до навигации
        h2 = cards.get(0).findElement(By.xpath(".//h2[@class=\"news-card__title\"]")).getText();
        cards.get(0).click();
        //получаем заголовок карточки с конкретной новостью
        String h1 = browser.findElement(By.xpath("//h1[@class=\"mg-story__title\"]")).getText();
        //сравниваем заголовки
        Assert.assertEquals(h1 , h2);
    }

    @Test
    public void sourceLink() {
       cards.get(0).click();
        //проверка что ссылок больше 1
       List<WebElement> sourceNewsLinks = browser.findElements(By.xpath("//div[@class=\"mg-snippet mg-snippet_without-text news-story__snippet\"]/div[@class=\"mg-snippet__wrapper\"]/div[@class=\"mg-snippet__content\"]/a[@href]"));
       Assert.assertTrue(sourceNewsLinks.size() > 0);
    }

    @Test
    public void brokenLinks() {
        //открыли конкретную новость
        cards.get(0).click();
        //получили ссылку на кнопку "показать ещё"
        WebElement button = browser.findElement(By.xpath("//button[@class=\"Button2 mg-button mg-button_theme_secondary mg-button_pin_round news-story__more\"]"));
        //нажали на кнопку
        button.click();
        //получили ссылки на новости
        List<WebElement> links = browser.findElements(By.xpath("//div[@class=\"mg-snippet mg-snippet_without-text news-story__snippet\"]/div[@class=\"mg-snippet__wrapper\"]/div[@class=\"mg-snippet__content\"]/a[@href]"));
        Map<Integer, List<String>> map = links.stream()
                .map(ele -> ele.getAttribute("href")) // получаем их значение
                .map(String::trim)                    // обрезаем конечные проберы
                .distinct()                           // оставляем уникальные ссылки
                .collect(Collectors.groupingBy(LinkUtil::getResponseCode)); // группируем в зависимости от кода ответа
        Assert.assertEquals(map.get(200).size(), links.size() );
    }

    @AfterClass
    public static void  after(){
        browser.quit();
    }
}
