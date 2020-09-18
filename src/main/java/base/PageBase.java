/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package base;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.Reporter;

public class PageBase {
	public JavascriptExecutor jse;
	public Actions actions;
	public static WebDriverWait wait;
	public WebDriver driver;
	public static WebDriver jsWaitDriver;
	public static WebDriverWait jsWait;
	public static JavascriptExecutor jsExec;

	//Super constructor
	public PageBase(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	/**
	 * Attempts to Click on a certain web element using selenium webdriver
	 * @param button the locator of the webElement under test (By xpath, id,name ...etc)
	 */
	protected static void clickButton(WebElement button) {
		try {
			Reporter.Log("Click on " + button.getText());

			if (button.isDisplayed()) {
				wait.until(ExpectedConditions.visibilityOf(button));
				button.click();
			}
		} catch (Exception e) {
			Reporter.Log("Failed to Click on " + button.getText() + ".");
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to insert text in a certain Field using seleniumWebdriver
	 * @param textElement the locator of the webElement under test (By xpath, id,name ...etc)
	 * @param value the value needed to be inserted in the text field
	 */
	protected static void typeTextInWebElement(WebElement textElement, String value) {
		try {
			Reporter.Log("Insert " + value + " in text field");
			if (textElement.isDisplayed()) {
				textElement.clear();
				textElement.sendKeys(value);
			}
		} catch (Exception e) {
			Reporter.Log("Failed to insert " + value + " in text field.");
			e.printStackTrace();
		}
	}

	/**
	 *  Attempts to select webElement and click rightClick with mouse on it
	 * @param elementLocator selected webElement
	 */
	public void rightClick(WebElement elementLocator) {
		try {
			actions = new Actions(driver).contextClick(elementLocator);
			actions.build().perform();

			Reporter.Log("[Right Click] sucessfully performed on the element");
		} catch (StaleElementReferenceException e) {
			Reporter.Log(e.toString());
			Reporter.Log("[Right Click] :Element is not attached to the page document " + e.getStackTrace());

		} catch (NoSuchElementException e) {
			Reporter.Log(e.toString());
			Reporter.Log("[Right Click] : Element " + elementLocator + " was not found in DOM " + e.getStackTrace());

		} catch (Exception e) {
			Reporter.Log(e.toString());
			Reporter.Log("[Right Click] : Element " + elementLocator + " was not clickable " + e.getStackTrace());

		}
	}

	/**
	 *  Attempts tp select webelement and click double with mouse on it
	 * @param elementLocator selected webelement
	 */
	protected void doubleClick(WebElement element) {
		try {
			actions = new Actions(driver);
			actions.doubleClick(element).perform();

			Reporter.Log("[Double Click] sucessfully performed on the element");

		} catch (StaleElementReferenceException e) {
			Reporter.Log(e.toString());
			Reporter.Log("[Double Click] :Element is not attached to the page document " + e.getStackTrace());

		} catch (NoSuchElementException e) {
			Reporter.Log(e.toString());
			Reporter.Log("[Double Click] : Element " + element + " was not found in DOM " + e.getStackTrace());

		} catch (Exception e) {
			Reporter.Log(e.toString());
			Reporter.Log("[Double Click] :Element " + element + " was not clickable " + e.getStackTrace());

		}
	}

	public void convertElementEditable(String elementName) {
		jse.executeScript("arguments[0].removeAttribute('readonly','readonly')", elementName);
	}

	protected void scrollToBottom() {
		//JavascriptExecutor js = (JavascriptExecutor) driver;
		jse.executeScript("scrollBy(0,1500)");
	}	

	/**
	 * Method to simulate the copy and paste using keyboard buttons CTRL+C & CTRL+V
	 * @param textField insert the field that contains the value to be copied
	 * @return
	 * @throws AWTException
	 * @throws IOException
	 * @throws UnsupportedFlavorException
	 */
	public String copyTextUsingRobot(WebElement textField) throws AWTException, IOException, UnsupportedFlavorException {
		Robot robot = new Robot();
		actions.moveToElement(textField).click().build().perform();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);// to copy the value
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(500);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_C);// to copy the value
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(500);

		// paste the value from clipboard to variable
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor flavor = DataFlavor.stringFlavor;
		String txt = (String) clipboard.getData(flavor);
		return txt;
	}


	/**
	 * Method to make the page holds till it loads 
	 * @param driver	Select WebDriver driver
	 * @param timeout select the time needed to make script waits
	 */
	public void waitforPageLoad(WebDriver driver, int timeout) {
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
	}

	/**
	 * Method to simulate the Thread.sleep
	 * @param milliSeconds insert the required time to make script sleeps
	 */
	public void holdScript(Integer milliSeconds) {
		long secondsLong = (long) milliSeconds;
		try {
			Thread.sleep(secondsLong);
		} catch (Exception e) {
			Reporter.Log(e.toString());
		}
	}

	/**
	 * Selects an element from a dropdown list using its displayed text
	 * @param elementLocator the locator of the webElement under test (By xpath, id,
	 *                       selector, name ...etc)
	 * @param text           the text of the choice that you need to select from the
	 *                       target dropDown menu
	 */
	public static void selectByVisibleText(WebElement elementLocator, String text) {
		try {
			wait.until(ExpectedConditions.visibilityOf(elementLocator));
			Select dropSelect = new Select(elementLocator);
			dropSelect.selectByVisibleText(text);
			Reporter.Log("Selected Value from list is : " + text );

		} catch (NoSuchElementException e) {
			Reporter.Log("Value not found in the dropdown menu.");
		}
	}
	//you can add below more methods for different types of inputs like checkboxes or dropdown lists and so on
}


