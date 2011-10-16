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
package org.jboss.jreadlineshell;


import org.jboss.jreadline.complete.Completion;
import org.jboss.jreadline.console.Console;
import org.jboss.jreadlineshell.file.Command;
import org.jboss.jreadlineshell.file.Ls;
import org.jboss.jreadlineshell.file.Prompt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle W. Pedersen <stale.pedersen@jboss.org>
 */
public class Shell {

    public static void main(String[] args) throws Exception {

        Prompt prompt =
                new Prompt(System.getProperty("user.name"),
                        "beistet",
                        System.getProperty("user.home"),
                        System.getProperty("user.dir"));

        Console console = new Console();
        Ls ls = new Ls(prompt);
        List<Command> commands = new ArrayList<Command>();
        commands.add(ls);

        List<Completion> completions = new ArrayList<Completion>();
        completions.add(ls);

        String line;

        console.addCompletions(completions);
        //console.pushToConsole(ANSIColors.GREEN_TEXT());
        while ((line = console.read(prompt.getPrompt())) != null) {

            boolean matched = false;
            for(Command cmd : commands)
                    if(cmd.matchCommand(line)) {
                        console.pushToConsole(cmd.runCommand(line.trim()));
                        matched = true;
                    }

            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                break;
            }

            if(!matched) {
                console.pushToConsole(line+". Command not found.");
            }
        }
    }
}
