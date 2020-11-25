package ru.yandex;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainPageTest {
    public static WebDriver browser;
    public static List<WebElement> cards;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/driver/chromedriver.exe"); //read me! to run, you need chrome version 86.0.42* if you have your own driver delete this line
        browser = new ChromeDriver();
        browser.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        JavascriptExecutor js = ((JavascriptExecutor) browser);
        browser.manage().window().maximize();
        //1.Открыть главную страницу.
        browser.get("https://yandex.ru/news/");
        WebElement a = browser.findElement(By.xpath("//span[text()='Технологии']"));
        //2.Перейти на вкладку “Технологии”
        a.click();
        //пролистываем до конца страницу
        List<WebElement> berth = browser.findElements(By.xpath("//div[@class=\"mg-footer__item news-footer__item\"]/a[text()=\"Как работают Яндекс.Новости\"]"));
        while(berth.size()==0){
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            berth = browser.findElements(By.xpath("//div[@class=\"mg-footer__item news-footer__item\"]/a[text()=\"Как работают Яндекс.Новости\"]"));
        }
        //сохраняем все карточки в переменную cards
        cards = browser.findElements(By.xpath("//article[contains(@class,'news-card_single') or contains(@class,'news-card_double')]"));// содержит или содержит
    }

    @Test
    public void cardsClick() {
    //проверка возможности навигироваться на горячую новость
        int i = 1;
        for(WebElement e : cards) {
            Assert.assertNotNull(e.findElement(By.xpath(".//a[@class=\"news-card__link\"]")).getAttribute("href"));
            System.out.println(i + "№"+ e.findElement(By.xpath(".//a[@class=\"news-card__link\"]")).getAttribute("href"));
            i++;
        }
    }



    @Test
    public void cardsCheckImg() {
    //проверка наличие у статьи картинки
        int i = 1;
        for(WebElement e : cards) {
            Assert.assertNotNull(e.findElement(By.xpath(".//img")).getAttribute("src"));                                                                             //возможно нужно было ещё открыть каждую картинку и проверить значение равно нулю или нет
            System.out.println(i + "№ " +e.findElement(By.xpath(".//img")).getAttribute("src"));
            i++;
        }

    }

    @Test
    public void cardsCheckText() {
    //проверка наличие описание статьи
        int i = 1;
        for(WebElement e : cards) {
            Assert.assertNotNull(e.findElement(By.xpath(".//div[@class=\"news-card__annotation\"]")).getText());
            System.out.println(i + "№ " + e.findElement(By.xpath(".//div[@class=\"news-card__annotation\"]")).getText());
            i++;
        }
    }

    @Test
    public void cardsCheckTextH2() {
    //проверка наличие заголовка статьи
        int i = 1;
        for(WebElement e : cards) {
            Assert.assertNotNull(e.findElement(By.xpath(".//h2[@class=\"news-card__title\"]")).getText());
            System.out.println(i + "№ " + e.findElement(By.xpath(".//h2[@class=\"news-card__title\"]")).getText());
            i++;
        }
    }

    @AfterClass
    public static void afterClass() {
        browser.quit();
    }

}
