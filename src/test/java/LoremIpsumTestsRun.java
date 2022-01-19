import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static org.openqa.selenium.By.xpath;
import static utils.WaitingUtils.waitForPageLoadComplete;
import static utils.WaitingUtils.waitVisibilityOfElements;

public class LoremIpsumTestsRun {

    private WebDriver driver;
    private static final String LOREN_IPSUM_OPEN = "https://lipsum.com";


    private static final String RUSSIAN_LINK = "//div[@id='Languages']//a[contains(@href,'ru.lipsum')]";
    private static final String LIPSUM_PARAGRAPH_ELEMENT = "//div[@id='lipsum']//p[1]";
    private static final String PANES_PARAGRAPH_ELEMENT = "//div[@id='Panes']//div[1]";
    private static final String AMOUNT_INPUT = "//input[@id='amount']";
    private static final String WORDS_RADIOBUTTON = "//input[@id='words']";
    private static final String BYTES_RADIOBUTTON = "//input[@id='bytes']";
    private static final String GENERATE_LOREM_IPSUM_BUTTON = "//input[@id='generate']";
    private static final String GENERATED_RESULT_ELEMENT = "//div[@id='lipsum']//p";
    private static final String START_WITH_LOREM_CHECKBOX = "//input[@id='start']";

    @BeforeTest
    public void profileSetUp() {
        chromedriver().setup();
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window();
        driver.get(LOREN_IPSUM_OPEN);
    }

    @Test
    public void switchOnRuLangAndVerifyThatParagraphContainsSpecificText() {
        driver.findElement(xpath(RUSSIAN_LINK)).click();
        waitForPageLoadComplete(driver);
        String paragraphText = driver.findElement(xpath(PANES_PARAGRAPH_ELEMENT)).getText();
        Assert.assertTrue(paragraphText.contains("рыба"));
    }

    @Test
    public void verifyThatFirstParagraphStartsWithSpecificText() {
        driver.findElement(xpath(GENERATE_LOREM_IPSUM_BUTTON)).click();
        String elementTextParagraph = driver.findElement(xpath(LIPSUM_PARAGRAPH_ELEMENT)).getText();
        Assert.assertTrue(elementTextParagraph.startsWith("Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
    }

    @Test
    public void verifyThatGeneratedResultsConsistOfTenWords() {
        int wordsAmount = 10;
        driver.findElement(xpath(WORDS_RADIOBUTTON)).click();
        driver.findElement(xpath(AMOUNT_INPUT)).click();
        driver.findElement(xpath(AMOUNT_INPUT)).clear();
        driver.findElement(xpath(AMOUNT_INPUT)).sendKeys(String.valueOf(wordsAmount));
        driver.findElement(xpath(GENERATE_LOREM_IPSUM_BUTTON)).click();
        waitForPageLoadComplete(driver);
        String generatedResultText = driver.findElement(xpath(GENERATED_RESULT_ELEMENT)).getText();
        String[] splittedGeneratedResults = generatedResultText.split(" ");
        Assert.assertEquals(wordsAmount, splittedGeneratedResults.length);
    }

    @Test
    public void verifyThatGeneratedResultsConsistOfFiveWordsByDefault() {
        int wordsAmount = 5;
        driver.findElement(xpath(WORDS_RADIOBUTTON)).click();
        driver.findElement(xpath(AMOUNT_INPUT)).click();
        driver.findElement(xpath(AMOUNT_INPUT)).clear();
        driver.findElement(xpath(AMOUNT_INPUT)).sendKeys(String.valueOf(wordsAmount));
        driver.findElement(xpath(GENERATE_LOREM_IPSUM_BUTTON)).click();
        waitForPageLoadComplete(driver);
        String generatedResultText = driver.findElement(xpath(GENERATED_RESULT_ELEMENT)).getText();
        String[] splittedGeneratedResults = generatedResultText.split(" ");
        Assert.assertEquals(wordsAmount, splittedGeneratedResults.length);
    }

    @Test
    public void verifyThatGeneratedResultsConsistOfAmountOfBytes() {
        byte byteAmount = 4;
        driver.findElement(xpath(BYTES_RADIOBUTTON)).click();
        driver.findElement(xpath(AMOUNT_INPUT)).click();
        driver.findElement(xpath(AMOUNT_INPUT)).clear();
        driver.findElement(xpath(AMOUNT_INPUT)).sendKeys(String.valueOf(byteAmount));
        driver.findElement(xpath(GENERATE_LOREM_IPSUM_BUTTON)).click();
        waitForPageLoadComplete(driver);
        String generatedResultText = driver.findElement(xpath(GENERATED_RESULT_ELEMENT)).getText();
        byte[] bytesFromText = generatedResultText.getBytes();
        Assert.assertEquals(byteAmount, bytesFromText.length);
    }

    @Test
    public void verifyThatGeneratedResultsStartsNotWithLorem() {
        driver.findElement(xpath(START_WITH_LOREM_CHECKBOX)).click();
        driver.findElement(xpath(GENERATE_LOREM_IPSUM_BUTTON)).click();
        waitForPageLoadComplete(driver);
        List<WebElement> generatedResults = driver.findElements(xpath(GENERATED_RESULT_ELEMENT));
//        SoftAssertions softly = new SoftAssertions();
        generatedResults.forEach(paragraph -> Assert.assertFalse(paragraph.getText().startsWith("Lorem ipsum")));
    }

    @Test()
    public void verifyAverageNumberOfParagraphsContainingLorem() {
        int averageCount = 0;
        int countersSum = 0;
        for (int i = 1; i <= 10; i++) {
            driver.findElement(xpath(GENERATE_LOREM_IPSUM_BUTTON)).click();
            waitForPageLoadComplete(driver);
            List<WebElement> generatedResults = waitVisibilityOfElements(driver, driver.findElements(xpath(GENERATED_RESULT_ELEMENT)));
            int counter = 0;
            for (WebElement paragraph : generatedResults) {
                String text = paragraph.getText();
                if (text.toLowerCase().contains("lorem")) {
                    counter++;
                }
            }
            countersSum = countersSum + counter;
            averageCount = countersSum / i;
            driver.navigate().back();
        }
        Assert.assertTrue(averageCount >= 2);
    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }
}
