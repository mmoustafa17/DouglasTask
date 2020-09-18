/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package testCases;

import java.io.IOException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import base.TestBase;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import jxl.read.biff.BiffException;
import pages.LoginPage;
import pages.MyDouglasPage;
import utilities.ExcelReader;
import utilities.Helper;
import utilities.Perform;
import utilities.Reporter;

public class AllTestCases extends TestBase{
	
	LoginPage LoginPageObject;
	MyDouglasPage MyDouglasPageObject;
	
	
	String douglasURL= utilities.LoadProperties.userData.getProperty("douglasURL");
	
	static Faker fakeData = new Faker(); 
	public static String email = fakeData.internet().emailAddress(); 
	public static String password ;
	
	@DataProvider
	public Object[][] data() throws IOException, BiffException {
		Object[][] data = new Object[1][3];
		ExcelReader xl = new ExcelReader("Sheet1", "RegistrationTestCase");
		Object[][] data1 = xl.getTestdata();
		for (int i = 0; i < data1.length; i++) {
			for (int j = 0; j < data1[i].length; j++) {
				data[0][j] = data1[i][j];
			}
		}
		return data;
	}

	@BeforeMethod
	public void BeforeMethod() throws IOException, InterruptedException {
		startDriver("chrome");
		openBrowser(douglasURL);
		LoginPageObject = new LoginPage(driver);
		MyDouglasPageObject = new MyDouglasPage(driver);
	}


	@Test(dataProvider = "data", testName = "NewRegistration",priority = 1)
	@Severity(SeverityLevel.NORMAL)
	@Description("Register new user in Douglas")
	public void NewRegistration(String fName,String lName,String pass)  
	{
		LoginPageObject.registerNewUserData(LoginPageObject.maleGender, fName, lName, pass, email);
		password = pass;
		Reporter.Log("Used Email : " + email);
		 
		LoginPageObject.closePopupWelcomeWindow();
		
		
		String assertionWord = Perform.getText(driver, MyDouglasPageObject.headlineWord);
		Helper.assertEquals("Hallo"+ " "+ fName+ " " + lName,assertionWord , 1, true);
		
	}
	
	
	
	
	@Test(dataProvider = "data",testName = "Valid Login",priority = 2)
	@Severity(SeverityLevel.NORMAL)
	@Description("Login with valid user")
	public void successfulLogin(String fName,String lName,String pass)  
	{
		LoginPageObject.login(email, password);
		LoginPageObject.closePopupWelcomeWindow();

		String assertionWord = Perform.getText(driver, MyDouglasPageObject.headlineWord);
		Helper.assertEquals("Hallo"+ " "+ fName+ " " + lName,assertionWord , 1, true);
	}
	
	
	
	
	@Test(dataProvider = "data",testName = "InValid Login",priority = 3)
	@Severity(SeverityLevel.NORMAL)
	@Description("Login with invalid user")
	public void InvalidLogin(String fName,String lName,String pass)  
	{		

		LoginPageObject.login("AnyWords@hotmail.com", "AnyPassword");

		String assertionWord = Perform.getText(driver, LoginPageObject.invalidLoginMsg);
		Helper.assertEquals("Ihre Eingabedaten sind leider fehlerhaft, stimmen Benutzername und Passwort?",assertionWord , 1, true);
	}
	
	
	
	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		closeDriver();
	}
}
