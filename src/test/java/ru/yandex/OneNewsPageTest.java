package ru.yandex;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OneNewsPageTest {

    public static WebDriver browser;
    public static List<WebElement> cards;
    public static JavascriptExecutor js;
    private static String h2;
    private static int i;

    // Тут поменять путь для сохранения скриншотов!
    public static void screenshot(String nameFileScreen) {
        try {
            File screenshot = ((TakesScreenshot)browser).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("src/test/resources/driver/screens/" + nameFileScreen + ".png")); //тут поменять путь "C:\\screen\\"
        } catch (Exception e) {
            //skip
        }
    }
    /**
     * Метод - выделяет webElement красным, делает скриншот и убирает выделение.
     * @param webElement - Один элемент для выделения
     * @param nameFileScreen - Имя скриншота
     */
    public static void screenshot(WebElement webElement, String nameFileScreen) {
        //WebElement webElement = driver.findElement(elementBy);
        String originalStyle = webElement.getAttribute("style");
        //JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", originalStyle + "border: 2px solid red;");

        //Do something e.g. make a screenshot
        screenshot(nameFileScreen);

        //Reset style
        js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", originalStyle);
    }
    /**
     * Метод - выделяет webElement красным, делает скриншот и убирает выделение.
     * @param webElements - Элементы для выделения
     * @param nameFileScreen - Имя скриншота
     */
    public static void screenshot(List<WebElement> webElements, String nameFileScreen) {
        for(WebElement e : webElements){
            js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", e, "style", e.getAttribute("style") + "border: 2px solid red;");
        }

        screenshot(nameFileScreen);

    }

    //класс для проверки битых ссылок
    public static class LinkUtil {
        public static int getResponseCode(String link) {
            URL url;
            HttpURLConnection con = null;
            Integer responsecode = 0; //Integer — это ссылочный тип данных, и переменная данного типа может быть null / Integer — это ссылочный тип данных, и его переменные хранят ссылки на значения, а не сами значения (объекты)
            try {
                url = new URL(link); //В процессе создания объекта проверяется заданный адрес URL.. Если адрес указан неверно, возникает исключение MalformedURLException
                con = (HttpURLConnection) url.openConnection();//Чтобы получить объект класса HttpURLConnection, следует вызвать метод openConnection() для объекта типа URL, как описано в данной статье, но результат нужно привести к типу HttpURLConnection
                responsecode = con.getResponseCode(); //getResponseCode() - возвращает код ответа по протоколу НТТР. Если код ответа не может быть получен, возвращается значение - 1. При разрыве соединения генерируется исключение типа IОЕхсерtiоn.
                //присваеваем ссылочной переменной для удобства, по ка не знаем что будем делать с ней. Храним ссылку
            } catch (Exception e) {
                // skip
            } finally {
                if (null != con)
                    con.disconnect();//если что то есть - разорвать соединие
            }
            return responsecode;
        }
    }



    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/driver/chromedriver.exe"); //read me! to run, you need chrome version 86.0.42* if you have your own driver delete this line
        browser = new ChromeDriver();
        browser.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        browser.manage().window().maximize();
        js = ((JavascriptExecutor) browser);
    }

    @Before
    public void before(){
        browser.get("https://yandex.ru/news/");
        screenshot( i + ".1 before");
        WebElement a = browser.findElement(By.xpath("//span[text()='Технологии']"));
        screenshot(a, i + ".2 before");
        a.click();
        screenshot( i + ".3 before");
        //сохраняем все карточки с новостями в переменную cards
        cards = browser.findElements(By.xpath("//article[contains(@class,'news-card_single') or contains(@class,'news-card_double')]"));
        i++;
    }

    @Test
    public void textComparison() {
        //сохраняем заголовок статьи до навигации
        h2 = cards.get(0).findElement(By.xpath(".//h2[@class=\"news-card__title\"]")).getText();
        screenshot(cards.get(0).findElement(By.xpath(".//h2[@class=\"news-card__title\"]")),i+".1 textComparisonClick");
        cards.get(0).click();

        //получаем заголовок карточки с конкретной новостью
        String h1 = browser.findElement(By.xpath("//h1[@class=\"mg-story__title\"]")).getText();
        screenshot(browser.findElement(By.xpath("//h1[@class=\"mg-story__title\"]")), i+".2 textComparisonClick");

        i++;
        //сравниваем заголовки
        Assert.assertEquals(h1 , h2);

    }

    @Test
    public void sourceLink() {
        //проверка что ссылок больше 1
        screenshot(cards.get(0),i+".1 sourceLink" );
       cards.get(0).click();
       screenshot( i+".2 sourceLink");
       List<WebElement> sourceNewsLinks = browser.findElements(By.xpath("//div[@class=\"mg-snippet mg-snippet_without-text news-story__snippet\"]/div[@class=\"mg-snippet__wrapper\"]/div[@class=\"mg-snippet__content\"]/a[@href]"));
       //List<WebElement> sourceNewsLinks = browser.findElement(By.xpath("//article/div[@class=\"mg-story__body\"]/div[5]"));
       js.executeScript("window.scrollTo(0, 450)");
       //screenshot(browser.findElement(By.xpath("//article/div[@class=\"mg-story__body\"]/div[5]")), i+".3 sourceLink");
       screenshot(sourceNewsLinks, i+".3 sourceLink");
        i++;
       Assert.assertTrue(sourceNewsLinks.size() > 0);
    }

    @Test
    public void brokenLinks() {
        screenshot(cards.get(0),i+".1 brokenLinks" );
        //открыли конкретную новость
        cards.get(0).click();
        screenshot(i+".2 brokenLinks");
        //получили ссылку на кнопку "показать ещё"
        WebElement button = browser.findElement(By.xpath("//button[@class=\"Button2 mg-button mg-button_theme_secondary mg-button_pin_round news-story__more\"]"));
//        //прокрутили вниз
        js.executeScript("window.scrollTo(0, 700)");
        button.click();

        //нажали на кнопку
        //screenshot(button,i+".4 brokenLinks");


        screenshot(i+".3 brokenLinks");
        //screenshot(browser.findElement(By.xpath("//article/div[@class=\"mg-story__body\"]/div[5]")), i+".6 brokenLinks");
        //получили ссылки на новости
        List<WebElement> links = browser.findElements(By.xpath("//div[@class=\"mg-snippet mg-snippet_without-text news-story__snippet\"]/div[@class=\"mg-snippet__wrapper\"]/div[@class=\"mg-snippet__content\"]/a[@href]"));
        Map<Integer, List<String>> map = links
                .stream()//находим все элементы с атрибутом href
                .map(ele -> ele.getAttribute("href")) // получаем их значение
                .map(String::trim)                    // обрезаем конечные проберы
                .distinct()                           // оставляем уникальные ссылки
                .collect(Collectors.groupingBy(LinkUtil::getResponseCode)); // группируем в зависимости от кода ответа
        //js.executeScript("window.scrollTo(0, 700)");
        screenshot(links, i + ".4 brokenLinks");
        i++;
        Assert.assertEquals(map.get(200).size(), links.size() );
    }

    @Test
    public void screenCards() {
        Set<String> oldWindowsSet = browser.getWindowHandles();
        int namberCards = 0;
        for(WebElement e: cards) {
            ((JavascriptExecutor)browser).executeScript("arguments[0].scrollIntoView(true);", e);
            //сделали скриншот
            screenshot(e,i+"."+ namberCards + ".1 screenCards" );
            //открыли конкретную новость в новой вкладке
            String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
            e.findElement(By.xpath(".//a[@class=\"news-card__link\"]")).sendKeys(selectLinkOpeninNewTab);
            // получаем дескрипторы нового окна
            Set<String> newWindowsSet = browser.getWindowHandles();
            //удаляем старый сет дескрипторов, отсается новый
            newWindowsSet.removeAll(oldWindowsSet);
            //сохраняем его текст
            String newWindowHandle = newWindowsSet.iterator().next();
            //передаем управление браузере на новый дискриптор
            browser.switchTo().window(newWindowHandle);
            //сделали скриншот открытого окна
            screenshot(i+"."+ namberCards + ".2 screenCards");
            browser.close();
            browser.switchTo().window(oldWindowsSet.iterator().next());
            namberCards++;
        }
    }

    @AfterClass
    public static void  afterClass(){
        browser.quit();
    }
}
