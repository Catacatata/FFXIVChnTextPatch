package link.teemo.sqpack.util;

public class HexUtils {

	public static String bytesToHexString(byte[] b) {
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[3 * i + 1] = hex[b[i] & 0x0f];
			buff[3 * i + 2] = 45;
		}
		String re = new String(buff);
		return re.replace("-", " ");
	}

	public static String bytesToHexStringWithOutSpace(byte[] b) {
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[3 * i + 1] = hex[b[i] & 0x0f];
			buff[3 * i + 2] = 45;
		}
		String re = new String(buff);
		return re.replace("-", "");
	}

	public static String bytesToHexStringLower(byte[] b) {
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[3 * i + 1] = hex[b[i] & 0x0f];
			buff[3 * i + 2] = 45;
		}
		String re = new String(buff);
		return re.replace("-", "").toLowerCase();
	}

	
	public static String stringToHexString(String str) {
		byte[] b = str.getBytes();
		byte[] buff = new byte[3 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[3 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[3 * i + 1] = hex[b[i] & 0x0f];
			buff[3 * i + 2] = 45;
		}
		String re = new String(buff);
		return re.replace("-", " ");
	}

	public static byte[] hexStringToBytes(String hexstr) {
		hexstr = hexstr.replace(" ", "");
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	// utils
	private final static byte[] hex = "0123456789ABCDEF".getBytes();

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	public static int getHexStringLength(String hexStr) {
		hexStr = hexStr.replace(" ", "");
		if (hexStr.length() < 8) {
			return hexStr.length() + 1;
		}
		String headerStr = hexStr.substring(0, 8);
		return bytesToLength(HexUtils.hexStringToBytes(headerStr), "left");
	}

	// 3位byte[]转int长度
	public static int bytesToLength(byte[] bytes, String level) {
		if (level.equals("left")) {
			int leng = ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8)) | ((bytes[2] & 0xFF) << 16)
			/* | ((bytes[3] & 0xFF)<<24) */;
			return leng;
		} else if (level.equals("right")) {
			int leng = ((bytes[3] & 0xFF) | ((bytes[2] & 0xFF) << 8)) | ((bytes[1] & 0xFF) << 16)
			/* | ((bytes[0] & 0xFF)<<24) */;
			return leng;
		} else {
			return 0;
		}
	}
}
