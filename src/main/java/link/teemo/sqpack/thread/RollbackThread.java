package link.teemo.sqpack.thread;

import link.teemo.sqpack.swing.TextPatchPanel;
import link.teemo.sqpack.util.FileUtil;

import javax.swing.*;
import java.io.File;

public class RollbackThread implements Runnable{

    private String resourceFolder;
    private TextPatchPanel textPatchPanel;

    public RollbackThread(String resourceFolder, TextPatchPanel textPatchPanel){
        this.resourceFolder = resourceFolder;
        this.textPatchPanel = textPatchPanel;
    }
    @Override
    public void run() {
        textPatchPanel.rollbackButton.setEnabled(false);
        String[] resourceNames = {"000000.win32.dat0", "000000.win32.index", "000000.win32.index2", "0a0000.win32.dat0", "0a0000.win32.index", "0a0000.win32.index2"};
        int fileCount = 0;
        for(String resourceName :resourceNames){
            textPatchPanel.percentShow((double)fileCount++ / (double)resourceNames.length);
            File backupFile = new File("backup" + File.separator + resourceName);
            if(backupFile.exists() && backupFile.isFile()){
                FileUtil.copyTo(backupFile, resourceFolder + File.separator + backupFile.getName());
            }
        }
        textPatchPanel.percentShow(0);
        JOptionPane.showMessageDialog(null, "<html><body>还原完毕</body></html>", "提示",JOptionPane.PLAIN_MESSAGE);
        textPatchPanel.rollbackButton.setEnabled(true);
    }
}
