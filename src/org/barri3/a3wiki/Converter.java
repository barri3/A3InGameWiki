/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.barri3.a3wiki;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import uk.co.gamebrewers.common.commentator.Commentator;

/**
 *
 * @author GB-BARRI3
 */
public class Converter {
    private static final String SQF_ENSURE_CLIENT = "if(isDedicated) exitWith{};\n";
    
    private String outputBuffer = SQF_ENSURE_CLIENT;
    private int subjectUidCounter = 0;
    private HashMap<String, String> subjects = new HashMap<>();
    private String currentSubject = null;
    private String currentEntry = null;
    private boolean subjectChanged = false;
    
    public void run(Task task) {
        if (task.lastInput == null) {
            Commentator.err("Input has not been set in the task"); return;
        }
        
        if (task.lastOutput == null) {
            Commentator.err("Output has not been set in the task"); return;
        }

        try {
            FileReader fReader = new FileReader(task.lastInput);
            BufferedReader in = new BufferedReader(fReader);
            
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("# ")) {
                    handleSubject(line.substring(4));
                } else if (line.startsWith("### ")) {
                    handleEntry(line.substring(2));
                } else {
                    handleLine(line);
                }
            }
            
            if (currentEntry != null) {
                sqfEndEntry();
            }
            
            in.close();
            fReader.close();
        } catch (Exception err) {
            Commentator.err(err); return;
        }
        
        try {
            PrintStream out = new PrintStream(task.lastOutput);
            System.out.println(outputBuffer);
            out.print(outputBuffer);
            out.flush();
            out.close();
            
            Commentator.msg("Complete");
        } catch (Exception err) {
            Commentator.err(err);
        }
    }
    
    private void handleSubject(String subjectName) {
        String subjectUid = null;
        
        if (currentEntry != null) {
            sqfEndEntry();
        }
        
        if (!subjects.containsKey(subjectName)) {
            subjectUid = "subject" + subjectUidCounter;
            subjectUidCounter++;
            subjects.put(subjectName, subjectUid);
        } else {
            subjectUid = subjects.get(subjectName);
        }
        
        currentSubject = subjectUid;
        subjectChanged = true;
        
        sqfCreateSubject(subjectUid, subjectName);
    }
    
    private void handleEntry(String entryName) {
        if (!subjectChanged && currentEntry != null) {
            sqfEndEntry();
        } else if (subjectChanged) {
            subjectChanged = false;
        }
        
        currentEntry = entryName;
        sqfStartEntry(currentSubject, entryName);
    }
    
    private void handleLine(String line) {
        outputBuffer += line + "<br/>\n";
    }
    
    private void sqfCreateSubject(String subjectUid, String subjectName) {
        outputBuffer += "player createDiarySubject [\"" + subjectUid + "\",\"" + subjectName + "\"];\n";
    }
    
    private void sqfStartEntry(String subjectUid, String entryName) {
        outputBuffer += "player createDiaryRecord [\"" + subjectUid + "\", [ \"" + entryName + "\" , \"\n";
    }
    
    private void sqfEndEntry() {
        outputBuffer += "\" ]];\n";
    }
}
