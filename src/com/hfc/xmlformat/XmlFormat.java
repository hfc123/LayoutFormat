package com.hfc.xmlformat;

import com.hfc.xmlformat.interfaces.DimenXmls;
import com.hfc.xmlformat.interfaces.FileUitilInterface;
import com.hfc.xmlformat.interfaces.StringXmls;
import com.hfc.xmlformat.utils.DimenXmlsImpl;
import com.hfc.xmlformat.utils.FileUtils;
import com.hfc.xmlformat.utils.StringXmlsImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.Map;

public class XmlFormat extends AnAction {
    FileUitilInterface  fileUitil= null;
    StringXmls stringXmls= null;
    DimenXmls dimenXmls= null;
    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        final Project project = getEventProject(event);
        VirtualFile file = event.getData(LangDataKeys.VIRTUAL_FILE);
        System.out.println(file.getPath());
        Logger.init("JoLiveBuild", Logger.DEBUG);
        showNotification(project,MessageType.ERROR,file.getPath());
        initUtils(file);
        execute(project,file);
        systemReformat(event);
    }
    public static void showNotification(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    private void initUtils(VirtualFile file) {
        fileUitil  =new FileUtils();
        stringXmls =new StringXmlsImpl();
        dimenXmls  =new DimenXmlsImpl();
        ((FileUtils) fileUitil).init(file);
        try {
            ((StringXmlsImpl) stringXmls).init(fileUitil.getStringFilePath());
            ((DimenXmlsImpl)  dimenXmls).init(fileUitil.getDimeonFilePath());
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    private static String lineSeparator() {
        return "\n";
    }

    private void execute(Project project, final VirtualFile file) {
        VirtualFile[] files = file.getChildren();
        if (files.length > 0) {
            for (VirtualFile _file : files) {
//                if (Files.isXmlFileOrDir(_file)) {
                    execute(project, _file);
//                }
                // fileUitil.replaceDimenAndString(stringMaps,dimenMaps,file)
            }
        } else {
            System.out.println(file.getPath());
            final Document document = FileDocumentManager.getInstance().getDocument(file);
            new WriteCommandAction.Simple(project) {
                @Override protected void run() throws Throwable {
                    String txt = fileUitil.replaceDimenAndString(stringXmls,dimenXmls,file);
                   // Logger.error(txt);
                    document.setText(txt );
                    stringXmls.saveMaps();
                    dimenXmls .saveMaps();

                }
            }.execute();
        }
    }
    private void systemReformat(final AnActionEvent event) {
        event.getActionManager()
                .getAction(IdeActions.ACTION_EDITOR_REFORMAT)
                .actionPerformed(event);
    }
}
