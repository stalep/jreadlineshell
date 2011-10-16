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

import org.jboss.jreadline.complete.Completion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle W. Pedersen <stale.pedersen@jboss.org>
 */
public class Ls implements Completion, Command {

    private static final String command = new String("ls");
    private Prompt prompt;

    public Ls(Prompt prompt) {
        this.prompt = prompt;
    }

    @Override
    public List<String> complete(String s, int i) {
        List<String> completeList = new ArrayList<String>();
        if(s.trim().length() < 1 || s.equals(command) || s.equals("l")) {
            completeList.add(command);
        }
        else if(s.startsWith("ls ")) {
           String rest = s.substring(command.length()).trim();
            //System.out.println(" need to complete file/pathnames. rest:"+rest+":");
            if(rest.length() > 0)
                completeList.addAll(listMatchingDirectories(rest));
        }
        return completeList;
    }

    private List<String> listDirectory(File path) {
        List<String> fileNames = new ArrayList<String>();
        for(File file : path.listFiles())
            fileNames.add(file.getName());

        return fileNames;
    }

    @Override
    public boolean matchCommand(String cmd) {
        if(cmd.startsWith(command))
            return true;
        else
            return false;
    }

    @Override
    public String runCommand(String cmd) {
        //System.out.println("running command:"+cmd+":");
        StringBuilder builder = new StringBuilder();
        File dir = null;
        if(cmd.equals(command))
            dir = prompt.getCwd();
        else {
            String rest = cmd.substring(command.length()).trim();
            if(rest.contains(" ")) {
                System.out.println("list multiple files");
            }
            else {
                if(rest.startsWith("/"))
                    dir = new File(rest);
                else if(rest.startsWith("~")) {
                    dir = prompt.getHome();
                }
                else
                    dir = new File(prompt.getCwd().getAbsolutePath()+"/"+rest);
            }
        }

        if(dir != null && dir.isDirectory()) {
            for(String fileName : listDirectory(dir))
                builder.append(fileName).append("  ");
            }
        else
            builder.append(dir);

        return builder.toString();
    }

    private List<String> listMatchingDirectories(String possibleDir) {
        //System.out.println("looking for: " + possibleDir);
        List<String> returnFiles;
        if (new File(prompt.getCwd().getAbsolutePath() + "/" + possibleDir).isDirectory()) {
            //System.out.println("possibleDir is a dir, return those");
            if(!possibleDir.endsWith("/")) {
               returnFiles = new ArrayList<String>();
                returnFiles.add("/");
                return returnFiles;
            }
            else
                return listDirectory(new File(prompt.getCwd().getAbsolutePath() + "/" + possibleDir));
        }
        //should check if possibleDir contain /
        else if(possibleDir.contains("/")) {
            //1.list possibleDir.substring(pos
            String lastDir = possibleDir.substring(0,possibleDir.lastIndexOf("/"));
            String rest = possibleDir.substring(possibleDir.lastIndexOf("/")+1);
            //System.out.println("rest:"+rest);
            //System.out.println("lastDir:"+lastDir);
            List<String> allFiles = listDirectory(new File(prompt.getCwd()+"/"+lastDir));
            returnFiles = new ArrayList<String>();
            for (String file : allFiles)
                if (file.startsWith(rest))
                    returnFiles.add(file.substring(rest.length()));

            return returnFiles;

        }
        else {
            List<String> allFiles = listDirectory(prompt.getCwd());
            returnFiles = new ArrayList<String>();
            for (String file : allFiles)
                if (file.startsWith(possibleDir))
                    returnFiles.add(file.substring(possibleDir.length()));

            return returnFiles;
        }

    }
}
