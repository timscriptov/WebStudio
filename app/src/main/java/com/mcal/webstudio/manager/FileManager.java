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
package com.mcal.webstudio.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcal.webstudio.EditorActivity;
import com.mcal.webstudio.R;
import com.mcal.webstudio.adapter.FileManagerAdapter;
import com.mcal.webstudio.app.Data;

import java.io.File;
import java.util.ArrayList;

public class FileManager {

    public static String MAIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    @SuppressLint("StaticFieldLeak")
    private static AppCompatTextView mTextPatch;
    private static RecyclerView mRecyclerView;

    public static AlertDialog alertDialog;
    public static AlertDialog alertDialogRename;
    public static AlertDialog alertDialogDF;

    private static String managerPath = ""; //Путь папки - обновляется, при обновлении списка
    private static String mainPath = ""; //Запомнить главный путь, с которого открыли файловый менеджер

    //Список всех папок и файлов
    public static ArrayList<String> listFiles(String path) throws Exception {
        ArrayList<String> filesList = new ArrayList<>();
        for (File project : new File(path).listFiles()) {
            filesList.add(project.getAbsolutePath());
        }
        return filesList;
    }

    //Файловый менеджер
    public static void fileManager(final Context context, String path, int type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_file_manager, null);
        dialogBuilder.setView(dialogView);

        AppCompatButton mButtonCancel = dialogView.findViewById(R.id.mButtonCancel);
        AppCompatImageButton mButtonCreate = dialogView.findViewById(R.id.mButtonCreate);
        AppCompatButton mButtonMain = dialogView.findViewById(R.id.mButtonMain);
        mTextPatch = dialogView.findViewById(R.id.mTextPath);
        mRecyclerView = dialogView.findViewById(R.id.mRecyclerView);
        LinearLayout mLinearBack = dialogView.findViewById(R.id.mLinearBack);

        setListLoad(context, path);
        mainPath = path;

        mLinearBack.setOnClickListener(p1 -> {
            //Возвращаемся назад в списке
            if (new File(managerPath).getParentFile() != null) {
                setListLoad(context, new File(managerPath).getParentFile().getAbsolutePath());
            }
        });
        mButtonCancel.setOnClickListener(p1 -> {
            //Закрываем файловый менеджер
            alertDialog.dismiss();
        });
        mButtonCreate.setOnClickListener(p1 -> {
            //Открываем меню с созданием папок/файлов
            fileManagerCreateMenu(context, p1, managerPath);
        });
        mButtonMain.setOnClickListener(p1 -> {
            //Возвращаемся обратно, откуда открыли файловый менеджер
            setListLoad(context, mainPath);
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //Меню файлов/папок в списке
    public static void fileManagerMenu(final Context context, View view, final String path) {
        PopupMenu popupMenu = new PopupMenu(((AppCompatActivity) context), view);
        popupMenu.inflate(R.menu.df_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    if (EditorActivity.fileUpdateProject.equals(path) || EditorActivity.pathUpdateProject.equals(path)) { //Проверяем, чтобы не было совпадений
                        Data.showToast(context, "Please close file/folder with project or opened file");
                    } else {
                        if (DFManager.deleteDF(new File(path))) { //Удаление папки/файла
                            setListLoad(context, managerPath);
                            Data.showToast(context, "Deleted!");
                        } else {
                            Data.showToast(context, "Fail delete...");
                        }
                    }
                    return true;
                case R.id.action_rename:
                    if (EditorActivity.fileUpdateProject.equals(path) || EditorActivity.pathUpdateProject.equals(path)) { //Проверяем, чтобы не было совпадений
                        Data.showToast(context, "Please close file/folder with project or opened file");
                    } else {
                        rename(context, path);
                    }
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    //Список того, что можно создать
    public static void fileManagerCreateMenu(final Context context, View view, final String path) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.create_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_file: //Создаем файл
                    createNewDF(context, path, 0);
                    return true;
                case R.id.action_directory: //Создаем папку
                    createNewDF(context, path, 1);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    //Переименовать файл/папку
    public static void rename(final Context context, final String path) {
        AlertDialog.Builder dialogBuilderRename = new AlertDialog.Builder(context);
        dialogBuilderRename.setTitle(R.string.rename);
        dialogBuilderRename.setMessage(new File(path).getAbsolutePath());
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_rename_df, null);
        dialogBuilderRename.setView(dialogView);

        final AppCompatEditText mEditText = dialogView.findViewById(R.id.mEditName);
        AppCompatButton mButtonOk = dialogView.findViewById(R.id.mButtonOk);
        AppCompatButton mButtonCancel = dialogView.findViewById(R.id.mButtonCancel);
        mEditText.setText(new File(path).getName());

        mButtonOk.setOnClickListener(p1 -> {
            if (mEditText.getText().toString().trim().isEmpty()) {
                mEditText.setError(context.getString(R.string.please_enter_name));
            } else {
                if (DFManager.rename(path, new File(path).getParentFile() + File.separator + mEditText.getText().toString())) {
                    Data.showToast(context, context.getString(R.string.renamed));
                    setListLoad(context, managerPath);
                    alertDialogRename.cancel();
                } else {
                    Data.showToast(context, context.getString(R.string.error_rename));
                }
            }
        });
        mButtonCancel.setOnClickListener(p1 -> {
            alertDialogRename.dismiss();
        });


        alertDialogRename = dialogBuilderRename.create();
        alertDialogRename.show();
    }

    //Создать файл/папку
    public static void createNewDF(final Context context, final String path, final int type) {
        //0 = файл
        //1 = папка
        String title = "New";
        if (type == 0) {
            title = "Create new file";
        }
        if (type == 1) {
            title = "Create new directory";
        }

        AlertDialog.Builder dialogBuilderDF = new AlertDialog.Builder(context);
        dialogBuilderDF.setTitle(title);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_create_df, null);
        dialogBuilderDF.setView(dialogView);

        final AppCompatEditText mEditName = dialogView.findViewById(R.id.mEditName);
        final AppCompatButton mButtonCreate = dialogView.findViewById(R.id.mButtonCreate);

        mButtonCreate.setOnClickListener(p1 -> {
            if (mEditName.getText().toString().trim().isEmpty()) {
                mEditName.setError("Please enter name");
            } else {
                try {
                    if (type == 0) { //Создаем файл
                        if (DFManager.createFile(path + File.separator + mEditName.getText().toString())) {
                            Data.showToast(context, "Created file \"" + mEditName.getText().toString() + "\"");
                        } else {
                            Data.showToast(context, "Error file create!");
                        }
                    }
                    if (type == 1) { //Создаем папку
                        if (DFManager.createDirectory(path + File.separator + mEditName.getText().toString())) {
                            Data.showToast(context, "Created directory \"" + mEditName.getText().toString() + "\"");
                        } else {
                            Data.showToast(context, "Error directory create!");
                        }
                    }
                    setListLoad(context, managerPath);
                    alertDialogDF.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialogDF = dialogBuilderDF.create();
        alertDialogDF.show();
    }

    //Загружаем список всех файлов и папок + небольшие изменения...
    public static void setListLoad(Context context, String path) {
        try {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.Adapter mAdapter = new FileManagerAdapter(listFiles(path), context);
            mRecyclerView.setAdapter(mAdapter);

            //Те самые, небольшие изменения...
            mTextPatch.setText(path); //Обнавляем текст, котрый указывает путь к папке
            managerPath = path; //Обнавляем путь к папке
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}