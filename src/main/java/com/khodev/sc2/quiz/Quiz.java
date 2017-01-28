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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class Quiz {

    private final static ModFile mod =
            new ModFile("quiz.SC2Mod", true)
                    .add(ModFile.newDir("Base.SC2Data")
                            .add(ModFile.newDir("GameData")
                                    .add(ModFile.newFile("GameData.xml"))
                                    .add(ModFile.newFile("UserData.xml"))
                            )
                            .add(ModFile.newFile("Lib9F1ED8E8.galaxy"))
                            .add(ModFile.newFile("Lib9F1ED8E8_h.galaxy"))
                    )
                    .add(ModFile.newDir("enUS.SC2Data")
                            .add(ModFile.newDir("LocalizedData")
                                    .add(ModFile.newFile("GameStrings.txt"))
                                    .add(ModFile.newFile("ObjectStrings.txt"))
                                    .add(ModFile.newFile("TriggerStrings.txt"))
                            )
                    )
                    .add(ModFile.newDir("ComponentList.SC2Components"))
                    .add(ModFile.newFile("DocumentHeader"))
                    .add(ModFile.newFile("DocumentInfo"))
                    .add(ModFile.newFile("DocumentInfo.version"))
                    .add(ModFile.newFile("GameData.version"))
                    .add(ModFile.newFile("GameText.version"))
                    .add(ModFile.newFile("PreloadAssetDB.txt"))
                    .add(ModFile.newFile("Triggers"))
                    .add(ModFile.newFile("Triggers.version")
                    );
    private final List<Question> questions = new ArrayList<>();
    private String docName = "Quiz mod";
    private String shortDescription = "Generate your own quiz";
    private String longDescription = "This quiz was generated using https://github.com/khodev/sc2quiz.git. Feel free to use it and generate your favorite questions.";

    void setDocName(String docName) {
        this.docName = docName;
    }

    void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Quiz(String xls) throws IOException {
        FileInputStream file = new FileInputStream(xls);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        boolean headerSkipped = false;
        for (Row row : sheet) {
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            questions.add(new Question(
                    row.getCell(2).toString(),
                    row.getCell(3).toString()
            ));
        }

    }

    void export(String modPath) throws Exception {
        File modDir = new File(modPath);
        if (modDir.exists() && !modDir.isDirectory()) {
            throw new Exception("Destination already exists as a file: " + modPath);
        }
        if (!modDir.mkdir()) {
            throw new Exception("Cannot create dir: " + modPath);
        }

        mod.browse(new ModFileHandler() {
            @Override
            public void accept(ModFile file) throws IOException {
                if (file.isDirectory()) {
                    File dir = new File(modPath, file.getPath());
                    if (!dir.mkdir()) {
                        throw new IOException("Cannot create " + dir.getPath());
                    }
                } else {
                    File fileToWrite = new File(modPath, file.getPath());
                    switch (file.getName()) {
                        case "GameStrings.txt":
                            writeGameStrings(new FileOutputStream(fileToWrite));
                            break;
                        case "UserData.xml":
                            writeUserData(new FileOutputStream(fileToWrite));
                            break;
                        default:
                            Files.copy(this.getClass().getResource("/" + file.getPath()).openStream(), fileToWrite.toPath());
                            break;
                    }
                    System.out.println(file.getPath());
                }
            }
        });

    }


    private void writeGameStrings(OutputStream f) throws IOException {
        PrintStream p = new PrintStream(f, true, "UTF-8");
        System.setProperty("line.separator", "\n");
        p.println("DocInfo/Name=" + this.docName);
        p.println("DocInfo/DescShort=" + this.shortDescription);
        p.println("DocInfo/DescLong=" + this.longDescription);
        p.println("Param/Value/lib_9F1ED8E8_323DDC51=You missed it, remember next time: ");
        p.println("Param/Value/lib_9F1ED8E8_893604FF=Congratulations, you won 400 minerals!");
        p.println("Param/Value/lib_9F1ED8E8_AC21E3F1= = ");
        int i = 0;
        for (Question question : questions) {
            i++;
            p.println("UserData/QuizDictionary/" + i + "_Question=" + question.getQuestion());
            p.println("UserData/QuizDictionary/" + i + "_Answer=" + question.getAnswer());
        }
        p.flush();
        p.close();
    }

    private void writeUserData(OutputStream s) throws IOException {
        Element root = new Element("Catalog");
        Document d = new Document(root);
        Element user = new Element("CUser").setAttribute("id", "QuizDictionary");
        root.addContent(user);
        user.addContent(
                new Element("Fields")
                        .setAttribute("EditorColumn", "1")
                        .setAttribute("Id", "Level")
                        .setAttribute("Type", "Int")
        );
        user.addContent(
                new Element("Fields")
                        .setAttribute("EditorColumn", "2")
                        .setAttribute("Id", "Question")
                        .setAttribute("Type", "Text")
        );
        user.addContent(
                new Element("Fields")
                        .setAttribute("EditorColumn", "3")
                        .setAttribute("Id", "Answer")
                        .setAttribute("Type", "Text")
        );
        user.addContent(
                new Element("Instances")
                        .setAttribute("Id", "[Default]")
        );

        int i = 0;
        for (Question question : questions) {
            i++;
            Element instance = new Element("Instances").setAttribute("Id", "" + i);
            user.addContent(instance);
            Element q = new Element("Text")
                    .setAttribute("Text", "UserData/QuizDictionary/" + i + "_Question");
            instance.addContent(q);
            q.addContent(new Element("Field").setAttribute("Id", "Question"));
            Element r = new Element("Text")
                    .setAttribute("Text", "UserData/QuizDictionary/" + i + "_Answer");
            instance.addContent(r);
            r.addContent(new Element("Field").setAttribute("Id", "Answer"));
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(d, s);
    }

}
