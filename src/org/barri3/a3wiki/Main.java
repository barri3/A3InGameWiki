/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.barri3.a3wiki;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import uk.co.gamebrewers.common.commander.CCommander;
import uk.co.gamebrewers.common.commander.CCommanderInputTerminal;
import uk.co.gamebrewers.common.commander.CCommanderTargetClass;
import uk.co.gamebrewers.common.commentator.CCommentatorTerminalEndpoint;
import uk.co.gamebrewers.common.commentator.Commentator;
import uk.co.gamebrewers.common.commentator.CommentatorSystemEndpoint;
import uk.co.gamebrewers.common.terminal.CTerminal;
import uk.co.gamebrewers.common.threading.ThreadingUtil;
import uk.co.gamebrewers.common.threading.pooling.ThreadPool;

/**
 *
 * @author BARRI3
 */
public class Main {
    private CTerminal terminal = null;
    private CCommander commander = null;
    
    private boolean isProcessing = false;
    
    private Task task = new Task();
    
    public Main() {
        Commentator.addListener(new CommentatorSystemEndpoint());
        
        terminal = new CTerminal();
        terminal.setIcon(loadIcon());
        terminal.setTitle("A3InGameWiki - barri3.org");
        terminal.getCloseRequestedEvent().add((e) -> {
            while (isProcessing) {
                ThreadingUtil.join(25);
            }
            
            System.exit(0);
        });
        
        Commentator.addListener(new CCommentatorTerminalEndpoint(terminal));
        
        commander = new CCommander();
        commander.addInput("TERMINAL", new CCommanderInputTerminal(terminal));
        commander.addTarget("MAIN", new CCommanderTargetClass(this));
        
        try {
            commander.start(new ThreadPool());
        } catch (Exception err) {
            Commentator.err(err);
            
            terminal.alertError(err);
            
            System.exit(-1);
        }
    }
    
    private BufferedImage loadIcon() {
        try {
            return ImageIO.read(new File("icon.png"));
        } catch (Exception err) {
            Commentator.err(err);
            
            terminal.alertError(err);
            
            return null;
        }
    }
    
    public void cmdSetInput(String input) {
        task.lastInput = input;
    }
    
    public void cmdGetInput() {
        Commentator.msg("Input: " + task.lastInput);
    }
    
    public void cmdSetOutput(String output) {
        task.lastOutput = output;
    }
    
    public void cmdGetOutput() {
        Commentator.msg("Output: " + task.lastOutput);
    }
    
    public void cmdGetInputAndOutput() {
        cmdGetInput();
        cmdGetOutput();
    }
    
    public void cmdRun() {
        new Converter().run(task);
        
        cmdSaveTask("last");
    }
    
    public void cmdRunTask(String task) {
        cmdLoadTask(task);
        cmdRun();
    }
    
    public void cmdRunLast() {
        cmdRunTask("last");
    }
    
    public void cmdListTasks() {
        String dir = System.getProperty("user.dir");
        Commentator.msg("Checking " + dir + "...");
        File file = new File(dir);
        int count = 0;
        
        for (File child : file.listFiles()) {
            if (child.isFile() && child.getName().endsWith(".tsk") && !child.getName().contentEquals("last.tsk")) {
                Commentator.msg(child.getName().substring(0, child.getName().length() - 4));
                count++;
            }
        }
        
        if (count == 0) {
            Commentator.msg("No tasks");
        }
    }

    public void cmdRemoveTask(String task) {
        File file = new File(task + ".tsk");
        
        if (!file.exists()) {
            Commentator.msg(task + " does not exist");
            return;
        }
        
        if (file.delete()) {
            Commentator.msg(task + " was removed");
        } else {
            Commentator.msg(task + " could not be removed");
        }
    }
    
    public void cmdHasTask(String task) {
        if (new File(task + ".tsk").exists()) {
            Commentator.msg(task + " exists");
        } else {
            Commentator.msg(task + " does not exist");
        }
    }
    
    public void cmdSaveTask(String task) {
        if (this.task.lastInput == null) {
            Commentator.err("Input has not been set in the task"); return;
        }
        
        if (this.task.lastOutput == null) {
            Commentator.err("Output has not been set in the task"); return;
        }
        
        try {
            Task.save(new File(task + ".tsk"), this.task);
            Commentator.msg("Task " + task + " saved");
        } catch (Exception err) {
            Commentator.err(err);
        }
    }
    
    public void cmdLoadTask(String task) {
        try {
            this.task = Task.load(new File(task + ".tsk"));
            Commentator.msg("Task " + task + " loaded");
        } catch (Exception err) {
            Commentator.err(err);
        }
    }
    
    public void cmdLoadLastTask() {
        File file = new File("last.tsk");
        
        if (!file.exists()) {
            Commentator.err("Last task does not exist");
        }
        
        cmdLoadTask("last");
    }
    
    public static void main(String[] args) {
        new Main();
    }
}
