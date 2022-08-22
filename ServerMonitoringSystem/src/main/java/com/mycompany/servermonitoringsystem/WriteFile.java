/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servermonitoringsystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 *
 * @author namtd
 */
public class WriteFile {
    public void writeFile(String value, String pathDirectory) {
        String PATH = pathDirectory + "\\";
        String directoryName = PATH;
        String fileName = "logs.txt";

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try {
            FileOutputStream fos = new FileOutputStream(directoryName + "\\" + fileName + "\\", true);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            bufferedWriter.append(value + "\r\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
