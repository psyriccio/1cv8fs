/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz._1c8fs;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author shmalevoz
 */
public class Row {
	
	private Record _header;
	private Record _data;
	
	public Row(RandomAccessFile data, long header_addr, long data_addr) throws IOException, IllegalArgumentException {
		_header = new Record(data, header_addr);
		_data = new Record(data, data_addr);
	}
	
	public Row(byte[] data, int header_addr, int data_addr) throws IllegalArgumentException {
		_header = new Record(data, header_addr);
		_data = new Record(data, data_addr);
	}
}
