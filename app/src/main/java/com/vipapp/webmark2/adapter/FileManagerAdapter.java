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
package com.vipapp.webmark2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vipapp.webmark2.EditorActivity;
import com.vipapp.webmark2.R;
import com.vipapp.webmark2.app.Data;
import com.vipapp.webmark2.manager.FileManager;
import com.vipapp.webmark2.manager.ProjectManager;

import java.io.File;
import java.util.ArrayList;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.FileManagerViewHolder> {

    ArrayList<String> fileManagerList;
    Context context;

    public FileManagerAdapter(ArrayList<String> fileManagerList, Context context) {
        this.fileManagerList = fileManagerList;
        this.context = context;
    }

    @NonNull
    @Override
    public FileManagerAdapter.FileManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_file_manager, parent, false);
        return new FileManagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FileManagerAdapter.FileManagerViewHolder holder, final int position) {
        holder.name.setText(new File(fileManagerList.get(position)).getName());

        if (new File(fileManagerList.get(position)).isDirectory()) { //Если = папке
            holder.image.setImageResource(R.drawable.baseline_folder_24);
        }
        if (new File(fileManagerList.get(position)).isFile()) { //Если = файлу
            holder.image.setImageResource(R.drawable.baseline_insert_drive_file_24);
        }

        holder.itemView.setOnClickListener(p1 -> {
            if (new File(fileManagerList.get(position)).isDirectory()) { //Если = папке
                FileManager.setListLoad(context, new File(fileManagerList.get(position)).getAbsolutePath());
            }
            if (new File(fileManagerList.get(position)).isFile()) { //Если = файл
                try { //Пытаемся открыть файл
                    if (EditorActivity.runActivity) { //Если редактор открыт
                        EditorActivity.setOpenFile(context, fileManagerList.get(position));
                    } else { //Если редактор закрыт, то открываем его...
                        ProjectManager.openFile(context, fileManagerList.get(position));
                        if (FileManager.alertDialog.isShowing()) { //Закрываем файловый менеджер, если он открыт
                            FileManager.alertDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    Data.showToast(context, context.getString(R.string.error_opening_file) + e.getMessage());
                }
            }
        });

        holder.itemView.setOnLongClickListener(p1 -> { //Открываем меню
            FileManager.fileManagerMenu(context, p1, fileManagerList.get(position));
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return fileManagerList.size();
    }

    public static class FileManagerViewHolder extends RecyclerView.ViewHolder {

        protected AppCompatTextView name;
        protected AppCompatImageView image;

        public FileManagerViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mTextName);
            image = itemView.findViewById(R.id.mImage);
        }
    }
}