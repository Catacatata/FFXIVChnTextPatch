package link.teemo.sqpack.swing;

import com.shenou.fs.core.utils.res.Config;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import link.teemo.sqpack.EXDFUtil;
import link.teemo.sqpack.entity.EXDStringLocate;
import link.teemo.sqpack.model.SqPackDatFile;
import link.teemo.sqpack.model.SqPackIndex;
import link.teemo.sqpack.model.SqPackIndexFile;
import link.teemo.sqpack.model.SqPackIndexFolder;
import link.teemo.sqpack.util.FFCRC;
import link.teemo.sqpack.util.FFXIVString;
import link.teemo.sqpack.util.HexUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ToolPanel extends JFrame implements ActionListener {

    private static Point origin = new Point();
    private static String title = "提莫苑|汉化工具";

    private JLabel title_lable = new JLabel(title);
    private Dimension dimension;

    private JButton closeButton = new JButton("x");
    private JPanel titlePanel = new JPanel();
    private JPanel bodyPanel = new JPanel();

    private JComboBox<String> strTransOption = new JComboBox<String>();
    private JTextField strTransInsert = new JTextField();
    private JButton strTransButton = new JButton("转换");

    private JComboBox<String> strSearchOption = new JComboBox<String>();
    private JTextField strSearchInsert = new JTextField();
    private JButton strSearchButton = new JButton("搜索");

    private JComboBox<String> fileExplorerOption = new JComboBox<String>();
    private JComboBox<String> fileExplorerFileName = new JComboBox<String>();
    private JButton fileExplorerButton = new JButton("浏览");

    private JTextArea outputTextArea = new JTextArea(5,10);
    private JScrollPane outputTextJScrollPane = new JScrollPane(outputTextArea);
    private JButton outputTextCopyButton = new JButton("复制");

    private String pathToIndex = null;
    private java.util.List<String> fileList = new ArrayList<String>();

    public ToolPanel(){
        super(title);
        setUndecorated(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setIconImage(toolkit.createImage(HexUtils.hexStringToBytes("89504E470D0A1A0A0000000D4948445200000040000000400806000000AA6971DE000004EC4944415478DAED5B69485451141EB50DB3C56AA821A329C7596CD459A2FA57467B513F0209F147CB0F2B82C20ACA928ACA3F2544605909051651D962641B914542942456602B6DE492AD56A29665DF9137F27ACD7BB3F4EECCBB830F0EF7CD9B33EFDEEFDC73CE3DF79C3B3A5D082EB7DB3D283535D5EE703816B85CAE5CA7D3791C7407F74FF1DD1BB4F5681BD0D6815E821EE1FB4AB487D1AE453B2B2525C58A77F4D7F174198DC67E003D07200A00A20AD40EA09D81127EF71BEF20C194A05D6AB3D90C5AC6DD0B839C8FC19E007D0F06B01F02F905BA857EB2939393E334811A031982016DC6C0DEB2002D47E8F32BFA2C84798C090B70D8663C06918F417C535261B27750332B41E0DD3F4007ED76FBA850618F06F015E8FC83C2C07E92C3831F28C1FD975068044D04FA5B47A6C80C39A46C232FAD34E3684FA5A5A5CD236F1F4A9310994635CCC2A23A78805B056A5300FF009D4F062D24FB0C0778B1366012B254016E32990602D04985CEDAA17A5B70DF1B7C79821674869B842574DB7F81872A25E045F7153A790E1E1758A3D0D93E2D00F76212FB697C0183870A8DA3684DE1C56514E9D1CBA10107B4085E34D68280C163769B141C1DBD309A786929D432780F619256FAABF6168ACFE5C0E345EB3DBC00BF4C2B36EF874F68C378C72B82B75AAD43C9AE159C4A8E484B9C78DECA037811861AD93881BC38186E2AA8D00E116F2C789FF104DEA72928D932BE3B26F6A4F8BC9B47F002BD008498BFC0432A53F045875C6445DB5B11AF438E97235398290E74FAE2C11319E616D8BA5912115EE619BC4005E2D9CF5590D41AA9A6F0E2F57D68C0450FA0C1F8F05986B15A6A2B78561E01B34F02B8D195AFC3CD2605C674C9EC275146264204709A6C792A25226518AE7B592576450278C1B1E791000AE518E0F86648F047853AE5C5729748A13E09A05686E9B174F7841F4C8894D907EE339EE5EC938C7A6CF0A2FE791102BEBE3BB5EE2D9949EAE12DCB8AE7D722007C1D1569C4B3FAD10BE37B3C9F6DB7DB1345D15F14CBAC6E889C5E39300D97CE6A8D2F67016A9111142FF490CA72729B9F9C48716CD2F09D7298A0698AA9302A6C60862F45C2B246C517002EA28C744085548BC532003FBAC01160AA02D5824A29130D9AFB8F6D0773619DCF10CAD2E1DCE874600C8D68A9BE705528916FA74228804E27C74C491BA6352F746444C74B4045E8AC02ED6B7FD35E42E5B6992246A1CE7F97964F9A2DDC1753124528A25281258BCAE76827510E12CFF59E24AB262F2A4593AA2188180DB089341B142F4073469ACDE6617ABD3E2EA8DC7BCFD573755F06832196340C9A3591D66E98C57298C8D6AE8D0BAF179DEF0198C594592230A03D007604748E9212E4F0845D66AB42167723B70250A36A04019DE75900875488E31BB9150066AF4C8D1801ABD0082E0580C1DF5343005E3254DC6840934A51E26AEEC053F0A456680D3FB097C725D0AD66428347FBCF54B9ACCD9DFDEF545100EF788C01AEA8B955E66EB3E5E32469C044C7F578527F93DA0913DA7AF3A4FED96A0B206CA7C283D4805206677B9278C11FC3A29680B8622C17E885F3469D0C4C208117F52F642100AA63F010FFF7517BF9F3649C75D2A36D1AF5FE8B18D50C1A7851FFDB8CCA5F559A078F40259D61FDEF2C0FA16F25C3FA7EBED6673F83F101E74C2D273EE285FF00331300937F81A9E8F98B19D7FF9B349DF7A39A3D63011CD5B2F9D31FA62B581C9F174E7EBCA2E3F92C06FE07D4147657975EC1240000000049454E44AE426082")));
        dimension=toolkit.getScreenSize();
        setBounds((dimension.width-640)/2, (dimension.height-360)/2, 640, 360);
        setVisible(true);
        setResizable(false);
        setLayout(null);
        //标题栏
        titlePanel.setBounds(0, 0, 640, 30);
        titlePanel.setBackground(new Color(110,110,110));
        titlePanel.setBorder(new MatteBorder(0, 0, 0, 0, new Color(110,110,110)));
        add(titlePanel);
        bodyPanel.setBounds(0, 0, 640, 360);
        bodyPanel.setBackground(new Color(255,255,255));
        bodyPanel.setBorder(new MatteBorder(0, 1, 1, 1, new Color(110,110,110)));
        add(bodyPanel);
        title_lable.setBounds(10, 0, 150, 30);
        title_lable.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        title_lable.setForeground(new Color(255,255,255));
        add(title_lable,0);
        closeButton.setBounds(610, 0, 20, 30);
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
        strTransOption.addItem("String");
        strTransOption.addItem("HexString");
        strTransOption.setBounds(30, 50, 100, 23);
        strTransOption.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        strTransOption.setForeground(new Color(110,110,110));
        strTransOption.setOpaque(false);
        strTransOption.setFocusable(false);
        add(strTransOption,0);
        strTransInsert.setBounds(130, 50, 420, 25);
        strTransInsert.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        strTransInsert.setForeground(new Color(110,110,110));
        add(strTransInsert,0);
        strTransButton.setBounds(549, 50, 50, 24);
        strTransButton.setFont(new Font("Microsoft Yahei",Font.PLAIN,13));
        strTransButton.setForeground(new Color(110,110,110));
        strTransButton.setMargin(new Insets(0, 0, 0, 0));
        strTransButton.setOpaque(false);
        strTransButton.setIconTextGap(0);
        strTransButton.setContentAreaFilled(false);
        strTransButton.setFocusable(false);
        strTransButton.addActionListener(this);
        add(strTransButton,0);

        strSearchOption.addItem("String");
        strSearchOption.addItem("HexString");
        strSearchOption.setBounds(30, 80, 100, 23);
        strSearchOption.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        strSearchOption.setForeground(new Color(110,110,110));
        strSearchOption.setOpaque(false);
        strSearchOption.setFocusable(false);
        add(strSearchOption,0);
        strSearchInsert.setBounds(130, 80, 420, 25);
        strSearchInsert.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        strSearchInsert.setForeground(new Color(110,110,110));
        add(strSearchInsert,0);
        strSearchButton.setBounds(549, 80, 50, 24);
        strSearchButton.setFont(new Font("Microsoft Yahei",Font.PLAIN,13));
        strSearchButton.setForeground(new Color(110,110,110));
        strSearchButton.setMargin(new Insets(0, 0, 0, 0));
        strSearchButton.setOpaque(false);
        strSearchButton.setIconTextGap(0);
        strSearchButton.setContentAreaFilled(false);
        strSearchButton.setFocusable(false);
        strSearchButton.addActionListener(this);
        add(strSearchButton,0);

        fileExplorerOption.addItem("String");
        fileExplorerOption.addItem("FFXIVString");
        fileExplorerOption.addItem("HexString");
        fileExplorerOption.setBounds(30, 110, 100, 23);
        fileExplorerOption.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        fileExplorerOption.setForeground(new Color(110,110,110));
        fileExplorerOption.setOpaque(false);
        fileExplorerOption.setFocusable(false);
        add(fileExplorerOption,0);
        fileExplorerFileName.setBounds(130, 110, 420, 23);
        fileExplorerFileName.setFont(new Font("Microsoft Yahei",Font.BOLD,13));
        fileExplorerFileName.setForeground(new Color(110,110,110));
        fileExplorerFileName.setOpaque(false);
        fileExplorerFileName.setFocusable(false);
        add(fileExplorerFileName,0);
        fileExplorerButton.setBounds(549, 110, 50, 24);
        fileExplorerButton.setFont(new Font("Microsoft Yahei",Font.PLAIN,13));
        fileExplorerButton.setForeground(new Color(110,110,110));
        fileExplorerButton.setMargin(new Insets(0, 0, 0, 0));
        fileExplorerButton.setOpaque(false);
        fileExplorerButton.setIconTextGap(0);
        fileExplorerButton.setContentAreaFilled(false);
        fileExplorerButton.setFocusable(false);
        fileExplorerButton.addActionListener(this);
        add(fileExplorerButton,0);

        try {
            String path = Config.getProperty("GamePath");
            if (isFFXIVFloder(path)) {
                String resourceFolder = path + File.separator + "game" + File.separator + "sqpack" + File.separator + "ffxiv";
                pathToIndex = resourceFolder + File.separator + "0a0000.win32.index";
                HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndex).resloveIndex();
                Integer filePathCRC = FFCRC.ComputeCRC("exd".toLowerCase().getBytes("UTF-8"));
                Integer rootFileCRC = FFCRC.ComputeCRC("root.exl".toLowerCase().getBytes("UTF-8"));
                SqPackIndexFile rootIndexFileSE = indexSE.get(filePathCRC).getFiles().get(rootFileCRC);
                byte[] rootFile = extractFile(pathToIndex, rootIndexFileSE.getOffset());
                BufferedReader rootBufferReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(rootFile)));
                String line = null;
                while((line = rootBufferReader.readLine())!=null) {
                    String fileName = ("EXD/" + line.split(",")[0]);
                    fileExplorerFileName.addItem(fileName.toLowerCase());
                    fileList.add(fileName);
                }
            }
        }catch (Exception e){
        }

        outputTextArea.setBounds(30, 140, 570, 180);
        outputTextArea.setFont(new Font("Microsoft Yahei",Font.PLAIN,13));
        outputTextArea.setBackground(Color.white);
        outputTextArea.setBorder(new MatteBorder(1, 1, 1, 1, new Color(110,110,110)));
        outputTextArea.setEditable(false);
        outputTextJScrollPane.setBounds(30, 140, 570, 180);
        outputTextJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputTextJScrollPane.setVisible(true);
        add(outputTextJScrollPane,0);
        outputTextCopyButton.setBounds(30, 319, 570, 20);
        outputTextCopyButton.setFont(new Font("Microsoft Yahei",Font.PLAIN,13));
        outputTextCopyButton.setForeground(new Color(110,110,110));
        outputTextCopyButton.setMargin(new Insets(0, 0, 0, 0));
        outputTextCopyButton.setOpaque(false);
        outputTextCopyButton.setIconTextGap(0);
        outputTextCopyButton.setContentAreaFilled(false);
        outputTextCopyButton.setFocusable(false);
        outputTextCopyButton.addActionListener(this);
        add(outputTextCopyButton,0);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == closeButton) {
            Config.reloadConfig();
            this.setVisible(false);
        }
        if (actionEvent.getSource() == strTransButton) {
            if (strTransInsert.getText().length() == 0){
                outputTextArea.setText("无传入转换内容");
            }else{
                switch((String)strTransOption.getSelectedItem()){
                    case "String":
                        try {
                            outputTextArea.setText("");
                            outputTextArea.append("String:\t" + strTransInsert.getText() + "\n");
                            outputTextArea.append("FFXIVString:\t" + strTransInsert.getText() + "\n");
                            outputTextArea.append("HexString:\t" + HexUtils.bytesToHexStringWithOutSpace(strTransInsert.getText().getBytes("UTF-8")) + "\n");
                            outputTextArea.append("Base64:\t" + Base64.encode(strTransInsert.getText().getBytes("UTF-8")).replace("\n","") + "\n");
                        }catch (Exception e){
                            outputTextArea.setText("系统异常");
                        }
                        break;
                    case "HexString":
                        try {
                            outputTextArea.setText("");
                            outputTextArea.append("String:\t" + new String(HexUtils.hexStringToBytes(strTransInsert.getText()),"UTF-8") + "\n");
                            outputTextArea.append("FFXIVString:\t" + FFXIVString.parseFFXIVString((HexUtils.hexStringToBytes(strTransInsert.getText()))) + "\n");
                            outputTextArea.append("HexString:\t" + strTransInsert.getText() + "\n");
                            outputTextArea.append("Base64:\t" + Base64.encode(HexUtils.hexStringToBytes(strTransInsert.getText())).replace("\n","") + "\n");
                        }catch (Exception e){
                            outputTextArea.setText("系统异常");
                        }
                        break;
                    default:
                        outputTextArea.setText("未知参数");
                }
            }

        }
        if (actionEvent.getSource() == strSearchButton) {
            if (strSearchInsert.getText().length() == 0){
                outputTextArea.setText("无传入搜索内容");
            }else try {
                java.util.List<EXDStringLocate> locates = new EXDFUtil(pathToIndex, fileList).search(((String)strSearchOption.getSelectedItem()), strSearchInsert.getText());
                if(locates != null && locates.size() == 0){
                    outputTextArea.setText("无符合内容的结果");
                }else{
                    outputTextArea.setText("");
                    outputTextArea.append("搜索关键字 " + strSearchInsert.getText() + "\n类型 " + (String)strSearchOption.getSelectedItem() + "\n");
                    outputTextArea.append("搜索到符合的结果 " + locates.size() + "条\n\n");
                    outputTextArea.append("文件|目录数|字符列|内容|字符串\n");
                    for(EXDStringLocate locate: locates){
                        outputTextArea.append(locate.getFileName().toLowerCase() + "|" +
                                locate.getIndex() + "|" +
                                locate.getStrCount() + "|" +
                                HexUtils.bytesToHexStringWithOutSpace(locate.getStrBody()) + "|" +
                                locate.getFFXIVString() + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                outputTextArea.setText("系统异常");
            }
        }
        if (actionEvent.getSource() == fileExplorerButton) {
            try {
                java.util.List<String> locates = new EXDFUtil(pathToIndex, fileList).explorer(((String)fileExplorerOption.getSelectedItem()), ((String)fileExplorerFileName.getSelectedItem()));
                if(locates != null && locates.size() == 0){
                    outputTextArea.setText("无内容");
                }else{
                    outputTextArea.setText("");
                    for(String locate: locates){
                        outputTextArea.append( locate + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                outputTextArea.setText("系统异常");
            }
        }
        if (actionEvent.getSource() == outputTextCopyButton) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(outputTextArea.getText());
            clip.setContents(tText, null);
        }
    }

    private boolean isFFXIVFloder(String path){
        if(path == null)
            return false;
        if(new File(path + File.separator + "game" + File.separator + "ffxiv.exe").exists())
            return true;
        return false;
    }

    private byte[] extractFile(String pathToIndex, long dataOffset) throws IOException {
        String pathToOpen = pathToIndex;
        int datNum = (int) ((dataOffset & 0xF) / 2L);
        dataOffset -= (dataOffset & 0xF);
        pathToOpen = pathToOpen.replace("index2", "dat" + datNum);
        pathToOpen = pathToOpen.replace("index", "dat" + datNum);
        SqPackDatFile datFile = new SqPackDatFile(pathToOpen);
        byte[] data = datFile.extractFile(dataOffset * 8L, false);
        datFile.close();
        return data;
    }

    public static void main(String[] args) throws  Exception {
        ToolPanel s = new ToolPanel();
    }
}
