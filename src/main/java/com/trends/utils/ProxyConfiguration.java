package com.trends.utils;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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
        return currentProxyIP;
    }
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
 currentProxyIP=connectToProxy();
        // Optional: Add arguments for debugging or specific configurations
        options.addArguments("--proxy-server=" + PROXY_HOST + ":" + PROXY_PORT);

        return new ChromeDriver(options);
    }
     public static String connectToProxy() {
        try {
            // Establish socket connection to proxy
            Socket socket = new Socket(PROXY_HOST, PROXY_PORT);
    
            // Build the CONNECT request with Proxy-Authorization header
            String connectRequest = 
                "CONNECT api.ipify.org:443 HTTP/1.1\r\n" +
                "Host: api.ipify.org:443\r\n" +
                "Proxy-Authorization: Basic " + 
                Base64.getEncoder()
                      .encodeToString((PROXY_USERNAME + ":" + PROXY_PASSWORD).getBytes()) + "\r\n" +
                "\r\n";
    
            // Send CONNECT request
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(connectRequest.getBytes());
            outputStream.flush();
    
            // Read proxy response
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseLine;
            StringBuilder response = new StringBuilder();
    
            while ((responseLine = reader.readLine()) != null && !responseLine.isEmpty()) {
                response.append(responseLine).append("\n");
            }
    
            System.out.println("Proxy Response:\n" + response);
    
            // Check if CONNECT was successful (HTTP 200)
            if (!response.toString().contains("200 Connection established")) {
                System.err.println("Failed to establish connection via proxy.");
                socket.close();

                return "unknown";

            }
    
            // Extract the X-ProxyMesh-IP from the response
            String proxyIP = "unknown";
            for (String line : response.toString().split("\n")) {
                if (line.startsWith("X-ProxyMesh-IP:")) {
                    proxyIP = line.split(":")[1].trim();
                    break;
                }
            }
    
            // Proceed to TLS handshake for secure communication (if necessary)
            System.out.println("CONNECT successful. Proceeding to TLS handshake...");
            socket.close();
    
            return proxyIP;
    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    
    }
}
