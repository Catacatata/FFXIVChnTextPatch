package link.teemo.sqpack.thread;

import link.teemo.sqpack.ReplaceEXDFKt;
import link.teemo.sqpack.ReplaceFont;
import link.teemo.sqpack.swing.TextPathPanel;
import link.teemo.sqpack.util.FileUtil;

import javax.swing.*;
import java.io.File;

public class RollbackThread implements Runnable{

    private String resourceFolder;
    private TextPathPanel textPathPanel;

    public RollbackThread(String resourceFolder, TextPathPanel textPathPanel){
        this.resourceFolder = resourceFolder;
        this.textPathPanel = textPathPanel;
    }
    @Override
    public void run() {
        textPathPanel.rollbackButton.setEnabled(false);
        String[] resourceNames = {"000000.win32.dat0", "000000.win32.index", "000000.win32.index2", "0a0000.win32.dat0", "0a0000.win32.index", "0a0000.win32.index2"};
        int fileCount = 0;
        for(String resourceName :resourceNames){
            textPathPanel.percentShow((double)fileCount++ / (double)resourceNames.length);
            File backupFile = new File("backup" + File.separator + resourceName);
            if(backupFile.exists() && backupFile.isFile()){
                FileUtil.copyTo(backupFile, resourceFolder + File.separator + backupFile.getName());
            }
        }
        textPathPanel.percentShow(0);
        JOptionPane.showMessageDialog(null, "<html><body>还原完毕</body></html>", "提示",JOptionPane.PLAIN_MESSAGE);
        textPathPanel.rollbackButton.setEnabled(true);
    }
}
