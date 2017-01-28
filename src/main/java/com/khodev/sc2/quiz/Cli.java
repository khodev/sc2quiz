/*
 * Copyright (c) 2017 Martin Saison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.khodev.sc2.quiz;

import org.apache.commons.cli.*;

public class Cli {

    private final String[] args;
    private final Options options = new Options();

    private Cli(String[] args) {
        this.args = args;
        Option helpOption = new Option("h", "help", false, "show this help");
        Option genOption = Option.builder("g").longOpt("generate").desc("Generate an sc2 quiz mod ").hasArg(true).numberOfArgs(2).argName("file.xls> <folder.SC2Mod").build();
        Option dnOption = new Option("dn", "document-name", true, "set the document title of the mod");
        dnOption.setRequired(false);
        dnOption.setArgs(1);
        Option sdOption = new Option("sd", "short-description", true, "set the short description of the mod");
        sdOption.setRequired(false);
        sdOption.setArgs(1);
        Option ldOption = new Option("ld", "long-description", true, "set the long description of the mod");
        ldOption.setRequired(false);
        ldOption.setArgs(1);
        options.addOption(helpOption);
        options.addOption(genOption);
        options.addOption(dnOption);
        options.addOption(sdOption);
        options.addOption(ldOption);
    }

    public static void main(String[] args) {
        Cli cli = new Cli(args);
        cli.parse();
    }

    private void parse() {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                help();
            } else if (cmd.hasOption("g")) {
                String src = cmd.getOptionValues("g")[0];
                String dst = cmd.getOptionValues("g")[1];
                Quiz quiz = new Quiz(src);
                if (cmd.hasOption("dn")) {
                    quiz.setDocName(cmd.getOptionValue("dn"));
                }
                if (cmd.hasOption("sd")) {
                    quiz.setShortDescription(cmd.getOptionValue("sd"));
                }
                if (cmd.hasOption("ld")) {
                    quiz.setLongDescription(cmd.getOptionValue("ld"));
                }
                quiz.export(dst);
            } else {
                help();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void help() {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("java -jar sc2quiz.jar -<command> [...]", options);
        System.exit(0);
    }

}