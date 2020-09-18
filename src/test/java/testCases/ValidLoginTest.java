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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

public class ValidLoginTest extends TestBase{
	
	LoginPage LoginPageObject;
	MyDouglasPage MyDouglasPageObject;

	String douglasURL= utilities.LoadProperties.userData.getProperty("douglasURL");
	
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
	public void BeforeMethod() {
		openBrowser(douglasURL);
		LoginPageObject = new LoginPage(driver);
		MyDouglasPageObject = new MyDouglasPage(driver);

	}


	@Test(dataProvider = "data")
	@Severity(SeverityLevel.NORMAL)
	@Description("Login with valid user")
	public void successfulLogin(String fName,String lName,String pass)  
	{
		LoginPageObject.login(RegistrationTest.email, RegistrationTest.password);
		LoginPageObject.closePopupWelcomeWindow();

		String assertionWord = Perform.getText(driver, MyDouglasPageObject.headlineWord);
		Helper.assertEquals("Hallo"+ " "+ fName+ " " + lName,assertionWord , 1, true);
	}
}
