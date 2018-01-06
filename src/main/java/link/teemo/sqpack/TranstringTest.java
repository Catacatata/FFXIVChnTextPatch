package link.teemo.sqpack;

import com.shenou.fs.core.utils.res.Config;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import link.teemo.sqpack.util.ArrayUtil;
import link.teemo.sqpack.util.HexUtils;

import java.io.File;

public class TranstringTest {
    public static void main(String[] args) throws  Exception{
        byte[] br = HexUtils.hexStringToBytes("02100103");
        String line1 = "骑上你的星光熊。";
        String line2 = "它就是为了星芒祭存在。";
        byte[] target = new byte[0];
        target = ArrayUtil.append(target, line1.getBytes("UTF-8"));
        target = ArrayUtil.append(target, br);
        target = ArrayUtil.append(target, line2.getBytes("UTF-8"));
        System.out.println(Base64.encode(target));

        byte[] switchs = HexUtils.hexStringToBytes("02097AE948FF0AE382B3E382ABE38396FF2002081CE905FF0AE382B3E382ABE38396FF0DE3839DE383A9E383AAE382B903FF10E38395E382A7E383ABE382ABE38389FF0AE382B3E382ABE38396FF0DE3839DE383A9E383AAE382B9FF2002081CE905FF0AE382B3E382ABE38396FF0DE3839DE383A9E383AAE382B90303");
        line1 = "星芒祭实行委员会的神父培育出来的熊。名字是，";
        line2 = "。为了让孩子们快乐，学会了投礼物饼干的特技。那特技，经证明可以有效的搜寻偷星芒祭装饰的盗贼。";
        target = new byte[0];
        target = ArrayUtil.append(target, line1.getBytes("UTF-8"));
        target = ArrayUtil.append(target, switchs);
        target = ArrayUtil.append(target, line2.getBytes("UTF-8"));
        System.out.println(Base64.encode(target));

        line1 = "节日狂欢！";
        target = new byte[0];
        target = ArrayUtil.append(target, line1.getBytes("UTF-8"));
        System.out.println(Base64.encode(target));

        Config.setConfigResource("transtable", "conf" + File.separator + "transtable.properties");
        Config.setConfigResource("transtring", "conf" + File.separator + "transtring.properties");
        System.out.println(Config.getProperty("transtring", "カーバンクル・アクアマリン"));
    }
}
