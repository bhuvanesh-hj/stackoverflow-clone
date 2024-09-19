package com.stackoverflow.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class HtmlUtils {
    public static String truncateHtml(String html, int maxLines) {
        Document doc = Jsoup.parse(html);
        Element body = doc.body();

        String text = body.text(); // Get the plain text
        String truncatedText = text.length() > maxLines ? text.substring(0, maxLines) + "..." : text;

        // Return truncated text wrapped in HTML tags
        return "<p>" + truncatedText + "</p>";
    }

    public static String extractFormattedContent(String html) {
        Document doc = Jsoup.parse(html);
        StringBuilder formattedContent = new StringBuilder();

        // Extracting different sections
        for (Element element : doc.select("p, strong, div, code,li")) {
//            formattedContent.append(element.outerHtml()).append("<br/><br/>"); // Adding gap between tags
            formattedContent
                    .append(element.outerHtml()).append("<br/></b>");
        }

        return formattedContent.toString();
    }
}