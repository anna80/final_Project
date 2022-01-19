package pages;

import org.openqa.selenium.WebDriver;

import static org.openqa.selenium.support.PageFactory.initElements;

public class LipsumPage {

    protected WebDriver driver;

    public LipsumPage(WebDriver driver) {
        this.driver = driver;
        initElements(driver, this);
    }
}


