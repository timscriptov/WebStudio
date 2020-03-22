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
package com.mcal.webstudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcal.webstudio.adapter.HistoryAdapter;
import com.mcal.webstudio.adapter.SymbolAdapter;
import com.mcal.webstudio.adapter.SyntaxCSSAdapter;
import com.mcal.webstudio.adapter.SyntaxHTMLAdapter;
import com.mcal.webstudio.adapter.SyntaxJSAdapter;
import com.mcal.webstudio.app.Data;
import com.mcal.webstudio.language.LangSyntax;
import com.mcal.webstudio.language.LanguageCSS;
import com.mcal.webstudio.language.LanguageHTML;
import com.mcal.webstudio.language.LanguageJS;
import com.mcal.webstudio.language.SyntaxUtils;
import com.mcal.webstudio.manager.DFManager;
import com.mcal.webstudio.manager.FileManager;
import com.mcal.webstudio.tokenizer.TokenizerCSS;
import com.mcal.webstudio.tokenizer.TokenizerHTML;
import com.mcal.webstudio.tokenizer.TokenizerJS;
import com.mcal.webstudio.widget.CodeEditor;
import com.mcal.webstudio.widget.commons.CodeEditorData;

import java.io.File;
import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {

    private static RecyclerView mRecyclerViewSymbol;
    private static RecyclerView mRecyclerHistory;

    public static CodeEditor mCodeEditor;

    public static String fileProject = "";
    public static String fileUpdateProject = "";
    public static String pathProject = "";
    public static String pathUpdateProject = "";

    public static ArrayList<String> filesHistory = new ArrayList<>();
    public static Handler handlerCode;

    public static boolean runActivity = false; //Проверям открыт ли этот класс

    private static ProgressDialog progressLoad;
    private static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data.themeApp(this);
        setContentView(R.layout.editor);
        mCodeEditor = findViewById(R.id.mCodeEditor);
        mRecyclerViewSymbol = findViewById(R.id.mRecyclerViewSymbol);
        LinearLayout mLinearNavigationHistory = findViewById(R.id.mLinearNavigationHistory);
        AppCompatButton mButtonClearAll = mLinearNavigationHistory.findViewById(R.id.mButtonClearAll);
        mRecyclerHistory = mLinearNavigationHistory.findViewById(R.id.mRecyclerViewHistory);
        Toolbar mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        progressLoad = new ProgressDialog(this);
        progressLoad.setMessage(getString(R.string.please_wait));
        progressLoad.setCancelable(false);

        handlerCode = new Handler();

        if (mCodeEditor != null) {
            if (sp.getBoolean("horizontalScrollKey", true)) { //Скролл по гаризонтали
                mCodeEditor.setHorizontallyScrolling(true);
            } else {
                mCodeEditor.setHorizontallyScrolling(false);
            }
        }

        //Очищаем весь список недавних файлов
        mButtonClearAll.setOnClickListener(p1 -> {
            filesHistory.clear();
            setUpdateHistory(filesHistory, EditorActivity.this);
            fileUpdateProject = "";
            mCodeEditor.setText("");
            mCodeEditor.clearUndoRedo();
            Data.showToast(EditorActivity.this, getString(R.string.cleared));
        });

        //Заполняем все необходимое
        fileProject = getIntent().getStringExtra("filePath");
        fileUpdateProject = fileProject;
        pathProject = new File(fileProject).getParent();
        pathUpdateProject = new File(fileProject).getParent();

        //Открываем файл
        setOpenFile(this, fileUpdateProject);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sp.getBoolean("clearHistoryKey", false)) { //Очищаем истрию, если пользователь не против
            filesHistory.clear();
        }
        runActivity = false;
        fileProject = "";
        fileUpdateProject = "";
        pathProject = "";
        pathUpdateProject = "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        runActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sp.getBoolean("autoSaveKey", true)) { //Автоматически сохраняем, если пользователь не против
            setSaveFile(this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_file_manager) { //Файловый менеджер
            FileManager.fileManager(this, pathProject, 1);
        }
        if (item.getItemId() == R.id.action_save) { //Сохранить
            setSaveFile(this);
        }
        if (item.getItemId() == R.id.action_undo) { //Отменить
            mCodeEditor.undo();
        }
        if (item.getItemId() == R.id.action_redo) { //Вернуть
            mCodeEditor.redo();
        }
        if (item.getItemId() == R.id.action_replace) { //Заменить
            CodeEditorData.replaceText(this, mCodeEditor);
        }
        if (item.getItemId() == R.id.action_run) { //Запустить
            startActivity(new Intent(this, RunActivity.class).putExtra("project", pathProject));
        }
        return true;
    }

    //Открываем файл
    public static void setOpenFile(final Context context, final String path) {
        try {
            mCodeEditor.type = LangSyntax.NONE;
            mCodeEditor.setAdapter(new SyntaxHTMLAdapter(context, R.layout.row_tokenizer, LangSyntax.none()));
            setUpdateSymbol(SyntaxUtils.ALL_symbol(), context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path.endsWith(LangSyntax.HTML) || path.endsWith(LangSyntax.HTM) || path.endsWith(LangSyntax.PHP) || path.endsWith(LangSyntax.XML)) {
            try {
                mCodeEditor.type = LangSyntax.HTML;
                mCodeEditor.setTokenizer(new TokenizerHTML());
                mCodeEditor.setAdapter(new SyntaxHTMLAdapter(context, R.layout.row_tokenizer, LanguageHTML.htmlTag()));
                setUpdateSymbol(SyntaxUtils.HTML_symbol(), context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (path.endsWith(LangSyntax.CSS) || path.endsWith(LangSyntax.SCSS)) {
            try {
                mCodeEditor.type = LangSyntax.CSS;
                mCodeEditor.setTokenizer(new TokenizerCSS());
                mCodeEditor.setAdapter(new SyntaxCSSAdapter(context, R.layout.row_tokenizer, LanguageCSS.cssProperty()));
                setUpdateSymbol(SyntaxUtils.CSS_symbol(), context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (path.endsWith(LangSyntax.JAVASCRIPT) || path.endsWith(LangSyntax.JAVASCRIPT2) || path.endsWith(LangSyntax.JAVA) || path.endsWith(LangSyntax.CLASS)) {
            try {
                mCodeEditor.type = LangSyntax.JAVASCRIPT;
                mCodeEditor.setTokenizer(new TokenizerJS());
                mCodeEditor.setAdapter(new SyntaxJSAdapter(context, R.layout.row_tokenizer, LanguageJS.js()));
                setUpdateSymbol(SyntaxUtils.JS_symbol(), context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRecyclerHistory.setClickable(false);
        mCodeEditor.setHint(R.string.please_wait);
        mCodeEditor.setText("");
        progressLoad.show();
        handlerCode.postDelayed(() -> {
            mRecyclerHistory.setClickable(true); //Включаем клики
            mCodeEditor.setHint(""); //Убираем подсказку
            fileUpdateProject = path; //Обнавляем открытый файл
            setAddHistory(path, context); //Добавляем файл в историю
            mCodeEditor.setText(DFManager.readFile(context, path)); //Открываем файл
            progressLoad.dismiss(); //Закрываем диалог
            mCodeEditor.clearUndoRedo(); //Очищаем недавние действия
        }, 500);
    }

    //Сохранение файла
    public static void setSaveFile(Context context) {
        DFManager.writeFile(context, mCodeEditor.getText().toString(), fileUpdateProject);
    }

    //Добавляем новый файл в историю
    public static void setAddHistory(final String path, final Context context) {
        ((AppCompatActivity) context).runOnUiThread(() -> {
            if (!filesHistory.contains(path)) {
                filesHistory.add(path);
                setUpdateHistory(filesHistory, context);
            } else {
                setUpdateHistory(filesHistory, context);
            }
        });
    }

    //Получаем список с недавними файлами
    public static void setUpdateHistory(ArrayList<String> array, Context context) {
        try {
            RecyclerView.LayoutManager mLayoutManagerHistory = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            mRecyclerHistory.setLayoutManager(mLayoutManagerHistory);
            RecyclerView.Adapter mAdapterHistory = new HistoryAdapter(array, context);
            mRecyclerHistory.setAdapter(mAdapterHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Получаем список с быстрыми символами
    public static void setUpdateSymbol(ArrayList<String> array, Context context) {
        try {
            RecyclerView.LayoutManager mLayoutManagerSymbol = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewSymbol.setLayoutManager(mLayoutManagerSymbol);
            RecyclerView.Adapter mAdapterSymbol = new SymbolAdapter(array, context);
            mRecyclerViewSymbol.setAdapter(mAdapterSymbol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}