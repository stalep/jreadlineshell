/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.jreadlineshell.file;

import org.jboss.jreadline.command.Command;
import org.jboss.jreadline.complete.Completion;
import org.jboss.jreadline.util.Parser;
import org.jboss.jreadlineshell.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Ståle W. Pedersen <stale.pedersen@jboss.org>
 */
public class Ls implements Completion, Command {

    private int height;
    private int width;
    private static final String command = "ls";
    private Prompt prompt;
    private Pattern startsWithBack = Pattern.compile("^\\.\\..*");
    private Pattern containBack = Pattern.compile("[\\.\\.[/]?]+");
    private Pattern space = Pattern.compile(".+\\s+.+");

    public Ls(Prompt prompt, int height, int width) {
        this.prompt = prompt;
        this.height = height;
        this.width = width;
    }

    @Override
    public List<String> complete(String s, int cursor) {
        List<String> completeList = new ArrayList<String>();
        if(s.trim().length() < 1 || s.equals(command) || s.equals("l")) {
            completeList.add(command);
        }
        else if(s.startsWith("ls ")) {
            //String rest = s.substring("ls ".length());

            String word = Parser.findWordClosestToCursor(s, cursor);
            completeList.addAll(FileUtils.listMatchingDirectories(word, prompt));
        }
        return completeList;
    }

    @Override
    public boolean matchCommand(String cmd) {
        return cmd.startsWith(command);
    }

    @Override
    public String executeCommand(String cmd) {
        //System.out.println("running command:"+cmd+":");
        StringBuilder builder = new StringBuilder();
        File dir = null;
        if(cmd.equals(command))
            dir = prompt.getCwd();
        else {
            String rest = cmd.substring(command.length()).trim();
            //if rest doesnt contain ' '
            if(!FileUtils.space.matcher(rest).matches()) {
                if(FileUtils.containParent.matcher(rest).matches()) {

                }
            }

            else {

            }

            if(rest.contains(" ")) {
                System.out.println("list multiple files");
                //TODO: need to sort files and folders and list them accordingly
                /*
                for(String s : rest.split(" ")) {
                    File f = new File(s);
                    if(f.isDirectory()) {
                        builder.append(Parser.formatCompletions(f.list(), 80, 80));
                    }
                }
                */
            }
            else {
                if(FileUtils.startsWithSlash.matcher(rest).matches())
                    //if(rest.startsWith("/"))
                    dir = new File(rest);
                else if(rest.startsWith("~")) {
                    dir = prompt.getHome();
                }
                else
                    dir = new File(prompt.getCwd().getAbsolutePath()+"/"+rest);
            }
        }

        if(dir != null && dir.isDirectory()) {
            builder.append(Parser.formatCompletions(FileUtils.listDirectory(dir), height, width));
            /*
            for(String fileName : listDirectory(dir))
                builder.append(fileName).append("  ");
            */
            }
        else
            builder.append(dir).append("\n");

        return builder.toString();
    }
}
