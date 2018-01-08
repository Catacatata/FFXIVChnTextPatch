package link.teemo.sqpack.thread;

import com.shenou.fs.core.utils.res.Config;
import link.teemo.sqpack.ReplaceEXDFKt;
import link.teemo.sqpack.ReplaceFont;
import link.teemo.sqpack.swing.TextPatchPanel;

import javax.swing.*;
import java.io.File;

public class ReplaceThread implements Runnable{

   private String resourceFolder;
    private TextPatchPanel textPatchPanel;

    public ReplaceThread(String resourceFolder, TextPatchPanel textPatchPanel){
        this.resourceFolder = resourceFolder;
        this.textPatchPanel = textPatchPanel;
    }
    @Override
    public void run() {
        try {
            textPatchPanel.replaceButton.setEnabled(false);
            //打字体补丁
//            new ReplaceFont(resourceFolder + File.separator + "000000.win32.index", "resource" + File.separator + "font").replaceFont();
            //汉化补丁
            ReplaceEXDFKt.replace(resourceFolder + File.separator + "0a0000.win32.index", "resource" + File.separator + "text", textPatchPanel);
            textPatchPanel.percentShow(0);
            String lang = Config.getProperty("Language");
            switch(lang){
                case "日文":
                    lang = "日化";
                    break;
                case "英文":
                    lang = "英化";
                    break;
                case "法文":
                    lang = "法化";
                    break;
                case "德文":
                    lang = "德化";
                    break;
                default:
                    lang = "日化";
            }
            JOptionPane.showMessageDialog(null, "<html><body>" + lang + "完毕</body></html>", "提示",JOptionPane.PLAIN_MESSAGE);
            textPatchPanel.replaceButton.setEnabled(true);
        }catch (Exception exception){
            String lang = Config.getProperty("Language");
            switch(lang){
                case "日文":
                    lang = "日化";
                    break;
                case "英文":
                    lang = "英化";
                    break;
                case "法文":
                    lang = "法化";
                    break;
                case "德文":
                    lang = "德化";
                    break;
                default:
                    lang = "日化";
            }
            JOptionPane.showMessageDialog(null, "<html><body>程序跑飞啦！</body></html>", "" + lang + "错误",JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }
}
