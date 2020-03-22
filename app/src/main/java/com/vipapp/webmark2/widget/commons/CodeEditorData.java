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
package com.vipapp.webmark2.widget.commons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.vipapp.webmark2.R;
import com.vipapp.webmark2.widget.CodeEditor;

public class CodeEditorData {

    private static AlertDialog alertDialogReplace;

    public static void replaceText(Context context, final CodeEditor editor) {
        AlertDialog.Builder dialogBuilderReplace = new AlertDialog.Builder(context, R.style.Theme_MaterialComponents_DayNight_Dialog_Alert);
        dialogBuilderReplace.setTitle(R.string.replace);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_replace, null);
        dialogBuilderReplace.setView(dialogView);

        final AppCompatEditText mEditWith = dialogView.findViewById(R.id.mEditWith);
        final AppCompatEditText mEditTo = dialogView.findViewById(R.id.mEditTo);
        AppCompatButton mButtonReplace = dialogView.findViewById(R.id.mButtonReplace);
        AppCompatButton mButtonCancel = dialogView.findViewById(R.id.mButtonCancel);

        mButtonReplace.setOnClickListener(p1 -> {
            if (mEditWith.getText().toString().trim().isEmpty()) {
                mEditWith.setError(context.getString(R.string.please_enter_value));
            } else {
                editor.setReplaceText(mEditWith.getText().toString(), mEditTo.getText().toString());
                alertDialogReplace.dismiss();
            }
        });
        mButtonCancel.setOnClickListener(p1 -> {
            alertDialogReplace.cancel();
        });

        alertDialogReplace = dialogBuilderReplace.create();
        alertDialogReplace.show();
    }
}
