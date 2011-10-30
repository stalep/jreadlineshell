package org.jboss.jreadlineshell.util;

import org.jboss.jreadline.console.Config;
import org.jboss.jreadline.util.Parser;
import org.jboss.jreadlineshell.file.Prompt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class FileUtils {

    public static final Pattern startsWithParent = Pattern.compile("^\\.\\..*");
    public static final Pattern containParent =
            Pattern.compile("[\\.\\.["+ Config.getPathSeparator()+"]?]+");
    public static final Pattern space = Pattern.compile(".+\\s+.+");
    public static final Pattern startsWithSlash =
            Pattern.compile("^\\"+Config.getPathSeparator()+".*");
    public static final Pattern endsWithSlash =
            Pattern.compile(".*\\"+Config.getPathSeparator()+"$");

    public static List<String> listMatchingDirectories(String possibleDir, Prompt prompt) {
        // that starts with possibleDir
        List<String> returnFiles = new ArrayList<String>();
        if (possibleDir.trim().isEmpty()) {
            List<String> allFiles = listDirectory(prompt.getCwd());
            for (String file : allFiles)
                if (file.startsWith(possibleDir))
                    returnFiles.add(file.substring(possibleDir.length()));

            return returnFiles;
        }
        else if (!startsWithSlash.matcher(possibleDir).matches() &&
                new File(prompt.getCwd().getAbsolutePath() +
                        Config.getPathSeparator() +possibleDir).isDirectory()) {
            if(!endsWithSlash.matcher(possibleDir).matches()){
                returnFiles.add("/");
                return returnFiles;
            }
            else
                return listDirectory(new File(prompt.getCwd().getAbsolutePath() +
                        Config.getPathSeparator()
                        +possibleDir));
        }
        else if(new File(prompt.getCwd().getAbsolutePath() +Config.getPathSeparator()+ possibleDir).isFile()) {
            returnFiles.add(" ");
            return returnFiles;
        }
        //else if(possibleDir.startsWith(("/")) && new File(possibleDir).isFile()) {
        else if(startsWithSlash.matcher(possibleDir).matches() &&
                new File(possibleDir).isFile()) {
            returnFiles.add(" ");
            return returnFiles;
        }
        else {
            returnFiles = new ArrayList<String>();
            if(new File(possibleDir).isDirectory() &&
                    !endsWithSlash.matcher(possibleDir).matches()) {
                returnFiles.add(Config.getPathSeparator());
                return returnFiles;
            }
            else if(new File(possibleDir).isDirectory() &&
                    !endsWithSlash.matcher(possibleDir).matches()) {
                return listDirectory(new File(possibleDir));
            }

            //1.list possibleDir.substring(pos
            String lastDir = null;
            String rest = null;
            if(possibleDir.contains(Config.getPathSeparator())) {
                lastDir = possibleDir.substring(0,possibleDir.lastIndexOf(Config.getPathSeparator()));
                rest = possibleDir.substring(possibleDir.lastIndexOf(Config.getPathSeparator())+1);
            }
            else {
                if(new File(prompt.getCwd()+Config.getPathSeparator()+possibleDir).exists())
                    lastDir = possibleDir;
                else {
                    rest = possibleDir;
                }
            }
            //System.out.println("rest:"+rest);
            //System.out.println("lastDir:"+lastDir);

            List<String> allFiles;
            if(startsWithSlash.matcher(possibleDir).matches())
                allFiles =  listDirectory(new File(Config.getPathSeparator()+lastDir));
            else if(lastDir != null)
                allFiles =  listDirectory(new File(prompt.getCwd()+
                        Config.getPathSeparator()+lastDir));
            else
                allFiles =  listDirectory(prompt.getCwd());

            //TODO: optimize
            //1. remove those that do not start with rest, if its more than one
            if(rest != null && !rest.isEmpty())
                for (String file : allFiles)
                    if (file.startsWith(rest))
                        //returnFiles.add(file);
                        returnFiles.add(file.substring(rest.length()));

            if(returnFiles.size() > 1) {
                String startsWith = Parser.findStartsWith(returnFiles);
                if(startsWith != null && startsWith.length() > 0) {
                    returnFiles.clear();
                    returnFiles.add(startsWith);
                }
                //need to list complete filenames
                else {
                    returnFiles.clear();
                    for (String file : allFiles)
                        if (file.startsWith(rest))
                            returnFiles.add(file);
                }
            }

            return returnFiles;

        }
    }

    public static List<String> listDirectory(File path) {
        List<String> fileNames = new ArrayList<String>();
        if(path != null && path.isDirectory())
            for(File file : path.listFiles())
                fileNames.add(file.getName());

        return fileNames;
    }

    public static String getDirectoryName(File path, File home) {
        if(path.getAbsolutePath().startsWith(home.getAbsolutePath()))
            return "~"+path.getAbsolutePath().substring(home.getAbsolutePath().length());
        else
            return path.getAbsolutePath();
    }

    /**
     * Parse file name
     * 1. .. = parent dir
     * 2. ~ = home dir
     *
     *
     * @param name file
     * @param cwd current working directory
     * @return file correct file
     */
    public static File getFile(String name, String cwd) {
        //contains ..
        if(containParent.matcher(name).matches()) {
            if(startsWithParent.matcher(name).matches()) {

            }

        }
        else if(name.startsWith("~")) {

        }
        else
            return new File(name);

        return null;
    }
}
