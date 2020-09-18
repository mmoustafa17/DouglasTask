/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package utilities;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class JSWaiter {

    private static boolean debug = false;

    private static WebDriver jsWaitDriver;
    private static WebDriverWait jsWait;
    private static JavascriptExecutor jsExec;

    private JSWaiter() {
	throw new IllegalStateException("Utility class");
    }

    // Get the driver
    public static void setDriver(WebDriver driver) {
	jsWaitDriver = driver;
	jsWait = new WebDriverWait(jsWaitDriver, 10);
	jsExec = (JavascriptExecutor) jsWaitDriver;
    }

    /**
     * Waits for jQuery, Angular, and/or Javascript if present on the current page.
     * 
     * @return true in case waiting didn't face any isssues, and false in case of a
     *         severe exception
     */
    public static boolean waitForLazyLoading() {
	try {
	    Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
	    if (jQueryDefined) {
		waitForJQueryLoad();
	    } else {
		if (debug) {
		    Reporter.Log("jQuery is not defined on this site!");
		}
	    }

	    try {
		// check if angular is defined
		waitForAngularIfDefined();
	    } catch (WebDriverException e) {
		if (debug) {
		    	    Reporter.Log(e.toString());
		    Reporter.Log("Angular is not defined on this site!");
		}
	    }

	    Boolean jsReady = (Boolean) jsExec.executeScript("return document.readyState").toString().trim()
		    .equalsIgnoreCase("complete");
	    if (!jsReady) {
		waitForJSLoad();
	    } else {
		if (debug) {
		    Reporter.Log("JS is Ready!");
		}
	    }
	    return true;
	} catch (WebDriverException e) {
	    	    Reporter.Log(e.toString());
	    return true;
	} 
	catch (Exception e) {
		String message = e.getMessage();
	    if (message != null && message.contains("jQuery is not defined")) {
		// do nothing
		return true;
	    } else if (message != null && message.contains("Error communicating with the remote browser. It may have died.")) {
			    Reporter.Log(e.toString());
		return false;
	    } else {
			    //Reporter.Log(e.toString());
		//Reporter.Log("Unhandled Exception: " + e.getMessage());
		return false;
	    }
	}
    }

    // Wait for JQuery Load
    private static void waitForJQueryLoad() {
	// Wait for jQuery to load
	ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) jsWaitDriver)
		.executeScript("return jQuery.active") == 0);

	// Get JQuery is Ready
	boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");

	if (!jqueryReady) {
	    // Wait JQuery until it is Ready!
	    if (debug) {
		Reporter.Log("Waiting for JQuery to be Ready!");
	    }
	    int tryCounter = 0;
	    while ((!jqueryReady) && (tryCounter < 5)) {
		if (debug) {
		    Reporter.Log("JQuery is NOT Ready!");
		}
		// Wait for jQuery to load
		jsWait.until(jQueryLoad);
		sleep(20);
		tryCounter++;
		jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active == 0");
	    }
	    if (debug) {
		Reporter.Log("JQuery is Ready!");
	    }
	} else {
	    if (debug) {
		Reporter.Log("JQuery is Ready!");
	    }
	}
    }

    // Wait for Angular Load
    private static void waitForAngularLoad() {
	WebDriverWait wait = new WebDriverWait(jsWaitDriver, 15);
	JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

	String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";

	// Wait for ANGULAR to load
	ExpectedCondition<Boolean> angularLoad = driver -> Boolean
		.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());

	// Get Angular is Ready
	boolean angularReady = Boolean.parseBoolean(jsExec.executeScript(angularReadyScript).toString());

	if (!angularReady) {
	    // Wait ANGULAR until it is Ready!
	    if (debug) {
		Reporter.Log("Waiting for ANGULAR to be Ready!");
	    }
	    int tryCounter = 0;
	    while ((!angularReady) && (tryCounter < 5)) {
		if (debug) {
		    Reporter.Log("ANGULAR is NOT Ready!");
		}
		// Wait for Angular to load
		wait.until(angularLoad);
		// More Wait for stability (Optional)
		sleep(20);
		tryCounter++;
		angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());
	    }
	    if (debug) {
		Reporter.Log("ANGULAR is Ready!");
	    }
	} else {
	    if (debug) {
		Reporter.Log("ANGULAR is Ready!");
	    }
	}
    }

    // Wait Until JS Ready
    private static void waitForJSLoad() {
	WebDriverWait wait = new WebDriverWait(jsWaitDriver, 15);
	JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

	// Wait for Javascript to load
	ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) jsWaitDriver)
		.executeScript("return document.readyState").toString().trim().equalsIgnoreCase("complete");

	// Get JS is Ready
	boolean jsReady = (Boolean) jsExec.executeScript("return document.readyState").toString().trim()
		.equalsIgnoreCase("complete");

	// Wait Javascript until it is Ready!
	if (!jsReady) {
	    // Wait JS until it is Ready!
	    if (debug) {
		Reporter.Log("Waiting for JS to be Ready!");
	    }
	    int tryCounter = 0;
	    while ((!jsReady) && (tryCounter < 5)) {
		if (debug) {
		    Reporter.Log("JS in NOT Ready!");
		}
		// Wait for Javascript to load
		wait.until(jsLoad);
		// More Wait for stability (Optional)
		sleep(20);
		tryCounter++;
		jsReady = (Boolean) jsExec.executeScript("return document.readyState").toString().trim()
			.equalsIgnoreCase("complete");
	    }
	    if (debug) {
		Reporter.Log("JS is Ready!");
	    }
	} else {
	    if (debug) {
		Reporter.Log("JS is Ready!");
	    }
	}
    }

    private static void waitForAngularIfDefined() {
	Boolean angularDefined = !((Boolean) jsExec.executeScript("return window.angular === undefined"));
	if (angularDefined) {
	    Boolean angularInjectorDefined = !((Boolean) jsExec
		    .executeScript("return angular.element(document).injector() === undefined"));

	    if (angularInjectorDefined) {
		waitForAngularLoad();
	    } else {
		if (debug) {
		    Reporter.Log("Angular injector is not defined on this site!");
		}
	    }
	} else {
	    if (debug) {
		Reporter.Log("Angular is not defined on this site!");
	    }
	}
    }

    private static void sleep(Integer milliSeconds) {
	long secondsLong = (long) milliSeconds;
	try {
	    Thread.sleep(secondsLong);
	} catch (Exception e) {
	    Reporter.Log(e.toString());
	    // InterruptedException
	}
    }
}