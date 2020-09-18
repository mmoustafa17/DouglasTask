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

import base.PageBase;

public class MyDouglasPage extends PageBase{

	public MyDouglasPage(WebDriver driver) {
		super(driver);
		actions = new Actions(driver);
	}

	/** Web-elements identified locators */

	By myDouglasList		= By.xpath("//*[@data-wt-content='navIcons.MyDouglas']");
	public By headlineWord  = By.xpath("(//*[@class='rd__my-douglas-welcome__headline'])[1]");


}
