/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz._1c8fs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.shmalevoz.utils.Conversion;
import ru.shmalevoz.utils.Log;

/**
 *
 * @author shmalevoz
 */
public class Record {
	
	private static final Logger log = Log.getLogger(Record.class.getName());
	
	private int _addr;
	private boolean _data_compressed;
	private Header _header;
	private byte[] _data;
	
	private static final boolean _default_compressed = false;
	
	/**
	 * Заголовок записи. Должен иметь вид
	 * 0x0D 0x0A 
	 * DWORD размер значимых данных в записи строкой / размер всех страниц
	 * 0x20
	 * DWORD размер данных в записи строкой / размер текущей страницы
	 * 0x20
	 * DWORD адрес следующей записи этого типа. Если слшедующей нет - 0x7FFFFFFF
	 * 0x20
	 * 0x0D 0x0A
	 * Сразу за заголовком следуют данные
	 */
	private class Header {
		
		public static final int lenght = 31;
		
		private int _data_size;
		private int _record_size;
		private int _next_addr;
		private int _data_addr;
		
		public Header(byte[] data, int pos, int addr) throws IllegalArgumentException {
			// проверим длину доступного буфера
			if (data.length < lenght) throw new IllegalArgumentException("Illegal header buffer size at addr " + Integer.toHexString(addr));
			if (data[0] != 0x0D || data[1] != 0x0A || data[10] != 0x20 || data[19] != 0x20 || data[28] != 0x20 || 
					data[29] != 0x0D || data[30] != 0x0A) throw new IllegalArgumentException("Error record header format at " + Integer.toHexString(addr));
			// читаем данные
			_data_size = Conversion.String2Int(Arrays.copyOfRange(data, 2, 10), Conversion.HEX);
			_record_size = Conversion.String2Int(Arrays.copyOfRange(data, 11, 19), Conversion.HEX);
			_next_addr = Conversion.String2Int(Arrays.copyOfRange(data, 20, 28), Conversion.HEX);
			if (log.isLoggable(Level.CONFIG)) {
				log.config("Read header at " + String.format("0x%08x", addr) + "\n"
						+ "\tdata size " + String.format("0x%08x", _data_size) + "\n"
						+ "\trecord size " + String.format("0x%08x", _record_size) +"\n"
						+ "\tnext header offset " + String.format("0x%08x", _next_addr)
						);
			}
			_data_addr = pos + lenght;
		}
		
		public int getDataSize() {
			return Math.min(_data_size, _record_size);
		}
		
		public int getDataAddr() {
			return _data_addr;
		}
		
		public boolean hasNext() {
			return !Utils.isValidSign(_next_addr);
		}
		
		public int getNextAddr() {
			return _next_addr;
		}
	}
	
	public Record(RandomAccessFile data, long pos) throws IOException, IllegalArgumentException {
		this(data, pos, _default_compressed);
	}
	
	public Record(RandomAccessFile data, long pos, boolean compressed) throws IOException, IllegalArgumentException {
		// предварительно читаем в массив заголовок, и по полученным 
		// из него данным о размере формируем массив данных
		byte[] buf = new byte[Header.lenght];
		_addr = (int) pos;
		data.seek(pos);
		data.read(buf, 0, buf.length);
		_header = new Header(buf, 0, _addr);
		// читаем сами данные. сведения в заголовке
		buf = new byte[Header.lenght + _header.getDataSize()];
		if (log.isLoggable(Level.CONFIG)) {
			log.config("Read record with data at " + String.format("%08x", _addr) + " size " + String.format("%08x", buf.length));
		}
		data.seek(pos);
		data.read(buf, 0, buf.length);
		Init(buf, 0, compressed, true);
	}
	
	public Record(byte[] data, int pos) throws IllegalArgumentException {
		this(data, pos, _default_compressed);
	}
	
	public Record(byte[] data, int pos, boolean compressed) throws IllegalArgumentException {
		_addr = pos;
		Init(data, pos, compressed, false);
	}
	
	private void Init(byte[] data, int pos, boolean compressed, boolean checked) throws IllegalArgumentException {
		if (!checked) _header = new Header(data, pos, pos);
		if (data.length < Header.lenght + _header.getDataSize()) throw new IllegalArgumentException("Illegal data lenght at addr " + Integer.toHexString(pos));
		_data_compressed = compressed;
		_data = new byte[_header.getDataSize()];
		log.config("Remember data with size " + String.format("0x%08x", _data.length));
		_data = Arrays.copyOfRange(data, pos + Header.lenght, pos + Header.lenght + _header.getDataSize());
	}
	
	public byte[] getData() {
		return _data;
	}
	
	public int getAddr() {
		return _addr;
	}
	
	public int getNextRecordAddr() {
		return _header.getNextAddr();
	}
	
	public boolean hasNext() {
		return _header.hasNext();
	}
	
	public Record getNext(RandomAccessFile data) throws IOException, IllegalArgumentException {
		return new Record(data, getNextRecordAddr(), _data_compressed);
	}
	
	public Record getNext(byte[] data) throws IllegalArgumentException {
		return new Record(data, getNextRecordAddr(), _data_compressed);
	}
}
