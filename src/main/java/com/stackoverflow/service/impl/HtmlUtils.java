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

        String text = body.text();
        String truncatedText = text.length() > maxLines ? text.substring(0, maxLines) + "..." : text;

        return "<p>" + truncatedText + "</p>";
    }

    public static String extractFormattedContent(String html) {
        Document doc = Jsoup.parse(html);
        StringBuilder formattedContent = new StringBuilder();

        for (Element element : doc.select("p, div, pre,li")) {
            if (element.tagName().equals("pre")) {
                // Add inline style for <pre> tag
                formattedContent.append("<pre style='background-color: #f1f1f1; padding: 10px; border-radius: 5px;'>" + element.text() + "</pre><br></br>");
            } else {
                formattedContent.append(element.outerHtml()).append("<br></br>");
            }
            //formattedContent.append(element.outerHtml()).append("<br></br>");
        }

        return formattedContent.toString();
    }
}