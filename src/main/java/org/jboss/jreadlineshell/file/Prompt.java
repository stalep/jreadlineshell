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

import org.jboss.jreadline.terminal.ANSIColors;
import org.jboss.jreadlineshell.util.FileUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle W. Pedersen <stale.pedersen@jboss.org>
 */
public class Prompt {

    private File home;
    private File cwd; //currentWorkingDirectory
    private String userName;
    private String machineName;
    private StringBuilder promptFirstPart;
    private StringBuilder prompt;
    private StringBuilder promptSecondPart;

    public Prompt(String userName, String home, String cwd) {
        this(userName, null, home, cwd);
    }

    public Prompt(String userName, String machineName, String home, String cwd) {
        this.userName = userName;
        this.home = new File(home);
        this.cwd = new File(cwd);
        if(!this.home.isDirectory() || !this.cwd.isDirectory())
            throw new RuntimeException(("home and cwd must be a directory"));

        if(machineName == null || machineName.trim().length() < 1) {
            try {
                this.machineName = InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException e) {
            }
        }
        if(this.machineName == null)
            this.machineName = "localhost";

        promptFirstPart = new StringBuilder();
        promptFirstPart.append(ANSIColors.BLUE_TEXT()).append("[")
              .append(ANSIColors.RED_TEXT()).append(userName).append("@")
              .append(this.machineName).append(":");
        promptSecondPart = new StringBuilder();
        promptSecondPart.append(ANSIColors.BLUE_TEXT()).append("]")
                .append(ANSIColors.WHITE_TEXT()).append("$ ");

    }


    public String getPrompt() {
        List<String> output = new ArrayList<String>();
            return new StringBuilder().append(promptFirstPart)
                    .append(FileUtils.getDirectoryName(cwd, home))
                    .append(promptSecondPart)
                    .toString();
    }

    public File getCwd() {
        return cwd;
    }

    public void setCwd(File cwd) {
        this.cwd = cwd;
        if(!this.cwd.isDirectory())
            throw new RuntimeException("Working directory isnt a directory");
    }

    public File getHome() {
        return home;
    }

}
