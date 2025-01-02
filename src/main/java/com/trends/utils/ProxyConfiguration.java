package com.trends.utils;

import org.json.JSONObject;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.Base64;

public class ProxyConfiguration {
    // ProxyMesh Configuration
    private static final String PROXY_URL = "http://Username:Password@host:port"; // change username and password
    private static final String PROXY_HOST = "HOST"; // Host without credentials
    private static final int PROXY_PORT = "PORT";                   // Port
    private static final String PROXY_USERNAME = "Username";  // Username
    private static final String PROXY_PASSWORD = " Password"; // Password

    private static String currentProxyIP = "127.0.0.1"; // Default IP

    // HTTP Request through ProxyMesh
    public static String getProxyIPAddress() {
        try {
            URL url = new URL("https://api.ipify.org?format=json");
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
    
            String auth = PROXY_USERNAME + ":" + PROXY_PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Proxy-Authorization", "Basic " + encodedAuth);
            conn.setRequestMethod("GET");
    
            // Set timeouts
            conn.setConnectTimeout(10000); // 10 seconds
            conn.setReadTimeout(10000); // 10 seconds
    
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Failed to connect to proxy. Response Code: " + responseCode);
                return "unknown";
            }
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while (( line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
    
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("ip");
        } catch (Exception e) {
            System.err.println("Error occurred while trying to get the proxy IP address: " + e.getMessage());
            e.printStackTrace();
            return "unknown";
        }
    }
    

    // Selenium WebDriver with ProxyMesh configuration
    public static  WebDriver createWebDriverWithProxy() {
        System.setProperty("webdriver.chrome.driver", "path to chromedriver"); // Update with your path

        // Selenium Proxy object with inline authentication
        Proxy seleniumProxy = new Proxy();
        seleniumProxy.setHttpProxy(PROXY_URL)  // Use full inline URL with credentials
                     .setSslProxy(PROXY_URL);

        ChromeOptions options = new ChromeOptions();
        options.setProxy(seleniumProxy);

        // Optional: Add arguments for debugging or specific configurations
        options.addArguments("--proxy-server=" + PROXY_HOST + ":" + PROXY_PORT);

        return new ChromeDriver(options);
    }
}