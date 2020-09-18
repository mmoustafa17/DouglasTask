/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.PageBase;
import utilities.Perform;

public class LoginPage extends PageBase{

	public LoginPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, 20);
		actions = new Actions(driver);
	}

	/** Web-elements identified locators */
	
	By cookieInfo			= By.xpath("//button[contains(@aria-label,'Einwilligen')]");
	By loginEmail			= By.xpath("//*[@data-ui-name='loginForm.email']");
	By loginPassword		= By.xpath("//*[@data-ui-name='loginForm.password']");
	By loginBtn				= By.name("LoginForm|SubmitChanges");
	public By femalGender 	= By.xpath("(//div[@class='rd__col rd__col--sm-6 rd__col--lg-4'])[1]");
	public By maleGender 	= By.xpath("(//div[@class='rd__col rd__col--sm-6 rd__col--lg-4'])[2]");
	By firstName 			= By.name("my-douglas-register-prename");
	By surName 				= By.name("my-douglas-register-lastname");
	By password 			= By.name("my-douglas-register-password");
	By repeatPassword 		= By.name("my-douglas-register-password-repeat");
	By emailAddress			= By.name("my-douglas-register-email");
	By newRegistrationBtn	= By.name("RegisterForm|SubmitChanges");
	By welcomePopupWindow	= By.cssSelector("div.rd__modal-content__header__close");
	public By invalidLoginMsg = By.xpath("//*[@data-ui-name='message']");
	By forgetPasswordLink   = By.xpath("//*[@data-ui-name='loginForm.lostPasswordLink']");
	
	private void handleCookie() {
		Perform.click(driver, cookieInfo);
	}
	
	public void registerNewUserData(By userGender,String fName,String lName, String pass, String emailAdd) {
		handleCookie();
		Perform.click(driver, userGender);
		Perform.type(driver, firstName, fName);
		Perform.type(driver, surName, lName);
		Perform.type(driver, password, pass);
		Perform.type(driver, repeatPassword, pass);
		Perform.type(driver, emailAddress, emailAdd);
		Perform.click(driver, newRegistrationBtn);
	}
	
	public void closePopupWelcomeWindow() {
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.rd__modal-content__header__close")));
		Perform.click(driver, welcomePopupWindow);
	}
	
	public void login(String loginMail, String loginPass) {
		handleCookie();
		Perform.type(driver, loginEmail, loginMail);
		Perform.type(driver, loginPassword, loginPass);
		Perform.click(driver, loginBtn);
	}
	
	public void clickForgetPassword() {
		handleCookie();
		Perform.click(driver, forgetPasswordLink);
	}
}
