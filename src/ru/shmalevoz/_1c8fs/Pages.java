/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz._1c8fs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.shmalevoz.utils.Conversion;
import ru.shmalevoz.utils.Log;

/**
 *
 * @author shmalevoz
 */
public class Pages extends Record {
	
	private static final Logger log = Log.getLogger(Pages.class.getName());
	
	private ArrayList<RowLink> _links;
	
	/**
	 * Описывает запись ссылок в данных заголовка страницы
	 */
	private class RowLink {
		
		private int _header_addr;
		private int _data_addr;
		
		public static final int lenght = 12;
		public static final int separator_lenght = 4;
		
		public RowLink(byte[] data, int pos) {
			_header_addr = Conversion.ByteArray2Int(data, pos, true);
			_data_addr = Conversion.ByteArray2Int(data, pos + 4, true);
		}
		
		public int getHeaderAddr() {
			return _header_addr;
		}
		
		public int getDataAddr() {
			return _data_addr;
		}
	}
	
	public Pages(RandomAccessFile data, long pos) throws IOException, IllegalArgumentException {
		super(data, pos, false);
		Init();
	}
	
	public Pages(byte[] data, int pos) throws IllegalArgumentException {
		super(data, pos, false);
		Init();
	}
	
	private void Init() {
		// преобразуем данные в список записей
		int offset = 0;
		byte[] data = getData();
		_links = new ArrayList<RowLink>();
		while (offset < data.length) {
			_links.add(new RowLink(data, offset));
			if (log.isLoggable(Level.CONFIG)) {
				log.config("Read row link. Addresses:\n"
						+ "\tHeader addr " + String.format("0x%08x", _links.get(_links.size() - 1).getHeaderAddr()) + "\n"
						+ "\tData addr " + String.format("0x%08x", _links.get(_links.size() - 1).getDataAddr()));
			}
			offset += RowLink.lenght;
		}
	}
	
	public int getRowsCount() {
		return _links.size();
	}
}
