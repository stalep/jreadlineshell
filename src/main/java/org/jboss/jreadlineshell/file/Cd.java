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
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class Cd implements Command, Completion{

    private Prompt prompt;
    private static final String command = "cd";

    public Cd(Prompt prompt) {
        this.prompt = prompt;
    }

    @Override
    public boolean matchCommand(String cmd) {
        return cmd.startsWith(command);
    }

    @Override
    public String runCommand(String cmd) {
        if(cmd.trim().equals(command)) {
            prompt.setCwd(prompt.getHome());
            return null;
        }
        else {
            String rest = cmd.substring(command.length()).trim();
            // if it contains ' ', we only use the first argument
            if(rest.contains(" ")) {
               rest = rest.substring(0, rest.indexOf(" "));
            }

            File file;
            if(rest.startsWith("/"))
                file = new File(rest);
            else
                file = new File(prompt.getCwd()+"/"+rest);
            if(file.isDirectory()) {
                prompt.setCwd(file.getAbsoluteFile());
                return null;
            }
            else {
               return rest+". is not a directory.";
            }
        }
    }

    @Override
    public List<String> complete(String s, int i) {
        List<String> completeList = new ArrayList<String>();
        if(s.trim().isEmpty() || s.startsWith("cd ") || s.equals("cd") || s.equals("c"))
            completeList.add("cd");

        return completeList;
    }
}
