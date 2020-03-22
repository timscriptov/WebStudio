/*
 * Copyright (C) 2020 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.vipapp.webmark2.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.vipapp.webmark2.R;
import com.vipapp.webmark2.app.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class DFManager {

    public static String MAIN = Environment.getExternalStorageDirectory() + File.separator + "WebMark";
    public static String MAIN_PROJECTS = Environment.getExternalStorageDirectory() + File.separator + "WebMark" + File.separator + "projects";
    public static String MAIN_NOMEDIA = Environment.getExternalStorageDirectory() + File.separator + "WebMark" + File.separator + ".nomedia";
    public static String INDEX_HTML = "index.html";

    //Создаем папку
    public static boolean createDirectory(String path) throws Exception {
        File file = new File(path);
        return file.mkdir();
    }

    //Создаем файл
    public static boolean createFile(String path) throws Exception {
        File file = new File(path);
        return file.createNewFile();
    }

    //Удаляем папку/файл
    public static boolean deleteDF(File path) {
        if (path.isDirectory()) {
            String[] children = path.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDF(new File(path, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return path.delete();
    }

    //Проверка - какой тип главного файла
    public static String checkIndexType(String path) {
        String type = "";
        if (new File(path + File.separator + INDEX_HTML).exists()) {
            type = path + File.separator + INDEX_HTML;
        }
        return type;
    }

    //Проверка типа файла
    public static String getMimeType(File file) {
        String type = "text/*";
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (!extension.equals("")) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type.equals("")) {
            type = "text/*";
        }
        return type;
    }

    //Получаем размер папки/файла
    public static long sizeDF(File path) {
        long length = 0;
        for (File file : path.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += sizeDF(file);
        }
        return length;
    }

    //Вычисление размера папки/файла
    @SuppressLint("DefaultLocale")
    public static String sizeFormat(long bytes) {
        String s = bytes < 0 ? "-" : "";
        long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        return b < 1000L ? bytes + "B"
                : b < 999_950L ? String.format("%s%.1f" + "KB", s, b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "MB", s, b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "GB", s, b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "TB", s, b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "PB", s, b / 1e3)
                : String.format("%s%.1f" + "EB", s, b / 1e6);
    }

    //Переименование файла/папки
    public static boolean rename(String oldPath, String newPath) {
        boolean success = false;
        File dir = Environment.getExternalStorageDirectory();
        if (dir.exists()) {
            File file = new File(oldPath);
            File newFile = new File(newPath);
            if (file.exists())
                success = file.renameTo(newFile);
        }
        return success;
    }

    //Запись текста в файл
    public static void writeFile(Context context, String text, String path) {
        if (new File(path).exists()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                byte[] buffer = text.getBytes();
                fos.write(buffer, 0, buffer.length);
                fos.close();
                Data.showToast(context, context.getString(R.string.saved));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    Data.showToast(context, context.getString(R.string.error_write_file) + e.getMessage());
                }
            }
        } else {
            Data.showToast(context, context.getString(R.string.error_saved_not_file));
        }
    }

    //Чтение текста из файла
    public static String readFile(Context context, String path) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Data.showToast(context, context.getString(R.string.error_read_file) + e.getMessage());
        }
        return text.toString();
    }
}