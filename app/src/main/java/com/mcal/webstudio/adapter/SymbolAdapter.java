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
package com.mcal.webstudio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mcal.webstudio.EditorActivity;
import com.mcal.webstudio.R;

import java.util.ArrayList;

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.SymbolViewHolder> {

    ArrayList<String> symbolList;
    Context context;

    public SymbolAdapter(ArrayList<String> symbolList, Context context) {
        this.symbolList = symbolList;
        this.context = context;
    }

    @NonNull
    @Override
    public SymbolAdapter.SymbolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_symbol, parent, false);
        return new SymbolViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SymbolAdapter.SymbolViewHolder holder, final int position) {
        holder.text.setText(symbolList.get(position).toString());

        holder.itemView.setOnClickListener(p1 -> {
            if (position == 0) {
                EditorActivity.mCodeEditor.insertTab();
            } else {
                EditorActivity.mCodeEditor.addTextCursorPosition(symbolList.get(position).toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return symbolList.size();
    }

    public static class SymbolViewHolder extends RecyclerView.ViewHolder {

        protected AppCompatTextView text;

        public SymbolViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.mTextSymbol);
        }
    }
}