/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.barri3.a3wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.FileSystemNotFoundException;

/**
 *
 * @author GB-BARRI3
 */
public class Task {
    public String lastInput = null;
    public String lastOutput = null;
    
    public static Task load(File file) throws Exception {
        Task task = new Task();
        
        if (!file.exists()) {
            throw new FileSystemNotFoundException(file.toString());
        }
        
        FileReader reader = new FileReader(file);
        BufferedReader in = new BufferedReader(reader);
        
        Exception throwMe = null;
        
        try {
            reader = new FileReader(file);
            in = new BufferedReader(reader);
            
            while (in.ready()) {
                String line = in.readLine();
                
                if (!line.startsWith("#")) {
                    if (!line.contains(": ")) {
                        throw new Exception("Malformed configuration line: " + line);
                    }
                    
                    int separator = line.indexOf(":");
                    String key = line.substring(0, separator);
                    String value = line.substring(separator + 2, line.length());
                    
                    switch (key) {
                        case "lastInput": task.lastInput = getString(value); break;
                        case "lastOutput": task.lastOutput = getString(value); break;
                        
                        default: throw new Exception("Unrecognised key/value: " + line);
                    }
                }
            }
        } catch (Exception err) {
            throwMe = err;
        }
        
        try {
            in.close();
        } catch (Exception err) { /* Ignored exception */ }
        
        try {
            reader.close();
        } catch (Exception err) { /* Ignored exception */ }
        
        if (throwMe != null) {
            throw throwMe;
        }
        
        return task;
    }
    
    private static String getString(String value) {
        if (value.isBlank() || value.isEmpty() || value.contentEquals("null")) {
            return null;
        }
        
        return value;
    }
    
    public static void save(File file, Task task) throws Exception {
        PrintStream out = new PrintStream(file);
        
        out.println("lastInput: " + task.lastInput);
        out.println("lastOutput: " + task.lastOutput);
        
        out.flush();
        out.close();
    }
}
