//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package link.teemo.sqpack.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class FFXIVString {
    static final int START_BYTE = 2;
    static final int END_BYTE = 3;
    static final int TYPE_NONE = 0;
    static final int TYPE_TIME = 7;
    static final int TYPE_IF = 8;
    static final int TYPE_SWITCH = 9;
    static final int TYPE_NEWLINE = 16;
    static final int TYPE_ICON1 = 18;
    static final int TYPE_COLOR_CHANGE = 19;
    static final int TYPE_ITALICS = 26;
    static final int TYPE_INDENT = 29;
    static final int TYPE_ICON2 = 30;
    static final int TYPE_DASH = 31;
    static final int TYPE_SERVER_VALUE0 = 32;
    static final int TYPE_SERVER_VALUE1 = 33;
    static final int TYPE_SERVER_VALUE2 = 34;
    static final int TYPE_SERVER_VALUE3 = 36;
    static final int TYPE_SERVER_VALUE4 = 37;
    static final int TYPE_PLAYERLINK = 39;
    static final int TYPE_REFERENCE = 40;
    static final int TYPE_INFO = 41;
    static final int TYPE_LINK = 43;
    static final int TYPE_SPLIT = 44;
    static final int TYPE_REFERENCE_JA = 48;
    static final int TYPE_REFERENCE_EN = 49;
    static final int TYPE_REFERENCE_DE = 50;
    static final int TYPE_REFERENCE_FR = 51;
    static final int TYPE_REFERENCE2 = 64;
    static final int TYPE_ITEM_LOOKUP = 49;
    static final int INFO_NAME = 235;
    static final int INFO_GENDER = 233;
    static final int SIZE_DATATYPE_BYTE = 240;
    static final int SIZE_DATATYPE_BYTE256 = 241;
    static final int SIZE_DATATYPE_INT16 = 242;
    static final int SIZE_DATATYPE_INT24 = 250;
    static final int SIZE_DATATYPE_INT32 = 254;
    static final int DECODE_BYTE = 240;
    static final int DECODE_INT16_MINUS1 = 241;
    static final int DECODE_INT16_1 = 242;
    static final int DECODE_INT16_2 = 244;
    static final int DECODE_INT24_MINUS1 = 245;
    static final int DECODE_INT24 = 246;
    static final int DECODE_INT24_1 = 250;
    static final int DECODE_INT24_2 = 253;
    static final int DECODE_INT32 = 254;
    static final int DECODE_VARIABLE = 255;
    static final int COMPARISON_GE = 224;
    static final int COMPARISON_UN = 225;
    static final int COMPARISON_LE = 226;
    static final int COMPARISON_NEQ = 227;
    static final int COMPARISON_EQ = 228;
    static final int INFO_INTEGER = 232;
    static final int INFO_PLAYER = 233;
    static final int INFO_STRING = 234;
    static final int INFO_OBJECT = 235;
    static final byte[] forenamePayload = new byte[]{-1, 7, 2, 41, 3, -21, 2, 3, -1, 2, 32, 2, 3};
    static final byte[] surnamePayload = new byte[]{-1, 7, 2, 41, 3, -21, 2, 3, -1, 2, 32, 3, 3};

    public FFXIVString() {
    }

    public static String parseFFXIVString(byte[] stringBytes) {
        try {
            byte[] newStringBytes = new byte[stringBytes.length * 4];
            ByteBuffer buffIn = ByteBuffer.wrap(stringBytes);
            buffIn.order(ByteOrder.LITTLE_ENDIAN);
            ByteBuffer buffOut = ByteBuffer.wrap(newStringBytes);
            buffIn.order(ByteOrder.LITTLE_ENDIAN);

            while(buffIn.hasRemaining()) {
                byte b = buffIn.get();
                if (b == 2) {
                    try {
                        processPacket(buffIn, buffOut);
                    } catch (UnsupportedEncodingException var6) {
                        var6.printStackTrace();
                    }
                } else {
                    buffOut.put(b);
                }
            }

            try {
                return new String(newStringBytes, 0, buffOut.position(), "UTF-8");
            } catch (UnsupportedEncodingException var8) {
                var8.printStackTrace();
            }
        } catch (Exception var9) {
            try {
                return "<ERROR Parsing: " + new String(stringBytes, "UTF-8") + ">";
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
            }
        }

        return "ERROR";
    }

    private static void processPacket(ByteBuffer buffIn, ByteBuffer buffOut) throws UnsupportedEncodingException {
        int type = buffIn.get() & 255;
        int payloadSize = buffIn.get() & 255;
        if (payloadSize <= 1) {
            switch(type) {
            case 16:
                buffOut.put("<br>".getBytes("UTF-8"));
            default:
            }
        } else {
            payloadSize = getPayloadSize(payloadSize, buffIn);
            byte[] payload = new byte[payloadSize];
            buffIn.get(payload);
            byte[] exdName;
            switch(type) {
            case -34:
                byte[] opt1 = new byte[payload[4] - 1];
                byte[] opt2 = new byte[payload[4 + payload[4]] - 1];
                System.arraycopy(payload, 4, opt1, 0, opt1.length);
                System.arraycopy(payload, 5 + payload[3], opt2, 0, opt2.length);
                buffOut.put("<".getBytes("UTF-8"));
                ByteBuffer optionPayload;
                if (opt1[0] == 2) {
                    optionPayload = ByteBuffer.wrap(opt1);
                    optionPayload.get();
                    processPacket(optionPayload, buffOut);
                } else {
                    buffOut.put(opt1);
                }

                buffOut.put("/".getBytes("UTF-8"));
                if (opt2[0] == 2) {
                    optionPayload = ByteBuffer.wrap(opt1);
                    optionPayload.get();
                    processPacket(optionPayload, buffOut);
                } else {
                    buffOut.put(opt2);
                }

                buffOut.put(">".getBytes("UTF-8"));
                break;
            case -33:
                for(int i = 0; i < payload.length; ++i) {
                    System.out.print(String.format("0x%x ", payload[i]));
                }

                System.out.print("\n");
            case 7:
            case 39:
                break;
            case 8:
                ByteBuffer payloadBB = ByteBuffer.wrap(payload);
                payloadBB.order(ByteOrder.LITTLE_ENDIAN);
                StringBuilder builder = new StringBuilder();
                builder.append("<if");
                builder.append("(");
                processCondition(payloadBB, builder);
                builder.append(") {");
                decode(payloadBB, builder);
                builder.append("} else {");
                decode(payloadBB, builder);
                builder.append("}>");
                buffOut.put(builder.toString().getBytes("UTF-8"));
                break;
            case 9:
                int pos2 = 1;
                String switchString2 = "<switch:";
                if (payload[0] != -35 && payload[0] != -24) {
                    if (payload[0] == -37) {
                        switchString2 = switchString2 + "?";
                    }
                } else {
                    if (payload[0] == -24) {
                        ++pos2;
                    }

                    while(true) {
                        ++pos2;
                        int stringSize = payload[pos2];
                        ++pos2;
                        if (stringSize - 1 != 0) {
                            byte[] switchBuffer = new byte[stringSize - 1];
                            System.arraycopy(payload, pos2, switchBuffer, 0, stringSize - 1);
                            if (switchBuffer[0] == 2) {
                                ByteBuffer switchBB = ByteBuffer.wrap(switchBuffer);
                                switchBB.position(1);
                                byte[] outProcessBuffer = new byte[512];
                                ByteBuffer outProcessBB = ByteBuffer.wrap(outProcessBuffer);
                                processPacket(switchBB, outProcessBB);
                                switchString2 = switchString2 + new String(outProcessBuffer, 0, outProcessBB.position(), "UTF-8");
                            } else {
                                switchString2 = switchString2 + new String(switchBuffer, "UTF-8");
                            }
                        }

                        pos2 += stringSize - 1;
                        if (payload[pos2] == 3) {
                            break;
                        }

                        switchString2 = switchString2 + "/";
                    }
                }

                buffOut.put((switchString2 + ">").getBytes("UTF-8"));
                break;
            case 16:
                buffOut.put("\\n".getBytes("UTF-8"));
                break;
            case 18:
            case 30:
                buffOut.put(String.format("<icon:%d>", payload[0]).getBytes("UTF-8"));
                break;
            case 19:
                if (payload[0] == -20) {
                    buffOut.put("</color>".getBytes("UTF-8"));
                } else if (payload[0] == -2) {
                    buffOut.put(String.format("<color #%02X%02X%02X>", payload[2], payload[3], payload[4]).getBytes("UTF-8"));
                } else {
                    buffOut.put("<color?>".getBytes("UTF-8"));
                }
                break;
            case 26:
                if (payload[0] == 2) {
                    buffOut.put("<i>".getBytes("UTF-8"));
                } else {
                    buffOut.put("</i>".getBytes("UTF-8"));
                }
                break;
            case 31:
                buffOut.put("-".getBytes("UTF-8"));
                break;
            case 32:
            case 34:
            case 36:
                buffOut.put("<value>".getBytes("UTF-8"));
                break;
            case 40:
                exdName = new byte[payload[1] - 1];
                System.arraycopy(payload, 2, exdName, 0, exdName.length);
                buffOut.put(String.format("<ref:%s>", new String(exdName)).getBytes("UTF-8"));
                break;
            case 41:
                if ((payload[0] & 255) == 235 && (payload[1] & 255) == 2) {
                    buffOut.put("<forename surname>".getBytes("UTF-8"));
                } else {
                    buffOut.put("<value>".getBytes("UTF-8"));
                }
                break;
            case 43:
                buffOut.put(String.format("<2b?:0x%x, 0x%x, 0x%x>", payload[0], payload[1], payload[2]).getBytes("UTF-8"));
                break;
            case 44:
                if (Arrays.equals(payload, forenamePayload)) {
                    buffOut.put("<forename>".getBytes("UTF-8"));
                } else if (Arrays.equals(payload, surnamePayload)) {
                    buffOut.put("<surname>".getBytes("UTF-8"));
                } else {
                    buffOut.put("<split:".getBytes("UTF-8"));
                    int contentsSize = payload[1];
                    buffOut.put(String.format("[%d]", payload[1 + contentsSize + 1]).getBytes("UTF-8"));
                    byte[] splitBuffer = new byte[contentsSize];
                    System.arraycopy(payload, 2, splitBuffer, 0, splitBuffer.length - 2);
                    ByteBuffer splitBB = ByteBuffer.wrap(splitBuffer);
                    splitBB.position(1);
                    byte[] outSplitProcessBuffer = new byte[512];
                    ByteBuffer outSplitProcessBB = ByteBuffer.wrap(outSplitProcessBuffer);
                    outSplitProcessBuffer = parseFFXIVString(splitBuffer).getBytes();
                    buffOut.put((new String(outSplitProcessBuffer, 0, outSplitProcessBuffer.length, "UTF-8")).getBytes("UTF-8"));
                    buffOut.put(">".getBytes("UTF-8"));
                }
                break;
            case 49:
                buffOut.put("<item>".getBytes("UTF-8"));
                break;
            case 64:
                exdName = new byte[payload[6] - 1];
                System.arraycopy(payload, 7, exdName, 0, exdName.length);
                buffOut.put(String.format("<ref:%s>", new String(exdName)).getBytes("UTF-8"));
                break;
            default:
                String unknownMsg = String.format("<?0x%x>", type);
                buffOut.put(unknownMsg.getBytes("UTF-8"));
            }

        }
    }

    private static void processCondition(ByteBuffer buffIn, StringBuilder builder) {
        int code = buffIn.get() & 255;
        String compareStr;
        switch(code) {
        case 224:
            compareStr = ">=";
            break;
        case 225:
            compareStr = "?";
            break;
        case 226:
            compareStr = "<=";
            break;
        case 227:
        default:
            decode(buffIn, builder);
            return;
        case 228:
            compareStr = "==";
        }

        decode(buffIn, builder);
        builder.append(compareStr);
        decode(buffIn, builder);
    }

    private static void decode(ByteBuffer buffIn, StringBuilder builder) {
        int code = buffIn.get() & 255;
        if (code < 208) {
            builder.append(" " + code + " ");
        } else if (code < 224) {
            builder.append(" " + code + " ");
        } else {
            switch(code) {
            case 232:
                builder.append("INT:");
                decode(buffIn, builder);
                return;
            case 233:
                builder.append("PLYR:");
                decode(buffIn, builder);
                return;
            case 234:
                builder.append("STR:");
                decode(buffIn, builder);
                return;
            case 235:
                builder.append("OBJ:");
                decode(buffIn, builder);
                return;
            case 255:
                int size = buffIn.get() & 255;
                size = getPayloadSize(size, buffIn) - 1;
                byte[] data = new byte[size];
                buffIn.get(data);
                builder.append(parseFFXIVString(data));
            default:
            }
        }
    }

    private static void getParam(ByteBuffer buffIn, StringBuilder builder) {
    }

    private static int getPayloadSize(int payloadSize, ByteBuffer buffIn) {
        if (payloadSize < 240) {
            return payloadSize;
        } else {
            int val24;
            switch(payloadSize) {
            case 240:
                val24 = buffIn.get() & 255;
                return val24;
            case 241:
            case 242:
                return buffIn.getShort();
            case 250:
                val24 = 0;
                val24 = val24 | buffIn.get() << 16;
                val24 |= buffIn.get() << 8;
                val24 |= buffIn.get();
                return val24;
            case 254:
                return buffIn.getInt();
            default:
                return payloadSize;
            }
        }
    }
}
