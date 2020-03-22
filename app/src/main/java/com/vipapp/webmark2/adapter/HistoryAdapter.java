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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vipapp.webmark2.EditorActivity;
import com.vipapp.webmark2.R;

import java.io.File;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<String> historyList;
    Context context;

    public HistoryAdapter(ArrayList<String> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HistoryAdapter.HistoryViewHolder holder, final int position) {
        holder.mTextName.setText(new File(historyList.get(position)).getName());
        holder.mTextPath.setText(new File(historyList.get(position)).getAbsolutePath());

        ((AppCompatActivity) context).runOnUiThread(() -> {
            if (EditorActivity.class != null) {
                if (EditorActivity.fileUpdateProject.equals(historyList.get(position))) {
                    holder.mLinearActive.setVisibility(View.VISIBLE);
                } else {
                    holder.mLinearActive.setVisibility(View.GONE);
                }
            }
        });

        holder.itemView.setOnClickListener(p1 -> {
            if (EditorActivity.class != null) {
                if (new File(historyList.get(position)).isFile()) {
                    if (historyList.get(position).equals(EditorActivity.fileUpdateProject)) {

                    } else {
                        EditorActivity.setSaveFile(context);
                        EditorActivity.setOpenFile(context, historyList.get(position));
                    }
                }
            }
        });

        holder.mImageClose.setOnClickListener(p1 -> {
            ((AppCompatActivity) context).runOnUiThread(() -> {
                if (EditorActivity.class != null) {
                    if (!EditorActivity.filesHistory.isEmpty()) {
                        if (historyList.get(position).equals(EditorActivity.fileUpdateProject)) {
                            EditorActivity.fileUpdateProject = "";
                            EditorActivity.mCodeEditor.setText("");
                            EditorActivity.mCodeEditor.clearUndoRedo();
                        }
                        EditorActivity.filesHistory.remove(historyList.get(position));
                        EditorActivity.setUpdateHistory(EditorActivity.filesHistory, context);
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        protected AppCompatTextView mTextName;
        protected AppCompatTextView mTextPath;
        protected AppCompatImageView mImageClose;
        protected LinearLayout mLinearActive;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            mTextName = itemView.findViewById(R.id.mTextName);
            mTextPath = itemView.findViewById(R.id.mTextPath);
            mImageClose = itemView.findViewById(R.id.mImageClose);
            mLinearActive = itemView.findViewById(R.id.mLinearActive);
        }
    }
}