package link.teemo.sqpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.shenou.fs.core.utils.res.Config;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import link.teemo.sqpack.builder.BinaryBlockBuilder;
import link.teemo.sqpack.builder.EXDFBuilder;
import link.teemo.sqpack.model.*;
import link.teemo.sqpack.swing.TextPathPanel;
import link.teemo.sqpack.util.ArrayUtil;
import link.teemo.sqpack.util.FFCRC;
import link.teemo.sqpack.util.LERandomAccessFile;
import link.teemo.sqpack.util.LERandomBytes;


public class ReplaceEXDF {

	private String pathToIndexSE;
	private String pathToIndexCN;
	private List<String> fileList;
	private Boolean ignoreDiff;
	private TextPathPanel textPathPanel;
	private boolean cnResourceAvailable;
	private boolean cnEXHFileAvailable;
	private boolean cnEXDFileAvailable;
	private boolean cnEntryAvailable;

	public ReplaceEXDF(String pathToIndexSE, String pathToIndexCN, List<String> fileList, Boolean ignoreDiff, TextPathPanel textPathPanel) {
		this.pathToIndexSE = pathToIndexSE;
		this.pathToIndexCN = pathToIndexCN;
		this.fileList = fileList;
		this.ignoreDiff = ignoreDiff;
		this.textPathPanel = textPathPanel;
		this.cnResourceAvailable = true;
	}

	public void replaceSource() throws Exception {
		String patternStr = "^[a-zA-Z0-9_]*$";
		Pattern pattern = Pattern.compile(patternStr);
		System.out.println("Loading Index File...");
		HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
		HashMap<Integer, SqPackIndexFolder> indexCN = new SqPackIndex(pathToIndexCN).resloveIndex();
		System.out.println("Loading Index Complete");
		LERandomAccessFile leIndexFile  = new LERandomAccessFile(pathToIndexSE, "rw");
		LERandomAccessFile leDatFile    = new LERandomAccessFile(pathToIndexSE.replace("index", "dat0"), "rw");
		long datLength = leDatFile.length();
		leDatFile.seek(datLength);
		// 根据传入的文件进行遍历
        int fileCount = 0;
		for (String replaceFile : fileList) {
            textPathPanel.percentShow((double)fileCount++ / (double)fileList.size());
			if (replaceFile.toUpperCase().endsWith(".EXH")) {
				System.out.println("Now File : " + replaceFile);
				// 准备好文件目录名和文件名
				String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
				String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1);
				// 计算文件目录CRC
				Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
				// 计算头文件CRC
				Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
				// 解压并且解析头文件
				if(indexSE.get(filePatchCRC) == null) continue;
				SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
				if (exhIndexFileSE == null) continue;
				byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
				EXHFFile exhSE = new EXHFFile(exhFileSE);
				EXHFFile exhCN = null;
				HashMap<EXDFDataset, EXDFDataset> datasetMap = new HashMap();
				cnEXHFileAvailable = true;
				if(cnResourceAvailable) {
					try {
						SqPackIndexFile exhIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exhFileCRC);
						byte[] exhFileCN = extractFile(pathToIndexCN, exhIndexFileCN.getOffset());
						exhCN = new EXHFFile(exhFileCN);
						// 添加对照StringDataset
						int cnDatasetPossition = 0;
						if (datasetStringCount(exhSE.getDatasets()) > 0 && datasetStringCount(exhSE.getDatasets()) == datasetStringCount(exhCN.getDatasets())) {
							for (EXDFDataset datasetSE : exhSE.getDatasets()) {
								if (datasetSE.type == 0) {
									while (exhCN.getDatasets()[cnDatasetPossition].type != 0) {
										cnDatasetPossition++;
									}
									datasetMap.put(datasetSE, exhCN.getDatasets()[cnDatasetPossition++]);
								}
							}
						}
					} catch (Exception cnEXHFileException) {
						cnEXHFileAvailable = false;
					}
				}
				if (exhSE.getLangs().length > 0) {
					// 根据头文件 轮询资源文件
					for (EXDFPage exdfPage : exhSE.getPages()) {
						// 获取资源文件的CRC
						Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD")).toLowerCase().getBytes());
						// 进行CRC存在校验
						System.out.println("Replace File : " + fileName.substring(0, fileName.indexOf(".")));
						// 提取对应的文本文件
						SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
						byte[] exdFileJA = null;
						try {
							exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
						}catch (Exception jaEXDFileException){
							continue;
						}
						// 解压本文文件 提取内容
						EXDFFile ja_exd = new EXDFFile(exdFileJA);
						HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
						HashMap<Integer, byte[]> chsExdList = null;
						cnEXDFileAvailable = true;
						if (cnEXHFileAvailable){
							try{
								Integer exdFileCRCCN = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD")).toLowerCase().getBytes());
								SqPackIndexFile exdIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exdFileCRCCN);
								byte[] exdFileCN = extractFile(pathToIndexCN, exdIndexFileCN.getOffset());
								EXDFFile chs_exd = new EXDFFile(exdFileCN);
								chsExdList = chs_exd.getEntrys();
							}catch (Exception cnEXDFileException){
								cnEXDFileAvailable = false;
							}
						}
						// 填充中文内容 如果有需要可以自行修改规则
						for (Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
							Integer listEntryIndex = listEntry.getKey();
							EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
							EXDFEntry exdfEntryCN = null;
							cnEntryAvailable = true;
							if (cnEXDFileAvailable){
								try{
									byte[] data = chsExdList.get(listEntryIndex);
									exdfEntryCN = new EXDFEntry(data, exhCN.getDatasetChunkSize());
									if (ignoreDiff) {
										listEntry.setValue(data);
										continue;
									}
								}catch (Exception cnEntryNullException){
									cnEntryAvailable = false;
								}
							}
							LERandomBytes chunk = new LERandomBytes(new byte[exdfEntryJA.getChunk().length]);
							chunk.write(exdfEntryJA.getChunk());
							byte[] newFFXIVString = new byte[0];
							int stringCount = 0;
							for ( EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
								// 只限文本内容
								if (exdfDatasetSE.type == 0) {
									byte[] jaBytes = exdfEntryJA.getString(exdfDatasetSE.offset);
									String jaStr = new String(jaBytes, "UTF-8");
									if ( (pattern.matcher(jaStr).find() && jaStr.contains("_"))
											|| (jaBytes.length > 4 && jaBytes[0] == 0x02 && (jaBytes[1] == 0x28 || jaBytes[1] == 0x40) && ((int)jaBytes[2] + 3 == jaBytes.length)) ) {
										// 更新Chunk指针
										chunk.seek(exdfDatasetSE.offset);
										chunk.writeIntBigEndian(newFFXIVString.length);
										// 不动的的部分
										newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
										newFFXIVString = ArrayUtil.append(newFFXIVString, new byte[]{0x00});
									} else {
										// 打印内容
										// 更新Chunk指针
										chunk.seek(exdfDatasetSE.offset);
										chunk.writeIntBigEndian(newFFXIVString.length);
										// 更新文本内容
										String transKey = replaceFile.substring(0, replaceFile.lastIndexOf(".")).toLowerCase() + "_" + String.valueOf(listEntryIndex) + "_" + String.valueOf(stringCount + 1);
										if (Config.getConfigResource("transtable") != null && Config.getProperty("transtable", transKey) != null){
											newFFXIVString = ArrayUtil.append(newFFXIVString, Base64.decode(Config.getProperty("transtable", transKey)));
										}else if (Config.getConfigResource("transtring") != null && Config.getProperty("transtring", jaStr) != null){
											newFFXIVString = ArrayUtil.append(newFFXIVString, Config.getProperty("transtring", jaStr).getBytes("UTF-8"));
										} else {
											if (cnEXHFileAvailable && cnEXDFileAvailable && cnEntryAvailable && exdfEntryCN.getString(datasetMap.get(exdfDatasetSE).offset).length > 0){
												byte[] chBytes = exdfEntryCN.getString(datasetMap.get(exdfDatasetSE).offset);
												newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(chBytes));
											}else {
												newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
											}
										}
										newFFXIVString = ArrayUtil.append(newFFXIVString, new byte[]{0x00});
									}
									stringCount ++;
								}
							}
							// 打包整个Entry %4 Padding
							byte[] newEntryBody = ArrayUtil.append(chunk.getWork(), newFFXIVString);
							int paddingSize = 4 - (newEntryBody.length % 4);
							paddingSize = paddingSize == 0 ? 4 : paddingSize;
							LERandomBytes entryBody = new LERandomBytes(new byte[newEntryBody.length + paddingSize]);
							entryBody.write(newEntryBody);
							// 转成byte[] 存入Map
							listEntry.setValue(entryBody.getWork());
						}
						// 准备好修改好的内容
						byte[] exdfFile = new EXDFBuilder(jaExdList).buildExdf();
						byte[] exdfBlock = new BinaryBlockBuilder(exdfFile).buildBlock();
						// 填充修改后的内容到新文件
						leIndexFile.seek(exdIndexFileJA.getPt() + 8);
						leIndexFile.writeInt((int) (datLength / 8));
						datLength += exdfBlock.length;
						leDatFile.write(exdfBlock);
					}
				}
			}
		}
		leDatFile.close();
		leIndexFile.close();
		System.out.println("Replace Complete");
	}

	private static byte[] convertString(byte[] chBytes){


		// 00 54 51 51 02 01 41 03 45 04
//		if(new String(chBytes).equals("200")){
//			System.out.println(Config.getProperty("Language"));
//		}

		if(Config.getProperty("Language").equals("繁體中文") || Config.getProperty("Language").equals("正體中文")) {
			try {
				byte[] newFFXIVStringBytes = new byte[0];

				byte[] chBytesMirror = new byte[chBytes.length];

				ByteBuffer buffIn = ByteBuffer.wrap(chBytes);
				buffIn.order(ByteOrder.LITTLE_ENDIAN);

				ByteBuffer buffOut = ByteBuffer.wrap(chBytesMirror);
				buffIn.order(ByteOrder.LITTLE_ENDIAN);

				while (buffIn.hasRemaining()) {
					byte b = buffIn.get();
					if (b == 2) {
						byte[] bytes = new byte[buffOut.position()];
						buffOut.position(0);
						buffOut.get(bytes);
						buffOut.clear();
						newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, ChineseHelper.convertToTraditionalChinese(new String(bytes, "UTF-8")).getBytes("UTF-8"));
						newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, processPacket(buffIn));
					} else {
						buffOut.put(b);
					}
				}
				if (buffOut.position() != 0) {
					byte[] bytes = new byte[buffOut.position()];
					buffOut.position(0);
					buffOut.get(bytes);
					buffOut.clear();
					newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, ChineseHelper.convertToTraditionalChinese(new String(bytes, "UTF-8")).getBytes("UTF-8"));
				}


				return newFFXIVStringBytes;
			}catch (Exception e){}

		}
		return chBytes;
	}

	private boolean same02Byte(byte[] value, byte[] data) {
		int vcount = 0 ,dcount = 0;
		for(int i = 0 ; i < value.length ; i ++) {
			if( value[i] == 2) {
				vcount ++;
			}
		}
		for(int i = 0 ; i < data.length ; i ++) {
			if( data[i] == 2) {
				dcount ++;
			}
		}
		return vcount == dcount;
	}

	private byte[] extractFile(String pathToIndex, long dataOffset) throws IOException, FileNotFoundException {
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
	private int datasetStringCount(EXDFDataset[] datasets){
		int count = 0;
		for(EXDFDataset dataset : datasets){
			if(dataset.type == 0){
				count ++;
			}
		}
		return count;
	}
	private static byte[] processPacket(ByteBuffer buffIn){
		byte[] header = new byte[3];
		int possistion = 0;
		header[possistion++] = 0x02;
		header[possistion++] = buffIn.get();
		header[possistion++] = buffIn.get();
		int payloadSize = header[2] & 0xFF;
		if (payloadSize <= 1){
			return header;
		}
		payloadSize = getPayloadSize(payloadSize, buffIn);
		byte[] payload = new byte[payloadSize];
		buffIn.get(payload);
		return ArrayUtil.append(header, payload);
	}
	private static int getPayloadSize(int payloadSize, ByteBuffer buffIn)
	{
		if (payloadSize < 240) {
			return payloadSize;
		}
		switch (payloadSize)
		{
			case 240:
				int valByte = buffIn.get() & 0xFF;
				return valByte;
			case 241:
				//BUG
//				return 0;
			case 242:
				return buffIn.getShort();
			case 250:
				int val24 = 0;
				val24 |= buffIn.get() << 16;
				val24 |= buffIn.get() << 8;
				val24 |= buffIn.get();
				return val24;
			case 254:
				return buffIn.getInt();
		}

		return payloadSize;
	}

	public static void main(String[] args) throws Exception{
		System.out.println(Base64.encode("星光熊".getBytes("UTF-8")));
	}
}
