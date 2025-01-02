package com.trends;

import org.openqa.selenium.WebDriver;

import com.trends.utils.ProxyConfiguration;
import com.trends.web.WebServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Starting application...");
        WebServer.start();
    }
}
