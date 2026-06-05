package org.stringtecnologia.string_api.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PdfGeneratorPlaywrightService {

    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init() {

        log.info("Inicializando Playwright...");

        playwright = Playwright.create();

        browser =
                playwright.chromium().launch(
                        new BrowserType.LaunchOptions()
                                .setHeadless(true)
                );

        log.info("Playwright inicializado.");
    }

    public byte[] generate(String html) {

        Page page = null;

        try {

            page = browser.newPage();

            page.setContent(html);

            return page.pdf(
                    new Page.PdfOptions()
                            .setFormat("A4")
                            .setPrintBackground(true)
            );

        } catch (Exception e) {

            log.error(
                    "Erro ao gerar PDF com Playwright",
                    e
            );

            throw new RuntimeException(
                    "Erro ao gerar PDF",
                    e
            );

        } finally {

            if (page != null) {
                page.close();
            }
        }
    }

    @PreDestroy
    public void destroy() {

        log.info("Finalizando Playwright...");

        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}