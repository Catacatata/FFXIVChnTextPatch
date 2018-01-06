package link.teemo.sqpack.thread;

import link.teemo.sqpack.ReplaceEXDFKt;
import link.teemo.sqpack.ReplaceFont;
import link.teemo.sqpack.swing.TextPathPanel;

import javax.swing.*;
import java.io.File;

public class ReplaceThread implements Runnable{

    private String resourceFolder;
    private TextPathPanel textPathPanel;

    public ReplaceThread(String resourceFolder, TextPathPanel textPathPanel){
        this.resourceFolder = resourceFolder;
        this.textPathPanel = textPathPanel;
    }
    @Override
    public void run() {
        try {
            textPathPanel.replaceButton.setEnabled(false);
            //打字体补丁
            new ReplaceFont(resourceFolder + File.separator + "000000.win32.index", "resource" + File.separator + "font").replaceFont();
            //汉化补丁
            ReplaceEXDFKt.replace(resourceFolder + File.separator + "0a0000.win32.index", "resource" + File.separator + "text", textPathPanel);
            textPathPanel.percentShow(0);
            JOptionPane.showMessageDialog(null, "<html><body>汉化完毕</body></html>", "提示",JOptionPane.PLAIN_MESSAGE);
            textPathPanel.replaceButton.setEnabled(true);
        }catch (Exception exception){
            JOptionPane.showMessageDialog(null, "<html><body>程序跑飞啦！</body></html>", "汉化错误",JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }
}
