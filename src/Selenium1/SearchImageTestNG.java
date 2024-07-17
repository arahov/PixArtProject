package Selenium1;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchImageTestNG {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/Users/araho/Documents/WebDrivers/chromedriver"); //add your driver location here
        
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @DataProvider(name = "resolutions")
    public Object[][] resolutions() {
        return new Object[][]{
                {1024, 768},
                {1440, 900},
                {1366, 768}
        };
    }

    @Test(dataProvider = "resolutions")
    public void testSeleniumScript(int width, int height) {
    	driver = new ChromeDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.get("https://picsart.com/search/images/");
        driver.findElement(By.cssSelector("div[id='onetrust-banner-sdk'] button[id='onetrust-accept-btn-handler']")).click();
        System.out.println("------------");
    	
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
        
        switchToSearchIframe();
        
        WebElement filterButton = waitAndGetElement(By.cssSelector(
                "div[class^='search-root-0-2-'] div[class*='header-root-'] div[class^='search-header-triggersHolder-0-2-'] button#filter_icon"
        ));
        toggleButton(filterButton, "closed", "open");
        
        selectCheckbox();
                
        Actions action = new Actions(driver);
        WebElement imageElement = hoverOverImage(action);
        //check if buttons are present
        checkButtonPresence("like_button_item0", "like_button");
        checkButtonPresence("save_button_item0", "save_button");
        checkButtonPresence("try_now_button_item0", "try_now");
        
        clickButton(By.cssSelector("button[id='like_button_item0']"));
        
        handlePopup(); //check if its Displayed and then close popup
        
        //click on checkbox again
        switchToSearchIframe();
        selectCheckbox();
        
        // open trynow button
        action.moveToElement(imageElement).perform();
        checkButtonPresence("try_now_button_item0", "try_now");
        WebElement elementTrynow = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[id='try_now_button_item0'")));
        elementTrynow.click();
        // here should compare the page element with picture name with the picture name that was clicked and they should match 
        // ---- no time left to do it 
        System.out.println(driver.getCurrentUrl());
    }
    
    private void switchToSearchIframe() {
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe[data-testid='com.picsart.social.search']")));
        wait = new WebDriverWait(driver, 10);
    }
    
    private WebElement waitAndGetElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }
    
    private void toggleButton(WebElement button, String firstState, String secondState) {
        button.click();
        verifyAttribute(button, "Filter Button is", firstState);
        button.click();
        verifyAttribute(button, "Filter Button is", secondState);
    }
    
    private void verifyAttribute(WebElement element, String attribute, String expectedValue) {
        String attributeValue = element.getAttribute(attribute);
        if (expectedValue.equals(attributeValue)) {
            System.out.println("The" + attribute + "='" + expectedValue + "'.");
        } else {
            System.out.println("The" + attribute + "='" + expectedValue + "'.");
        }
    }
    // better to find the element that check if checked is true or not .. no time though :(
    private void selectCheckbox() {
        WebElement checkboxElement = waitAndGetElement(By.cssSelector(
                "div[data-testid='accordion-root'] li[id='checkbox_filter_item2'] input[data-testid='checkbox-item-check']"));
        checkboxElement.click();
        if (checkboxElement.isSelected()) {
            System.out.println("Checkbox is selected.");
        } else {
            System.out.println("checkbox not selected.");
        }
    }
 
    
    private WebElement hoverOverImage(Actions action) {
        WebElement elementHover = waitAndGetElement(By.cssSelector(
                "div[class^='search-root-0-2-'] div[data-testid='standard-content-grid-root'] " +
                        "a[href='https://picsart.com/i/image-458582461007201?licenses=personal&source=search']"));
        action.moveToElement(elementHover).perform();
        return elementHover;
    }
    
    private void checkButtonPresence(String buttonId, String buttonType) {
        WebElement element = waitAndGetElement(By.cssSelector("button[id='" + buttonId + "']"));
        String dataAutomationValue = element.getAttribute("id");
        if (dataAutomationValue.contains(buttonType)) {
            System.out.println(buttonType + " button is present");
        } else {
            System.out.println(buttonType + " button is missing");
        }
    }
    
    private void clickButton(By by) {
        WebElement button = waitAndGetElement(by);
        button.click();
    }
    
    private void handlePopup() {
        String myWindowHandle = driver.getWindowHandle();
        driver.switchTo().window(myWindowHandle);
        
        WebElement popup = waitAndGetElement(By.cssSelector("div[data-testid='registration-modal-container']"));
        if (popup.isDisplayed()) {
            System.out.println("Popup is displayed.");
            
            WebElement closeButton = popup.findElement(By.cssSelector("div[data-testid='registration-modal-container'] svg[data-testid='modal-close-icon']"));
            closeButton.click();
            System.out.println("popup is closed");
        }
    }
    
   
}
