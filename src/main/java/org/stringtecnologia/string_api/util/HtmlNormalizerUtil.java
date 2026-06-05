package org.stringtecnologia.string_api.util;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlNormalizerUtil {

    public static String normalize(String html) {
        Document document = Jsoup.parse(html);

        // ✅ Configuração para blindar contra erros de tag aberta
        document.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml) // Força o fechamento de tags meta, br, img
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
                .prettyPrint(false);

        return document.html();
    }
}
