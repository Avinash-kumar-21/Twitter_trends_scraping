package com.trends.web;

import com.google.gson.Gson;
import com.trends.db.MongoDBHandler;
import com.trends.scraper.TwitterScraper;
import com.trends.utils.ProxyConfiguration;
import org.openqa.selenium.WebDriver;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.*;

import java.util.HashMap;
import java.util.List;

public class WebServer {
    public static void start() {
        port(4567);

        get("/", (req, res) -> {
            res.type("text/html");
            return """
                                       <!DOCTYPE html>
                                       <html lang="en">
                                       <head>
                                           <meta charset="UTF-8">
                                           <title>Twitter Trends</title>
                                       </head>
                                       <body>
                                           <button onclick="fetchTrends()">Click here to run the script.</button>
                                           <div>When the link is click, it shows the following text:</div>
                                           <div id="results"></div>
                                           <script>
                                               async function fetchTrends() {
                                                   const response = await fetch('/run-scraper');
                                                   const data = await response.json();
                                                   const resultsDiv = document.getElementById('results');
                                                   resultsDiv.innerHTML = `
                                                       <h3>These are the most happening topics as on ${data.timestamp}</h3>
                                                        <p>1. ${data.trend1}</p>
                                                       <p>2. ${data.trend2}</p>
                                                       <p>3. ${data.trend3}</p>
                                                       <p>4. ${data.trend4}</p>
                                                       <p>5. ${data.trend5}</p>
                                                       <p>The IP address used for this query was: ${data.ipAddress}</p>
                                                       <p>Here’s a JSON extract of this record from the MongoDB:${data.mongoDbreturn}
                    </p>
                     <button onclick="fetchTrends()"> Click here to run the query again.</button>
                   

                                                   `;
                                               }
                                           </script>
                                       </body>
                                       </html>
                                       """;
        });

        get("/run-scraper", (req, res) -> {
            WebDriver driver = null;
            try {
                System.out.println("Fetching trends...");
                driver = ProxyConfiguration.createWebDriverWithProxy();
                List<String> trends = TwitterScraper.scrapeTrendingTopics(driver);

                // Fetch Proxy IP address
                String ipAddress = ProxyConfiguration.getProxyIPAddress();
                String mongoDbreturn = MongoDBHandler.saveTrendingTopics(trends, ipAddress);


                HashMap<String, String> result = new HashMap<>();
                for (int i = 0; i < trends.size(); i++) {
                    result.put("trend" + (i + 1), trends.get(i));
                }
                result.put("ipAddress", ipAddress);
                result.put("timestamp", String.valueOf(System.currentTimeMillis()));
                result.put("mongoDbreturn", mongoDbreturn);
                res.type("application/json");
                return new Gson().toJson(result);

            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(new HashMap<String, String>() {
                    {
                        put("error", "Failed to fetch trends: " + e.getMessage());
                    }
                });
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        });

    }
}
