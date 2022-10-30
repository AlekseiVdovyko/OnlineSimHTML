package ru.edu.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class OnlineSimProvider {

    Map<String, String> mapCountry = new LinkedHashMap<>();
    Map<String, Map<String, String>> allInfo = new LinkedHashMap<>();

    String url = "https://onlinesim.ru/price-list";

    public OnlineSimProvider() {
    }

    public void getInfo() {

        try {

            System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
            WebDriver webDriver = new ChromeDriver();
            webDriver.get(url);

            // получаем html-страницу со списком всех стран
            for (int i = 1; i < 2; i++) {
                Thread.sleep(1000);
                WebElement webElement = webDriver.findElement(By.xpath("//*[@id=\"country-" + i +"\"]/span"));
                webElement.click();
            }

            Document document = Jsoup.parse(webDriver.getPageSource());

            Elements elements = new Elements();

            // из страницы сохраняем полностью элементы содержащие код страны и название страны
            for (int a = 1; a <= 1000; a++) {
                Element element = document.getElementById("country-" + a);
                if (element == null) {
                    continue;
                }

                elements.add(element);
            }

            // из элементов в Map сохраняем код страны и название страны
            for (Element element : elements) {

                String code = element.id();
                String country = element.text();
                mapCountry.put(code, country);
            }

            // проходимся по страничке с названием стран, кликаем по каждой стране и получаем html-страницу с наименованием сервисов и их ценой
            for (Map.Entry<String, String> country : mapCountry.entrySet()) {
//                Thread.sleep(500);
                String countryNumber = country.getKey().substring(8);
                String xPath = "//*[@id=\"country-" + countryNumber +"\"]/span";

                WebElement webElement2 = webDriver.findElement(By.xpath(xPath));
                webElement2.click();

                new Actions(webDriver).scrollByAmount(0, 26).perform();
                Document document1 = Jsoup.parse(webDriver.getPageSource());

                Elements elements1 = document1.getElementsByClass("service-block");

                // в Map добавляем название страны
                allInfo.put(country.getValue(), new LinkedHashMap<>());

                for (Element element2 : elements1) {

                    String service = element2.getElementsByClass("price-name").text();
                    String priceService = element2.getElementsByClass("price-text").text();

                    // в Map к стране добавляем название сервиса и его стоимость
                    allInfo.get(country.getValue()).put(service, priceService);

                }
            }

//            for (Map.Entry<String, Map<String, String>> info : allInfo.entrySet()) {
//                System.out.println(info);
//            }

            webDriver.quit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // запись полученных данных в файл и приведение к требуемую виду
    public void writeFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write("{");
            for (Map.Entry<String, Map<String, String>> info : allInfo.entrySet()) {
                writer.newLine();
                writer.write(" ");
                writer.write("\"");
                writer.write(info.getKey());
                writer.write("\" : {");
                writer.newLine();
                for (Map.Entry<String, String> info2 : info.getValue().entrySet()) {
                    writer.write(" \"");
                    writer.write(info2.getKey());
                    writer.write("\" : ");
                    writer.write(info2.getValue().substring(0, info2.getValue().length()-1));
                    writer.write(",");
                    writer.newLine();
                }
                writer.write(" },");
            }
            writer.newLine();
            writer.write("}");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
