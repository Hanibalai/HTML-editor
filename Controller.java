package com.javarush.task.task32.task3209;

import javax.swing.*;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller {
    private View view;
    private HTMLDocument document;
    private File currentFile;

    public Controller(View view) {
        this.view = view;
    }

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();
    }

    public HTMLDocument getDocument() {
        return document;
    }

    public void resetDocument() {
        if (document != null) {
            document.removeUndoableEditListener(view.getUndoListener());
        }
        HTMLEditorKit editorKit = new HTMLEditorKit();
        document = (HTMLDocument) editorKit.createDefaultDocument();
        document.addUndoableEditListener(view.getUndoListener());
        view.update();
    }

    public void setPlainText(String text) {
        resetDocument();
        StringReader reader = new StringReader(text);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        try {
            editorKit.read(reader, document, 0);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    public String getPlainText() {
        StringWriter writer = new StringWriter();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        try {
            editorKit.write(writer, document, 0, document.getLength());
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
        return writer.toString();
    }

    public void init() {
        createNewDocument();
    }

    public void exit() {
        System.exit(0);
    }

    public void createNewDocument() {
        view.selectHtmlTab();
        resetDocument();
        view.setTitle("HTML редактор");
        currentFile = null;
    }

    public void openDocument() {
        view.selectHtmlTab();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new HTMLFileFilter());
        fileChooser.setDialogTitle("Open File");
        int index = fileChooser.showOpenDialog(view);
        if (index == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            resetDocument();
            view.setTitle(currentFile.getName());
            try (FileReader reader = new FileReader(currentFile)) {
                new HTMLEditorKit().read(reader, document, 0);
                view.resetUndo();
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }

    public void saveDocument() {
        view.selectHtmlTab();
        if (currentFile != null) {
            try (FileWriter writer = new FileWriter(currentFile)) {
                view.setTitle(currentFile.getName());
                new HTMLEditorKit().write(writer, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        } else saveDocumentAs();
    }

    public void saveDocumentAs() {
        view.selectHtmlTab();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new HTMLFileFilter());
        fileChooser.setDialogTitle("Save File");
        int index = fileChooser.showSaveDialog(view);
        if (index == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            view.setTitle(currentFile.getName());
            try (FileWriter writer = new FileWriter(currentFile)) {
                new HTMLEditorKit().write(writer, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }
}
