package link.teemo.sqpack.swing;

import com.shenou.fs.core.utils.res.Config;
import link.teemo.sqpack.thread.ReplaceThread;
import link.teemo.sqpack.thread.RollbackThread;
import link.teemo.sqpack.util.FileUtil;
import link.teemo.sqpack.util.HexUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class TextPatchPanel extends JFrame implements ActionListener {

    private static Point origin = new Point();
    private static String title = "提莫苑|FFXIV国际化 β-4.1";

    private JLabel title_lable = new JLabel(title);
    private Dimension dimension;

    private JButton configButton = new JButton("设置");
    private JButton toolButton = new JButton("工具");
    private JButton closeButton = new JButton("x");
    private JPanel titlePanel = new JPanel();
    private JPanel bodyPanel = new JPanel();

    public JButton replaceButton = new JButton("汉化");
    public JButton rollbackButton = new JButton("还原");

    private JPanel percentBackPanel = new JPanel();
    private JPanel percentFrontPanel = new JPanel();

    public TextPatchPanel(){
        super(title);
        setUndecorated(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setIconImage(toolkit.createImage(HexUtils.hexStringToBytes("89504E470D0A1A0A0000000D4948445200000040000000400806000000AA6971DE000004EC4944415478DAED5B69485451141EB50DB3C56AA821A329C7596CD459A2FA57467B513F0209F147CB0F2B82C20ACA928ACA3F2544605909051651D962641B914542942456602B6DE492AD56A29665DF9137F27ACD7BB3F4EECCBB830F0EF7CD9B33EFDEEFDC73CE3DF79C3B3A5D082EB7DB3D283535D5EE703816B85CAE5CA7D3791C7407F74FF1DD1BB4F5681BD0D6815E821EE1FB4AB487D1AE453B2B2525C58A77F4D7F174198DC67E003D07200A00A20AD40EA09D81127EF71BEF20C194A05D6AB3D90C5AC6DD0B839C8FC19E007D0F06B01F02F905BA857EB2939393E334811A031982016DC6C0DEB2002D47E8F32BFA2C84798C090B70D8663C06918F417C535261B27750332B41E0DD3F4007ED76FBA850618F06F015E8FC83C2C07E92C3831F28C1FD975068044D04FA5B47A6C80C39A46C232FAD34E3684FA5A5A5CD236F1F4A9310994635CCC2A23A78805B056A5300FF009D4F062D24FB0C0778B1366012B254016E32990602D04985CEDAA17A5B70DF1B7C79821674869B842574DB7F81872A25E045F7153A790E1E1758A3D0D93E2D00F76212FB697C0183870A8DA3684DE1C56514E9D1CBA10107B4085E34D68280C163769B141C1DBD309A786929D432780F619256FAABF6168ACFE5C0E345EB3DBC00BF4C2B36EF874F68C378C72B82B75AAD43C9AE159C4A8E484B9C78DECA037811861AD93881BC38186E2AA8D00E116F2C789FF104DEA72928D932BE3B26F6A4F8BC9B47F002BD008498BFC0432A53F045875C6445DB5B11AF438E97235398290E74FAE2C11319E616D8BA5912115EE619BC4005E2D9CF5590D41AA9A6F0E2F57D68C0450FA0C1F8F05986B15A6A2B78561E01B34F02B8D195AFC3CD2605C674C9EC275146264204709A6C792A25226518AE7B592576450278C1B1E791000AE518E0F86648F047853AE5C5729748A13E09A05686E9B174F7841F4C8894D907EE339EE5EC938C7A6CF0A2FE791102BEBE3BB5EE2D9949EAE12DCB8AE7D722007C1D1569C4B3FAD10BE37B3C9F6DB7DB1345D15F14CBAC6E889C5E39300D97CE6A8D2F67016A9111142FF490CA72729B9F9C48716CD2F09D7298A0698AA9302A6C60862F45C2B246C517002EA28C744085548BC532003FBAC01160AA02D5824A29130D9AFB8F6D0773619DCF10CAD2E1DCE874600C8D68A9BE705528916FA74228804E27C74C491BA6352F746444C74B4045E8AC02ED6B7FD35E42E5B6992246A1CE7F97964F9A2DDC1753124528A25281258BCAE76827510E12CFF59E24AB262F2A4593AA2188180DB089341B142F4073469ACDE6617ABD3E2EA8DC7BCFD573755F06832196340C9A3591D66E98C57298C8D6AE8D0BAF179DEF0198C594592230A03D007604748E9212E4F0845D66AB42167723B70250A36A04019DE75900875488E31BB9150066AF4C8D1801ABD0082E0580C1DF5343005E3254DC6840934A51E26AEEC053F0A456680D3FB097C725D0AD66428347FBCF54B9ACCD9DFDEF545100EF788C01AEA8B955E66EB3E5E32469C044C7F578527F93DA0913DA7AF3A4FED96A0B206CA7C283D4805206677B9278C11FC3A29680B8622C17E885F3469D0C4C208117F52F642100AA63F010FFF7517BF9F3649C75D2A36D1AF5FE8B18D50C1A7851FFDB8CCA5F559A078F40259D61FDEF2C0FA16F25C3FA7EBED6673F83F101E74C2D273EE285FF00331300937F81A9E8F98B19D7FF9B349DF7A39A3D63011CD5B2F9D31FA62B581C9F174E7EBCA2E3F92C06FE07D4147657975EC1240000000049454E44AE426082")));
        dimension=toolkit.getScreenSize();
        setBounds((dimension.width-290)/2, (dimension.height-130)/2, 290, 130);
        setVisible(true);
        setResizable(false);
        setLayout(null);
        //标题栏
        titlePanel.setBounds(0, 0, 290, 30);
        titlePanel.setBackground(new Color(110,110,110));
        titlePanel.setBorder(new MatteBorder(0, 0, 0, 0, new Color(110,110,110)));
        add(titlePanel);
        bodyPanel.setBounds(0, 0, 290, 130);
        bodyPanel.setBackground(new Color(255,255,255));
        bodyPanel.setBorder(new MatteBorder(0, 1, 1, 1, new Color(110,110,110)));
        add(bodyPanel);
        title_lable.setBounds(10, 0, 160, 30);
        title_lable.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        title_lable.setForeground(new Color(255,255,255));
        add(title_lable,0);
        //最小化以及关闭
        configButton.setBounds(230, 0, 25, 30);
        configButton.setFont(new Font("Microsoft Yahei",Font.BOLD,12));
        configButton.setForeground(new Color(255,255,255));
        configButton.setMargin(new Insets(0, 0, 0, 0));
        configButton.setBorder(null);
        configButton.setOpaque(false);
        configButton.setIconTextGap(0);
        configButton.setContentAreaFilled(false);
        configButton.setFocusable(false);
        configButton.addActionListener(this);
        add(configButton,0);

//        toolButton.setBounds(200, 0, 25, 30);
//        toolButton.setFont(new Font("Microsoft Yahei",Font.BOLD,12));
//        toolButton.setForeground(new Color(255,255,255));
//        toolButton.setMargin(new Insets(0, 0, 0, 0));
//        toolButton.setBorder(null);
//        toolButton.setOpaque(false);
//        toolButton.setIconTextGap(0);
//        toolButton.setContentAreaFilled(false);
//        toolButton.setFocusable(false);
//        toolButton.addActionListener(this);
//        add(toolButton,0);

        closeButton.setBounds(260, 0, 20, 30);
        closeButton.setFont(new Font("Microsoft Yahei",Font.BOLD,12));
        closeButton.setForeground(new Color(255,255,255));
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setBorder(null);
        closeButton.setOpaque(false);
        closeButton.setIconTextGap(0);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(this);
        add(closeButton,0);
        //拖拽功能
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY()- origin.y);
            }
        });
        //主要面板
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
        replaceButton.setText(lang);
        replaceButton.setBounds(30, 40, 80, 60);
        replaceButton.setFont(new Font("Microsoft Yahei",Font.PLAIN,25));
        replaceButton.setForeground(new Color(110,110,110));
        replaceButton.setMargin(new Insets(0, 0, 0, 0));
        replaceButton.setOpaque(false);
        replaceButton.setIconTextGap(0);
        replaceButton.setContentAreaFilled(false);
        replaceButton.setFocusable(false);
        replaceButton.addActionListener(this);
        add(replaceButton,0);

        rollbackButton.setBounds(180, 40, 80, 60);
        rollbackButton.setFont(new Font("Microsoft Yahei",Font.PLAIN,25));
        rollbackButton.setForeground(new Color(110,110,110));
        rollbackButton.setMargin(new Insets(0, 0, 0, 0));
        rollbackButton.setOpaque(false);
        rollbackButton.setIconTextGap(0);
        rollbackButton.setContentAreaFilled(false);
        rollbackButton.setFocusable(false);
        rollbackButton.addActionListener(this);
        add(rollbackButton,0);

        percentBackPanel.setBounds(30, 110, 230, 10);
        percentBackPanel.setForeground(new Color(255,255,255));
        percentBackPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(110,110,110)));
        add(percentBackPanel,0);

        percentFrontPanel.setBounds(30, 110, 0, 10);
        percentFrontPanel.setBackground(new Color(110,110,110));
        percentFrontPanel.setBorder(new MatteBorder(0, 0, 0, 0, new Color(110,110,110)));
        add(percentFrontPanel,0);

        if(new File("backup").exists() && new File("backup").isDirectory() && new File("backup").listFiles().length == 3){
            rollbackButton.setEnabled(true);
        }else{
            rollbackButton.setEnabled(false);
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == replaceButton) {
            String path = Config.getProperty("GamePath");
            String lang =Config.getProperty("Language");
            System.out.println("游戏路径：" + path);
            System.out.println("目标语言：" + lang);
            if(isFFXIVFloder(path)){
                //备份原文件
                String resourceFolder = path + File.separator + "game" + File.separator + "sqpack" + File.separator + "ffxiv";
                String[] resourceNames = {"0a0000.win32.dat0", "0a0000.win32.index", "0a0000.win32.index2"};
                for(String resourceName :resourceNames){
                    File resourceFile = new File(resourceFolder + File.separator + resourceName);
                    if(resourceFile.exists() && resourceFile.isFile()){
                        FileUtil.copyTo(resourceFile, "backup" + File.separator + resourceFile.getName());
                    }
                }
                rollbackButton.setEnabled(true);
                ReplaceThread replaceThread = new ReplaceThread(resourceFolder, this);
                Thread replaceFileThread = new Thread(replaceThread);
                replaceFileThread.start();
            }else{
                JOptionPane.showMessageDialog(null, "<html><body>请选择正确的游戏根目录<br />目录预设名为：<br />最终幻想XIV</body></html>", "路径错误",JOptionPane.ERROR_MESSAGE);
                this.setVisible(false);
                new ConfigApplicationPanel();
            }
        }
        if(e.getSource() == rollbackButton) {
            String path = Config.getProperty("GamePath");
            if(isFFXIVFloder(path)){
                //还原备份文件
                String resourceFolder = path + File.separator + "game" + File.separator + "sqpack" + File.separator + "ffxiv";
                RollbackThread rollbackThread = new RollbackThread(resourceFolder, this);
                Thread rollbackFileThread = new Thread(rollbackThread);
                rollbackFileThread.start();
            }else{
                JOptionPane.showMessageDialog(null, "<html><body>请选择正确的游戏根目录<br />目录预设名为：<br />最终幻想XIV</body></html>", "路径错误",JOptionPane.ERROR_MESSAGE);
                this.setVisible(false);
                new ConfigApplicationPanel();
            }
        }
        if (e.getSource() == configButton) {
            this.setVisible(false);
            new ConfigApplicationPanel();
        }
        if (e.getSource() == toolButton) {
            new ToolPanel();
        }
        if (e.getSource() == closeButton) {
            System.exit(0);
        }
    }

    private boolean isFFXIVFloder(String path){
        if(path == null)
            return false;
        if(new File(path + File.separator + "game" + File.separator + "ffxiv.exe").exists())
            return true;
        return false;
    }

    public void percentShow(double percent){
        int nowLength =(int)(230 * percent);
        percentFrontPanel.setBounds(30, 110, nowLength, 10);
    }

    public static void main(String[] args) throws  Exception {
        TextPatchPanel s = new TextPatchPanel();
    }
}
