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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vipapp.webmark2.R;
import com.vipapp.webmark2.manager.DFManager;
import com.vipapp.webmark2.manager.ProjectManager;

import java.io.File;
import java.util.ArrayList;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    public ArrayList<String> projectsList;
    public Context context;

    public ProjectsAdapter(ArrayList<String> projectsList, Context context) {
        this.projectsList = projectsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProjectsAdapter.ProjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_project, parent, false);
        return new ProjectsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ProjectsAdapter.ProjectsViewHolder holder, final int position) {
        holder.name.setText(new File(projectsList.get(position)).getName());
        holder.type.setText("html");

        holder.itemView.setOnClickListener(p1 -> {
            try {
                ProjectManager.openProject(context, DFManager.checkIndexType(new File(projectsList.get(position)).getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        holder.itemView.setOnLongClickListener(p1 -> {
            ProjectManager.menuProject(context, p1, projectsList.get(position));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder {

        protected AppCompatTextView name;
        protected AppCompatTextView type;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mTextName);
            type = itemView.findViewById(R.id.mTextType);
        }
    }
}