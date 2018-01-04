package link.teemo.sqpack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.shenou.fs.core.utils.res.Config;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import link.teemo.sqpack.builder.BinaryBlockBuilder;
import link.teemo.sqpack.builder.EXDFBuilder;
import link.teemo.sqpack.model.*;
import link.teemo.sqpack.util.ArrayUtil;
import link.teemo.sqpack.util.FFCRC;
import link.teemo.sqpack.util.LERandomAccessFile;
import link.teemo.sqpack.util.LERandomBytes;


public class ReplaceEXDF {

	private String pathToIndexSE;
	private String pathToIndexCN;
	private List<String> fileList;
	private Boolean ignoreDiff;

	public ReplaceEXDF(String pathToIndexSE, String pathToIndexCN, List<String> fileList, Boolean ignoreDiff) {
		this.pathToIndexSE = pathToIndexSE;
		this.pathToIndexCN = pathToIndexCN;
		this.fileList = fileList;
		this.ignoreDiff = ignoreDiff;
	}

	public void replaceSource() throws Exception {
		String patternStr = "^[A-Z0-9_]*$";
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
		for (String replaceFile : fileList) {
			if (replaceFile.toUpperCase().endsWith(".EXH")) {
				System.out.println("Now File : " + replaceFile);
				if(replaceFile.startsWith("EXD/cut_scene/041/VoiceMan_04100.EXHsdsds")){
					System.out.println("Stop");
				}
				// 准备好文件目录名和文件名
				String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
				String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1);
				// 计算文件目录CRC
				Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
				// 计算头文件CRC
				Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
				// 解压并且解析头文件
				if(indexSE.get(filePatchCRC) == null || indexCN.get(filePatchCRC) == null) continue;
				SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
				SqPackIndexFile exhIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exhFileCRC);
				if (exhIndexFileSE == null || exhIndexFileCN == null) continue;
				byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
				byte[] exhFileCN = extractFile(pathToIndexCN, exhIndexFileCN.getOffset());
				EXHFFile exhSE = new EXHFFile(exhFileSE);
				EXHFFile exhCN = new EXHFFile(exhFileCN);
				// 添加对照StringDataset
				HashMap<EXDFDataset, EXDFDataset> datasetMap = new HashMap();
				int cnDatasetPossition = 0;
				if(datasetStringCount(exhSE.getDatasets()) > 0 && datasetStringCount(exhSE.getDatasets()) == datasetStringCount(exhCN.getDatasets())) {
					for (EXDFDataset datasetSE : exhSE.getDatasets()) {
						if(datasetSE.type == 0) {
							while(exhCN.getDatasets()[cnDatasetPossition].type != 0){
								cnDatasetPossition ++;
							}
							datasetMap.put(datasetSE, exhCN.getDatasets()[cnDatasetPossition++]);
						}
					}
				}else{
					continue;
				}
				if (exhSE.getLangs().length > 0) {
					// 根据头文件 轮询资源文件
					for (EXDFPage exdfPage : exhSE.getPages()) {
						// 获取资源文件的CRC
						Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD")).toLowerCase().getBytes());
						Integer exdFileCRCCN = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD")).toLowerCase().getBytes());
						// 进行CRC存在校验
						if (indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA) != null && indexCN.get(filePatchCRC).getFiles().get(exdFileCRCCN) != null) {
							System.out.println("Replace File : " + fileName.substring(0, fileName.indexOf(".")));
							if(fileName.contains("VoiceMan_02200")){
								System.out.println("Stop");
							}
							// 提取对应的文本文件
							SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
							SqPackIndexFile exdIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exdFileCRCCN);
							byte[] exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
							byte[] exdFileCN = extractFile(pathToIndexCN, exdIndexFileCN.getOffset());
							// 解压本文文件 提取内容
							EXDFFile ja_exd = new EXDFFile(exdFileJA);
							EXDFFile chs_exd = new EXDFFile(exdFileCN);
							HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
							HashMap<Integer, byte[]> chsExdList = chs_exd.getEntrys();
							// 填充中文内容 如果有需要可以自行修改规则
							for (Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
								Integer listEntryIndex = listEntry.getKey();
								if (chsExdList.get(listEntryIndex) != null) {
									byte[] data = chsExdList.get(listEntryIndex);
									if (ignoreDiff) {
										listEntry.setValue(data);
									} else {
										// 根据头文件 解析资源文件的内容 并且进行替换
										EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
										EXDFEntry exdfEntryCN = new EXDFEntry(data, exhCN.getDatasetChunkSize());
										LERandomBytes chunk = new LERandomBytes(new byte[exdfEntryJA.getChunk().length]);
										chunk.write(exdfEntryJA.getChunk());
										byte[] newString = new byte[0];
										int stringCount = 0;
										for ( EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
											// 只限文本内容
											if (exdfDatasetSE.type == 0) {
												if (pattern.matcher(new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8")).find()
														&& new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8").contains("_")) {
													// 更新Chunk指针
													chunk.seek(exdfDatasetSE.offset);
													chunk.writeIntBigEndian(newString.length);
													// 不动的的部分
													newString = ArrayUtil.append(newString, exdfEntryJA.getString(exdfDatasetSE.offset));
													newString = ArrayUtil.append(newString, new byte[]{0x00});
												} else {
													// 打印内容
//													System.out.println(new String(exdfEntryJA.getString(exdfDataset.offset), "UTF-8"));
//													System.out.println(new String(exdfEntryCN.getString(exdfDataset.offset), "UTF-8"));
//													System.out.println();
													// 更新Chunk指针
													chunk.seek(exdfDatasetSE.offset);
													chunk.writeIntBigEndian(newString.length);
													// 更新文本内容
													String transKey = fileName.substring(0, fileName.indexOf(".")).toLowerCase() + "_" + String.valueOf(listEntryIndex);
													if (Config.getProperty(transKey) != null){
														newString = ArrayUtil.append(newString, Base64.decode(Config.getProperty(transKey).split("|")[stringCount]));
													}else if (exdfEntryCN.getString(datasetMap.get(exdfDatasetSE).offset).length > 0) {
														newString = ArrayUtil.append(newString, exdfEntryCN.getString(datasetMap.get(exdfDatasetSE).offset));
													} else {
														newString = ArrayUtil.append(newString, exdfEntryJA.getString(exdfDatasetSE.offset));
													}
													newString = ArrayUtil.append(newString, new byte[]{0x00});
												}
												stringCount ++;
											}
										}
										stringCount = 0;
										// 打包整个Entry %4 Padding
										byte[] newEntryBody = ArrayUtil.append(chunk.getWork(), newString);
										int paddingSize = 4 - (newEntryBody.length % 4);
										paddingSize = paddingSize == 0 ? 4 : paddingSize;
										LERandomBytes entryBody = new LERandomBytes(new byte[newEntryBody.length + paddingSize]);
										entryBody.write(newEntryBody);
										// 转成byte[] 存入Map
										listEntry.setValue(entryBody.getWork());
									}
								}
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
		}
		leDatFile.close();
		leIndexFile.close();
		System.out.println("Replace Complete");
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
}
