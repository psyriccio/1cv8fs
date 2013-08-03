/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz._1c8fs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import ru.shmalevoz.utils.Log;

/**
 * Описывает заголовок файла конфигурации 1с 8.x (*.cf)
 *
 * @author shmalevoz
 */
public class Image {
	
	private static final Logger log = Log.getLogger(Image.class.getName());

	private static final int lenght = 4 + 4 + 8;
	private static final int reserved_lenght = 8;
	
	private int page_size;
	private int next_pos;
	private byte[] reserved;
	private boolean isValid;

	/**
	 * Конструктор
	 *
	 * @param data поток данных
	 * @throws IOException при ошибках ввода/вывода
	 * @throws IllegalArgumentException при неверном формате файла
	 */
	public Image(RandomAccessFile data) throws IOException, IllegalArgumentException {
		this(data, data.getFilePointer());
	}

	/**
	 * Конструктор
	 *
	 * @param data поток данных
	 * @param pos начальная позиция в потоке
	 * @throws IOException при ошибках ввода/вывода
	 * @throws IllegalArgumentException при неверном формате файла
	 */
	public Image(RandomAccessFile data, long pos) throws IOException, IllegalArgumentException {
		data.seek(pos);
		byte[] buf = new byte[lenght];
		data.read(buf, 0, lenght);
		read(buf, 0);
	}

	/**
	 * Конструктор
	 *
	 * @param data массив байт данных
	 * @param pos начальная позиция в массиве
	 * @throws IllegalArgumentException при неверном формате файла
	 */
	public Image(byte[] data, int pos) throws IllegalArgumentException {
		read(data, pos);
	}

	/**
	 * Считывает заголовок
	 *
	 * @param data массив данных
	 * @param pos начальная позиция в массиве
	 * @throws IllegalArgumentException при неверном формате
	 */
	private void read(byte[] data, int pos) throws IllegalArgumentException {
		// Проверим корректность заголовка
		if (pos + lenght > data.length) {
			throw new ArrayIndexOutOfBoundsException("Ошибка чтения заголовка: индекс за пределами массива");
		}
		isValid = Utils.isValidSign(ru.shmalevoz.utils.Conversion.ByteArray2Int(data, pos, true));
		if (!isValid) {
			throw new IllegalArgumentException("Чтение заголовка образа: неверный идентификатор");
		}
		// Считываем данные
		next_pos = pos + lenght;
		page_size = ru.shmalevoz.utils.Conversion.ByteArray2Int(data, pos + 4, true);
		reserved = new byte[reserved_lenght];
		for (int i = 0; i < reserved_lenght; i++) {
			reserved[i] = data[pos + i + lenght - reserved_lenght];
		}
		log.config("Чтение заголовка файла:\n"
				+ "\tЗаголовок " + (!isValid ? "не" : "") + "корректен\n"
				+ "\tСмещение следующих данных " + next_pos + "\n"
				+ "\tРазмер страницы " + page_size);
	}
	
	/**
	 * Возвращает признак корректности заголовка
	 * @return признак корректности заголовка
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * Возвращает размер страницы по-умолчанию
	 * @return размер страницы по-умолчанию
	 */
	public int getPageSize() {
		return page_size;
	}
	
	/**
	 * Возвращает позицию начала данных заголовка страницы
	 * @return позиция начала данных заголовка страницы
	 */
	public int getNextPosition() {
		return next_pos;
	}
}
