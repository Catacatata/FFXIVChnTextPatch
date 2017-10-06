package link.teemo.sqpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import link.teemo.sqpack.builder.BinaryBlockBuilder;
import link.teemo.sqpack.builder.EXDFBuilder;
import link.teemo.sqpack.model.EXDFFile;
import link.teemo.sqpack.model.SqPackDatFile;
import link.teemo.sqpack.model.SqPackIndex;
import link.teemo.sqpack.model.SqPackIndexFile;
import link.teemo.sqpack.model.SqPackIndexFolder;
import link.teemo.sqpack.util.FFCRC;
import link.teemo.sqpack.util.LERandomAccessFile;


public class ReplaceEXDF {

	private String pathToIndex;
	private String pathToIndexCN;

	public ReplaceEXDF(String pathToIndex, String pathToIndexCN) {
		this.pathToIndex = pathToIndex;
		this.pathToIndexCN = pathToIndexCN;
	}

	public void ReplaceSource() throws Exception {
		System.out.println("Loading Index File...");
		HashMap<Integer, SqPackIndexFolder> index = new SqPackIndex(pathToIndex).resloveIndex();
		HashMap<Integer, SqPackIndexFolder> indexCN = new SqPackIndex(pathToIndexCN).resloveIndex();
		System.out.println("Loading Index Complete");
		LERandomAccessFile leIndexFile = new LERandomAccessFile(pathToIndex, "rw");
		LERandomAccessFile leDatFile = new LERandomAccessFile(pathToIndex.replace("index", "dat0"), "rw");
		long datLength = leDatFile.length();
		leDatFile.seek(datLength);
		for (Entry<Integer, SqPackIndexFolder> indexEntry : index.entrySet()) {
			if (indexEntry.getValue().getName().startsWith("exd")) {
				for (Entry<Integer, SqPackIndexFile> fileEntry : indexEntry.getValue().getFiles().entrySet()) {
					String exdhName = fileEntry.getValue().getName();
					if (exdhName.endsWith(".exh")) {
						Integer crc = FFCRC
								.ComputeCRC((exdhName.replace(".exh", "_0_ja.exd")).toLowerCase().getBytes());
						Integer crcCN = FFCRC
								.ComputeCRC((exdhName.replace(".exh", "_0_chs.exd")).toLowerCase().getBytes());
						if (index.get(indexEntry.getKey()).getFiles().get(crc) != null
								&& indexCN.get(indexEntry.getKey()).getFiles().get(crcCN) != null) {
							System.out.println("Replace : " + exdhName);
							SqPackIndexFile indexFile = index.get(indexEntry.getKey()).getFiles().get(crc);
							SqPackIndexFile indexFileCN = indexCN.get(indexEntry.getKey()).getFiles().get(crcCN);
							byte[] body = extractFile(pathToIndex, indexFile.getOffset());
							byte[] bodyCN = extractFile(pathToIndexCN, indexFileCN.getOffset());
							EXDFFile ja_exd = new EXDFFile(body);
							EXDFFile chs_exd = new EXDFFile(bodyCN);
							HashMap<Integer, byte[]> jaxdList = ja_exd.getEntrys();
							HashMap<Integer, byte[]> chsExdList = chs_exd.getEntrys();
							for (Entry<Integer, byte[]> listEntry : jaxdList.entrySet()) {
								Integer listEntryIndex = listEntry.getKey();
								if (chsExdList.get(listEntryIndex) != null) {
									byte[] data = chsExdList.get(listEntryIndex);
									if(same02Bytes(listEntry.getValue(), data))
										listEntry.setValue(data);
								}
							}
							byte[] exdfFile = new EXDFBuilder(jaxdList).buildExdf();
							byte[] exdfBlock = new BinaryBlockBuilder(exdfFile).buildBlock();
							// write index
							leIndexFile.seek(indexFile.getPt() + 8);
							leIndexFile.writeInt((int) (datLength / 8));
							// write data
							datLength += exdfBlock.length;
							leDatFile.write(exdfBlock);
						}
					}
				}
			}
		}
		leDatFile.close();
		leIndexFile.close();
	}

	private boolean same02Bytes(byte[] value, byte[] data) {
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
	
	public static void main(String arg[]) throws Exception {
		
		File inputFolder = new File("input");
		
		if(inputFolder.isDirectory() && inputFolder.list().length > 0) {
			for(File inputFile : inputFolder.listFiles()) {
				if(inputFile.isFile()) {
					LERandomAccessFile lera = new LERandomAccessFile("input" + File.separator + inputFile.getName(), "r");
					byte[] dist = new byte[(int)lera.length()];
					lera.readFully(dist);
					lera.close();
					FileOutputStream fos = new FileOutputStream(new File("output" + File.separator + inputFile.getName()));
					fos.write(dist);
					fos.flush();
					fos.close();
				}
			}
		}
		
		new ReplaceEXDF("output" + File.separator + "0a0000.win32.index", "resource" + File.separator + "0a0000.win32.index").ReplaceSource();
	}
}
