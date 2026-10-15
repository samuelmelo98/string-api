package org.stringtecnologia.string_api.ocr.storage;

import java.io.*;


public interface OcrStorage {
    void salvar(String key, byte[] content) throws IOException;

    byte[] ler(String key) throws IOException;
}
