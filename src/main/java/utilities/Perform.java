/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;


public class Perform {
    private static int defaultElementIdentificationTimeout = 2;
    private static int attemptsBeforeThrowingElementNotFoundException = 2;
    static WebDriver lastUsedDriver = null;
    
    // this will only be used for switching back to default content
   // static WebDriver lastUsedDriver = null;
    public Perform(WebDriver driver) {
        setLastUsedDriver(driver);
    }
    
    public static void setLastUsedDriver(WebDriver driver) {
        lastUsedDriver = driver;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// [private] Reporting Actions
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void passAction(WebDriver driver, By elementLocator, String actionName) {
	passAction(driver, elementLocator, actionName, null);
    }

    private static void passAction(WebDriver driver, By elementLocator, String actionName, String testData) {
	String message = "Action [" + actionName + "] successfully performed.";
	if (testData != null) {
	    message = message + " With the following test data [" + testData + "].";
	}
	takeScreenshot(driver, elementLocator, actionName, testData, true);
	Reporter.Log(message);
    }

    private static void failAction(WebDriver driver, String actionName) {
	failAction(driver, actionName, null);
    }

    private static void failAction(WebDriver driver, String actionName, String testData) {
	String message = "[" + actionName + "] failed.";
	if (testData != null) {
	    message = message + " With the following test data [" + testData + "].";
	}
	takeScreenshot(driver, null, actionName, testData, false);
	Reporter.Log(message);
	Assert.fail(message);
    }

    private static void takeScreenshot(WebDriver driver, By elementLocator, String actionName, String testData,
	    boolean passFailStatus) {
	if (passFailStatus) {
	    try {
		if ((elementLocator == null) && (testData == null)) {
		    // this only happens when switching to default content so there is no need to
		    // take a screenshot
		} else if (elementLocator != null) {
		    //ScreenshotManager.captureScreenShot(driver, elementLocator, actionName, true);
		} else {
		    //ScreenshotManager.captureScreenShot(driver, actionName, true);
		}
	    } catch (Exception e) {
				Reporter.Log(e.toString());
		Reporter.Log(
			"Failed to take a screenshot of the element as it doesn't exist anymore. Taking a screenshot of the whole page.");
		//ScreenshotManager.captureScreenShot(driver, actionName, true);
	    }
	} else {
	    //ScreenshotManager.captureScreenShot(driver, actionName, false);
	}
	lastUsedDriver = driver;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// [private] Preparation and Support Actions
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean identifyUniqueElement(WebDriver driver, By elementLocator) {
	return identifyUniqueElement(driver, elementLocator, attemptsBeforeThrowingElementNotFoundException, true);
    }

    private static boolean identifyUniqueElement(WebDriver driver, By elementLocator, int numberOfAttempts,
	    boolean checkForVisibility) {
	int matchingElementsCount = getMatchingElementsCount(driver, elementLocator, numberOfAttempts);

	switch (matchingElementsCount) {
	case 0:
	    failAction(driver, "identifyUniqueElement",
		    "zero elements found matching this locator [" + elementLocator + "].");
	    break;
	case 1:
	    // unique element found
	    if (checkForVisibility && !elementLocator.toString().contains("input[@type='file']")
		    && !elementLocator.equals(By.tagName("html"))) {
		// scroll element into viewPort
		((Locatable) driver.findElement(elementLocator)).getCoordinates().inViewPort();

		// check for visibility
		try {
		    (new WebDriverWait(driver, defaultElementIdentificationTimeout))
			    .until(ExpectedConditions.visibilityOfElementLocated(elementLocator));
		} catch (TimeoutException e) {
		    		Reporter.Log(e.toString());
		    failAction(driver, "identifyUniqueElement",
			    "unique element matching this locator [" + elementLocator + "] is not visible.");
		}
	    }

	    if (elementLocator != null) {
		// ScreenshotManager.storeElementScreenshotForAISupportedElementIdentification(driver,
		// elementLocator);
	    }

	    return true;
	default:
	    failAction(driver, "identifyUniqueElement",
		    "multiple elements found matching this locator [" + elementLocator + "].");
	    break;
	}
	return false;
    }

    private static int getMatchingElementsCount(WebDriver driver, By elementLocator, int numberOfAttempts) {
	return getMatchingElementsCount(driver, elementLocator, numberOfAttempts, true);
    }

    private static int getMatchingElementsCount(WebDriver driver, By elementLocator, int numberOfAttempts,
	    boolean waitForLazyLoading) {
	if (waitForLazyLoading) {
	    JSWaiter.waitForLazyLoading();
	}

	if (elementLocator != null) {
	    int matchingElementsCount = 0;
	    int i = 0;
	    do {
		try {
		    (new WebDriverWait(driver, defaultElementIdentificationTimeout))
			    .until(ExpectedConditions.presenceOfElementLocated(elementLocator));

		    matchingElementsCount = driver.findElements(elementLocator).size();
		} catch (TimeoutException e) {
		    // in case of assert element doesn't exist, or if an element really doesn't
		    // exist this exception will be thrown from the fluent wait command

		    // this is expected and in this case the loop should just continue to iterate

		    // I've included the finElements line inside this try clause because it makes no
		    // added value to try again to find the element within the same attempt
		}
		i++;
	    } while ((matchingElementsCount == 0) && (i < numberOfAttempts));
	    return matchingElementsCount;
	} else {
	    return 0;
	}
    }

    private static String determineSuccessfulTextLocationStrategy(WebDriver driver, By elementLocator) {
	String elementText = driver.findElement(elementLocator).getText();
	String successfulTextLocationStrategy = "text";
	if (elementText.trim().equals("")) {
	    elementText = driver.findElement(elementLocator).getAttribute("textContent");
	    successfulTextLocationStrategy = "textContent";
	}
	if (elementText.trim().equals("")) {
	    successfulTextLocationStrategy = "value";
	}
	return successfulTextLocationStrategy;
    }

    private static String readTextBasedOnSuccessfulLocationStrategy(WebDriver driver, By elementLocator,
	    String successfulTextLocationStrategy) {
	String actualText = "";
	switch (successfulTextLocationStrategy) {
	case "text":
	    actualText = driver.findElement(elementLocator).getText();
	    break;
	case "textContent":
	    actualText = driver.findElement(elementLocator).getAttribute("textContent");
	    break;
	case "value":
	    actualText = driver.findElement(elementLocator).getAttribute("value");
	    break;
	default:
	    break;
	}
	return actualText;
    }

    private static void typeWrapper(WebDriver driver, By elementLocator, String targetText, Boolean isSecureTyping) {
	if (identifyUniqueElement(driver, elementLocator)) {
	    // attempt to type
	    String successfulTextLocationStrategy = determineSuccessfulTextLocationStrategy(driver, elementLocator);
	    String elementText = readTextBasedOnSuccessfulLocationStrategy(driver, elementLocator,
		    successfulTextLocationStrategy);

	    if (!elementText.trim().equals("")) {
		// attempt to clear element then check text size
		clearBeforeTyping(driver, elementLocator, successfulTextLocationStrategy);
	    }
	    if ((getMatchingElementsCount(driver, elementLocator, attemptsBeforeThrowingElementNotFoundException) == 1)
		    && (!targetText.equals(""))) {
		performType(driver, elementLocator, targetText);
	    }
	    if ((getMatchingElementsCount(driver, elementLocator, attemptsBeforeThrowingElementNotFoundException) == 1)
		    && (!targetText.equals(""))) {
		// to confirm that the text was written successfully
		if (targetText.equals(readTextBasedOnSuccessfulLocationStrategy(driver, elementLocator,
			successfulTextLocationStrategy))) {
		    if (isSecureTyping) {
			passAction(driver, elementLocator, "type", targetText.replaceAll(".", "*"));
		    } else {
			passAction(driver, elementLocator, "type", targetText);
		    }
		} else {
		    // attempt once to type using javascript then confirm typing was successful
		    // again
		    clearBeforeTyping(driver, elementLocator, successfulTextLocationStrategy);
		    performTypeUsingJavaScript(driver, elementLocator, targetText);
		    if (targetText.equals(readTextBasedOnSuccessfulLocationStrategy(driver, elementLocator,
			    successfulTextLocationStrategy))) {
			if (isSecureTyping) {
			    passAction(driver, elementLocator, "type", targetText.replaceAll(".", "*"));
			} else {
			    passAction(driver, elementLocator, "type", targetText);
			}
		    } else {
			try {
			    //Boolean discreetLoggingState = ReportManager.isDiscreteLogging();
			    //ReportManager.setDiscreteLogging(true);
			    String actualText = getText(driver, elementLocator);
			    //ReportManager.setDiscreteLogging(discreetLoggingState);
			    failAction(driver, "type", "Expected to type: \"" + targetText + "\", but ended up with: \""
				    + actualText + "\"");
			} catch (Exception e) {
			    failAction(driver, "type",
				    "Expected to type: \"" + targetText + "\", but ended up with something else");
			}
		    }
		}
	    }
	}
    }

    private static void clearBeforeTyping(WebDriver driver, By elementLocator, String successfulTextLocationStrategy) {
	// attempt clear using clear
	driver.findElement(elementLocator).clear();
	String elementText = readTextBasedOnSuccessfulLocationStrategy(driver, elementLocator,
		successfulTextLocationStrategy);

	// attempt clear using sendKeys
	if (!elementText.trim().equals("")) {
	    driver.findElement(elementLocator).sendKeys("");
	}

	elementText = readTextBasedOnSuccessfulLocationStrategy(driver, elementLocator, successfulTextLocationStrategy);
	// attempt clear using javascript
	performTypeUsingJavaScript(driver, elementLocator, "");

	elementText = readTextBasedOnSuccessfulLocationStrategy(driver, elementLocator, successfulTextLocationStrategy);
	// attempt clear using letter by letter backspace
	if (!elementText.trim().equals("")) {
	    driver.findElement(elementLocator).sendKeys("");
	    for (int i = 0; i < elementText.length(); i++) {
		driver.findElement(elementLocator).sendKeys(Keys.BACK_SPACE);
	    }
	}
    }

    private static void performType(WebDriver driver, By elementLocator, String text) {
	// implementing loop to try and break out of the stale element exception issue
	for (int i = 0; i < attemptsBeforeThrowingElementNotFoundException; i++) {
	    try {
		// attempt to perform action
		driver.findElement(elementLocator).sendKeys(text);
		break;
	    } catch (StaleElementReferenceException | ElementNotInteractableException | UnreachableBrowserException
		    | NoSuchElementException | TimeoutException e) {
		if (i + 1 == attemptsBeforeThrowingElementNotFoundException) {
		    		Reporter.Log(e.toString());
		}
	    } catch (Exception e) {
		if (e.getMessage().contains("cannot focus element")
			&& (i + 1 == attemptsBeforeThrowingElementNotFoundException)) {
		    		Reporter.Log(e.toString());
		} else {
		    		Reporter.Log(e.toString());
		    Reporter.Log("Unhandled Exception: " + e.getMessage());
		}
	    }
	}
    }

    /**
     * Used in case the regular type text output didn't match with the expected type
     * text output
     * 
     * @param driver
     * @param elementLocator
     * @param text
     */
    private static void performTypeUsingJavaScript(WebDriver driver, By elementLocator, String text) {
	try {
	    ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + text + "';",
		    driver.findElement(elementLocator));
	} catch (Exception e) {
	    		Reporter.Log(e.toString());
	}
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// [Public] Core Element Actions
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Attempts to Click on a certain web element using selenium webdriver, or using
     * javascript
     * 
     * @param driver         the current instance of Selenium webdriver
     * @param elementLocator the locator of the webElement under test (By xpath, id,
     *                       selector, name ...etc)
     */
    public static void click(WebDriver driver, By elementLocator) {
	// Waits for the element to be clickable, and then clicks it.
	if (identifyUniqueElement(driver, elementLocator)) {
	    // adding hover before clicking an element to enable styles to show in the
	    try {
		performHover(driver, elementLocator);
	    } catch (Exception e) {
		if (!(e.getMessage().contains("Unable to locate element")
			|| e.getMessage().contains("no such element"))) {
		    		Reporter.Log(e.toString());
		}
		// else ignore this issue
	    }

	    takeScreenshot(driver, elementLocator, "click", null, true);
	    try {
		// wait for element to be clickable
		(new WebDriverWait(driver, defaultElementIdentificationTimeout))
			.until(ExpectedConditions.elementToBeClickable(elementLocator));
	    } catch (TimeoutException e) {
				Reporter.Log(e.toString());
	    }

	    try {
		driver.findElement(elementLocator).click();
	    } catch (Exception e) {
		try {
		    ((JavascriptExecutor) driver).executeScript("arguments[arguments.length - 1].click();",
			    driver.findElement(elementLocator));
		} catch (Exception e2) {
		    		Reporter.Log(e.toString());
		    Reporter.Log(e2.toString());
		    failAction(driver, "click");
		}
	    }
	    passAction(driver, elementLocator, "click");
	} else {
	    failAction(driver, "click");
	}
    }

    /**
     * Checks if there is any text in an element, clears it, then types the required
     * string into the target element.
     * 
     * @param driver         the current instance of Selenium webdriver
     * @param elementLocator the locator of the webElement under test (By xpath, id,
     *                       selector, name ...etc)
     * @param text           the target text that needs to be typed into the target
     *                       webElement
     */
    public static void type(WebDriver driver, By elementLocator, String text) {
	typeWrapper(driver, elementLocator, text, false);
    }

    private static void performHover(WebDriver driver, By elementLocator) {
	String javaScript = "var evObj = document.createEvent('MouseEvent');"
		+ "evObj.initMouseEvent(\"mousemove\", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
		+ "arguments[arguments.length -1].dispatchEvent(evObj);";
	((JavascriptExecutor) driver).executeScript(javaScript, driver.findElement(elementLocator));

	javaScript = "var evObj = document.createEvent('MouseEvents');"
		+ "evObj.initMouseEvent(\"mouseenter\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
		+ "arguments[arguments.length -1].dispatchEvent(evObj);";
	((JavascriptExecutor) driver).executeScript(javaScript, driver.findElement(elementLocator));

	javaScript = "var evObj = document.createEvent('MouseEvents');"
		+ "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
		+ "arguments[arguments.length -1].dispatchEvent(evObj);";
	((JavascriptExecutor) driver).executeScript(javaScript, driver.findElement(elementLocator));

	(new Actions(driver)).moveToElement(driver.findElement(elementLocator)).perform();
    }



    /**
     * Retrieves text from the target element and returns it as a string value.
     * 
     * @param driver         the current instance of Selenium webdriver
     * @param elementLocator the locator of the webElement under test (By xpath, id,
     *                       selector, name ...etc)
     * @return the text value of the target webElement
     */
    public static String getText(WebDriver driver, By elementLocator) {
	if (identifyUniqueElement(driver, elementLocator)) {
	    String elementText = driver.findElement(elementLocator).getText();
	    if (elementText.trim().equals("")) {
		elementText = driver.findElement(elementLocator).getAttribute("textContent");
	    }
	    if (elementText.trim().equals("")) {
		elementText = driver.findElement(elementLocator).getAttribute("value");
	    }
	    passAction(driver, elementLocator, "getText", elementText);
	    return elementText;
	} else {
	    failAction(driver, "getText");
	    return null;
	}
    }
}
