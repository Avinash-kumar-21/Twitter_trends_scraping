package com.trends.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TwitterScraper {
    public static List<String> scrapeTrendingTopics(WebDriver driver) throws InterruptedException {
        // Navigate to Twitter login page
        driver.get("https://x.com/i/flow/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Enter username
        WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("text")));
        usernameField.sendKeys("username"); // Replace with your username
        driver.findElement(By.xpath("//span[text()='Next']")).click();

        // Wait for the password field to appear
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));
        passwordField.sendKeys("password"); // Replace with your password
        driver.findElement(By.xpath("//span[text()='Log in']")).click();

        // Wait for login to complete (adjust as necessary)
        Thread.sleep(5000);

        // Navigate to the "Trending" tab
        driver.get("https://x.com/explore/tabs/for_you");

        // Wait for the trending section to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-label='Timeline: Explore']")));

        // Locate the trending topics elements
        List<WebElement> trendElements = driver.findElements(By.xpath("//div[@aria-label='Timeline: Explore']//span[contains(text(), '#')]"));

        List<String> trendingTopics = new ArrayList<>();
        for (int i = 0; i < Math.min(trendElements.size(), 5); i++) {
            trendingTopics.add(trendElements.get(i).getText());
        }

        // Print the trending topics (optional)
        System.out.println("Trending topics: " + trendingTopics);

        return trendingTopics;
    }
}