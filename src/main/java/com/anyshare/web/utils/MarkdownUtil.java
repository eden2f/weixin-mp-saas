package com.anyshare.web.utils;

import io.github.furstenheim.CopyDown;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * @author huangminpeng
 * @date 2021/4/23 10:45
 */
public class MarkdownUtil {

    public static String html2Markdown(String html) {
        CopyDown converter = new CopyDown();
        return converter.convert(html);
    }

    public static String html2PlainText(String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }
        Document document = Jsoup.parse(html);
        Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        document.outputSettings(outputSettings);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        document.select("p").append("\\n");
        String newHtml = document.html().replaceAll("\\\\n", "\n");
        String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);
        return StringEscapeUtils.unescapeHtml4(plainText.trim());
    }


}
