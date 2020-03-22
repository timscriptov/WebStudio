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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;

import com.vipapp.webmark2.EditorActivity;
import com.vipapp.webmark2.MainActivity;
import com.vipapp.webmark2.R;
import com.vipapp.webmark2.app.Data;
import com.vipapp.webmark2.manager.appmark.ExportProject;

import java.io.File;
import java.util.ArrayList;

public class ProjectManager {

    public static String ALSO_HTML = "index.html";
    public static String ALSO_CSS = "styles.css";
    public static String ALSO_JS = "script.js";

    public static String ALSO_CSS_DIR = "css";
    public static String ALSO_JS_DIR = "js";

    private static AlertDialog alertDialog;

    //Список всех проектов
    public static ArrayList<String> listProjects() throws Exception {
        ArrayList<String> projectsList = new ArrayList<String>();
        for (File project : new File(DFManager.MAIN_PROJECTS).listFiles()) {
            if (project.isDirectory()) {
                projectsList.add(project.getAbsolutePath());
            }
        }
        return projectsList;
    }

    //Создание нового проекта
    public static void createProject(final Context context, final int type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        dialogBuilder.setTitle(R.string.create_project);
        View dialogView = inflater.inflate(R.layout.alert_create_project, null);
        dialogBuilder.setView(dialogView);
        final AppCompatEditText mEditName = dialogView.findViewById(R.id.mEditName);
        final AppCompatButton mButtonCreate = dialogView.findViewById(R.id.mButtonCreate);

        mButtonCreate.setOnClickListener(p1 -> {
            if (mEditName.getText().toString().trim().isEmpty()) {
                mEditName.setError("Please enter name project");
            } else {
                try {
                    if (DFManager.createDirectory(DFManager.MAIN_PROJECTS + File.separator + mEditName.getText().toString())) {
                        //Так-же создаем index.html для будущего сайта
                        DFManager.createFile(DFManager.MAIN_PROJECTS + File.separator + mEditName.getText().toString() + File.separator + ALSO_HTML);

                        //+ Создаем css
                        if (DFManager.createDirectory(DFManager.MAIN_PROJECTS + File.separator + mEditName.getText().toString() + File.separator + ALSO_JS_DIR)) {
                            DFManager.createFile(DFManager.MAIN_PROJECTS + File.separator + mEditName.getText().toString() + File.separator + ALSO_JS_DIR + File.separator + ALSO_JS);
                        }

                        //+ Создаем js
                        if (DFManager.createDirectory(DFManager.MAIN_PROJECTS + File.separator + mEditName.getText().toString() + File.separator + ALSO_CSS_DIR)) {
                            DFManager.createFile(DFManager.MAIN_PROJECTS + File.separator + mEditName.getText().toString() + File.separator + ALSO_CSS_DIR + File.separator + ALSO_CSS);
                        }

                        //Проект успешно создан, после всех процедур + обновляем список проектов, если это необходимо
                        Data.showToast(context, "Project \"" + mEditName.getText().toString() + "\" created!");
                        alertDialog.dismiss();
                        if (type == 0) {
                            MainActivity.setLoadListProjects(context);
                        }
                    } else {
                        //Что-то пошло не так, в процессе создания проекта
                        Data.showToast(context, "Fail create project");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //Меню для проектов
    public static void menuProject(final Context context, View view, final String path) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.project_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete: //Удаление проекта
                    if (new File(path).isDirectory()) { //Если = папке
                        deleteProject(context, path);
                    }
                    return true;
                case R.id.action_export_to_am: //Экспорт веб проекта, в AppMark
                    if (new File(path).isDirectory()) { //Если = папке
                        ExportProject.exportProject(context, path);
                    }
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    //Открытие проекта из любой папки
    public static void openProject(Context context, String file) throws Exception {
        if (!DFManager.checkIndexType(new File(file).getParentFile() + "").equals("")) {
            context.startActivity(new Intent(context, EditorActivity.class).putExtra("filePath", file));
        } else {
            Data.showToast(context, "Error opening project");
        }
        //Data.showToast(context, DFManager.checkIndexType(new File(file).getParentFile().getAbsolutePath()));
    }

    //Открытие готового файла, без проекта
    public static void openFile(Context context, String file) throws Exception {
        if (new File(file).exists() && new File(file).isFile()) {
            context.startActivity(new Intent(context, EditorActivity.class).putExtra("filePath", file));
        } else {
            Data.showToast(context, "Error opening file");
        }
    }

    //Удаление проекта
    public static void deleteProject(final Context context, final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete)
                .setMessage("Are you sure you want to delete this project \"" + new File(path).getName() + "\"?")
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    if (DFManager.deleteDF(new File(path))) {
                        if (MainActivity.class != null) {
                            MainActivity.setLoadListProjects(context);
                            Data.showToast(context, "Deleted!");
                        }
                    } else {
                        Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}