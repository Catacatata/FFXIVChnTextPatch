package link.teemo.sqpack.util;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class LERandomBytes{
	private long point;
	private byte[] work;

	public LERandomBytes(byte[] work) throws FileNotFoundException {
		this.work = work;
		this.point = 0;
	}

	public final long getPointer() throws IOException {
		return point;
	}

	public final long length() throws IOException {
		return work.length;
	}
	
	public final byte[] read(int off, int len) throws IOException {
		byte[] tmp = new byte[len];
		System.arraycopy(work, off, tmp, 0, len);
		point += len;
		return tmp;
	}

	public final boolean readBoolean() throws IOException {
		return work[(int)point++]==1 ? true : false;
	}

	public final byte readByte() throws IOException {
		return work[(int)point++];
	}

	public final char readChar() throws IOException {
		byte[] tmp = new byte[]{work[(int)point++], work[(int)point++]};
		return (char) ((tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF);
	}

	public final int readInt() throws IOException {
		byte[] tmp = new byte[]{work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++]};
		return tmp[3] << 24 | (tmp[2] & 0xFF) << 16 | (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF;
	}

	public final int readInt2() throws IOException {
		byte[] tmp = new byte[]{work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++]};
		return tmp[0] << 24 | (tmp[1] & 0xFF) << 16 | (tmp[2] & 0xFF) << 8 | tmp[3] & 0xFF;
	}
	public final long readLong() throws IOException {
		byte[] tmp = new byte[]{work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++], work[(int)point++]};
		return tmp[7] << 56 | (tmp[6] & 0xFF) << 48 | (tmp[5] & 0xFF) << 40
				| (tmp[4] & 0xFF) << 32 | (tmp[3] & 0xFF) << 24 | (tmp[2] & 0xFF) << 16
				| (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF;
	}

	public final short readShort() throws IOException {
		byte[] tmp = new byte[]{work[(int)point++], work[(int)point++]};
		return (short) ((tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF);
	}


	public final int readUnsignedShort() throws IOException {
		byte[] tmp = new byte[]{work[(int)point++], work[(int)point++]};
		return (tmp[1] & 0xFF) << 8 | tmp[0] & 0xFF;
	}

	public final void seek(long pos) throws IOException {
		point = pos;
	}

	public final void skipBytes(int n) throws IOException {
		for(int i=0;i<n;i++){
			point++;
		}
	}

	public final void write(byte[] ba) throws IOException {
		System.arraycopy(ba, 0, work, (int)point, ba.length);
		point += ba.length;
	}

	public final synchronized void write(byte[] ba, int off, int len) throws IOException {
		System.arraycopy(ba, 0, work, off, len);
		point += len;
	}


	public final void writeByte(int v) throws IOException {
		work[(int)point++] = (byte) v;
	}

	public final void writeChar(int v) throws IOException {
		byte[] tmp = new byte[2];
		tmp[0] = ((byte) v);
		tmp[1] = ((byte) (v >> 8));
		System.arraycopy(tmp, 0, work, (int)point, tmp.length);
		point += tmp.length;
	}

	public final void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			writeChar(s.charAt(i));
		}
	}
	
	public final void writeInt(int v) throws IOException {
		byte[] tmp = new byte[4];
		tmp[0] = ((byte) v);
		tmp[1] = ((byte) (v >> 8));
		tmp[2] = ((byte) (v >> 16));
		tmp[3] = ((byte) (v >> 24));
		System.arraycopy(tmp, 0, work, (int)point, tmp.length);
		point += tmp.length;
	}
	
	public final void writeIntBigEndian(int v) throws IOException {
		byte[] tmp = new byte[4];
		tmp[3] = ((byte) v);
		tmp[2] = ((byte) (v >> 8));
		tmp[1] = ((byte) (v >> 16));
		tmp[0] = ((byte) (v >> 24));
		System.arraycopy(tmp, 0, work, (int)point, tmp.length);
		point += tmp.length;
	}

	public final void writeLong(long v) throws IOException {
		byte[] tmp = new byte[8];
		tmp[0] = ((byte) (int) v);
		tmp[1] = ((byte) (int) (v >> 8));
		tmp[2] = ((byte) (int) (v >> 16));
		tmp[3] = ((byte) (int) (v >> 24));
		tmp[4] = ((byte) (int) (v >> 32));
		tmp[5] = ((byte) (int) (v >> 40));
		tmp[6] = ((byte) (int) (v >> 48));
		tmp[7] = ((byte) (int) (v >> 56));
		System.arraycopy(tmp, 0, work, (int)point, tmp.length);
		point += tmp.length;
	}

	public final void writeShort(int v) throws IOException {
		byte[] tmp = new byte[2];
		tmp[0] = ((byte) v);
		tmp[1] = ((byte) (v >> 8));
		System.arraycopy(tmp, 0, work, (int)point, tmp.length);
		point += tmp.length;
	}
	
	public final void writeShortBigEndian(int v) throws IOException {
		byte[] tmp = new byte[2];
		tmp[1] = ((byte) v);
		tmp[0] = ((byte) (v >> 8));
		System.arraycopy(tmp, 0, work, (int)point, tmp.length);
		point += tmp.length;
	}

	public byte[] getWork() {
		return work;
	}

	public void readFully(byte[] b) {
		System.arraycopy(work, (int)point, b, 0, b.length);
		point += b.length;
		
	}
}