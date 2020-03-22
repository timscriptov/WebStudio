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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mcal.webstudio.adapter.ProjectsAdapter;
import com.mcal.webstudio.app.Data;
import com.mcal.webstudio.manager.DFManager;
import com.mcal.webstudio.manager.FileManager;
import com.mcal.webstudio.manager.ProjectManager;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String TAG = "Check";

    private static RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data.themeApp(this);
        setContentView(R.layout.main);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        Toolbar mToolbar = findViewById(R.id.mToolbar);
        AppBarLayout mAppBarLayout = findViewById(R.id.mAppBarLayout);
        FloatingActionButton mFab = findViewById(R.id.mFab);

        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(p1 -> ProjectManager.createProject(MainActivity.this, 0));

        //Запрос на разрешение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Разрешение выданно
                Log.v(TAG, "Разрешение выданно");
                setCreateDefaultContent();
                setLoadListProjects(this);
            } else {
                //Делаем запрос на разрешение
                Log.v(TAG, "Делаем запрос на разрешение");
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Data.settingChange) {
            Data.settingChange = false;
            this.recreate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Разрешение: " + permissions[0] + " -/- " + grantResults[0]);
            setCreateDefaultContent();
            setLoadListProjects(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_file_manager) { //Файловый менеджер
            FileManager.fileManager(this, FileManager.MAIN_PATH, 0);
        }
        if (item.getItemId() == R.id.action_settings) { //Настройки
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    //Создание всего необходимого, для нормальной работы приложения
    private void setCreateDefaultContent() {

        //Создаем главную папку
        try {
            DFManager.createDirectory(DFManager.MAIN);
        } catch (Exception e) {
            Log.v(TAG, "Что-то пошло не так, в процессе создания главной папки, вот причина: " + e.getMessage());
        }

        //Создаем папку с будущеми проектами
        try {
            DFManager.createDirectory(DFManager.MAIN_PROJECTS);
        } catch (Exception e) {
            Log.v(TAG, "Что-то пошло не так, в процессе создания папки с будущеми проектами, вот причина: " + e.getMessage());
        }

        //Создаем файл, котрый скроет всю мультимедию от галерей и плееров...
        try {
            DFManager.createFile(DFManager.MAIN_NOMEDIA);
        } catch (Exception e) {
            Log.v(TAG, "Что-то пошло не так, в процессе создания фала .nomedia, вот причина: " + e.getMessage());
        }
    }

    //Загружаем список проектов в ресайкл
    public static void setLoadListProjects(Context context) {
        try {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.Adapter mAdapter = new ProjectsAdapter(ProjectManager.listProjects(), context);
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.v(TAG, "Что-то пошло не так, не удалось загрузить список проектов, вот причина: " + e.getMessage());
        }
    }
}