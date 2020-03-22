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
package com.mcal.webstudio.language;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SyntaxUtils {

    //SYMBOL
    public static ArrayList<String> HTML_symbol() {
        ArrayList<String> symbolList = new ArrayList<>();
        symbolList.add("TAB");
        symbolList.add("<");
        symbolList.add(">");
        symbolList.add("/");
        symbolList.add("\"");
        symbolList.add("=");
        symbolList.add("(");
        symbolList.add(")");
        symbolList.add("{");
        symbolList.add("}");
        symbolList.add(":");
        symbolList.add(";");
        symbolList.add(".");
        symbolList.add(",");
        return symbolList;
    }

    public static ArrayList<String> CSS_symbol() {
        ArrayList<String> symbolList = new ArrayList<>();
        symbolList.add("TAB");
        symbolList.add("{");
        symbolList.add("}");
        symbolList.add(":");
        symbolList.add(";");
        symbolList.add("(");
        symbolList.add(")");
        symbolList.add("<");
        symbolList.add(">");
        symbolList.add("\"");
        symbolList.add("#");
        symbolList.add(".");
        symbolList.add(",");
        symbolList.add("=");
        symbolList.add("[");
        symbolList.add("]");
        return symbolList;
    }

    public static ArrayList<String> JS_symbol() {
        ArrayList<String> symbolList = new ArrayList<>();
        symbolList.add("TAB");
        symbolList.add("{");
        symbolList.add("}");
        symbolList.add("(");
        symbolList.add(")");
        symbolList.add(";");
        symbolList.add(".");
        symbolList.add(",");
        symbolList.add("\"");
        symbolList.add("'");
        symbolList.add(":");
        symbolList.add("+");
        symbolList.add("-");
        symbolList.add("=");
        symbolList.add("[");
        symbolList.add("]");
        symbolList.add("<");
        symbolList.add(">");
        return symbolList;
    }

    public static ArrayList<String> ALL_symbol() {
        ArrayList<String> symbolList = new ArrayList<>();
        symbolList.add("TAB");
        symbolList.add("{");
        symbolList.add("}");
        symbolList.add("(");
        symbolList.add(")");
        symbolList.add(";");
        symbolList.add(".");
        symbolList.add(",");
        symbolList.add("\"");
        symbolList.add("'");
        symbolList.add(":");
        symbolList.add("+");
        symbolList.add("-");
        symbolList.add("=");
        symbolList.add("[");
        symbolList.add("]");
        symbolList.add("<");
        symbolList.add(">");
        return symbolList;
    }

    //HTML
    public static Pattern HTML_START_ELEMENT = Pattern.compile("(?<=<)[^/\\s>?]+(?=(?:>|\\s))");
    public static Pattern HTML_END_ELEMENT = Pattern.compile("(?<=</).*?(?=>)");
    public static Pattern HTML_COMMENTS = Pattern.compile("(?:<!--)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?-->");
    public static Pattern HTML_COMMENTS_TWO = Pattern.compile("(?:<!)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?>");
    public static Pattern HTML_COMMENTS_XML = Pattern.compile("(?:<\\?)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\?>");
    public static Pattern HTML_START_TAG = Pattern.compile("(?<=\\s)[^:\\s=]*?(?=:)");
    public static Pattern HTML_END_TAG = Pattern.compile("\\b.\\w*?(?:=)");
    public static Pattern HTML_STRING_PATTERN = Pattern.compile("\".*?\"");

    //CSS
    public static Pattern CSS_START_TAG = Pattern.compile("(?<=\\s)[^:\\s=]*?(?=:)");
    public static Pattern CSS_END_TAG = Pattern.compile("\\b.\\w*?(?:=)");
    public static Pattern CSS_COMMENTS_SINGLE_LINE = Pattern.compile("(//)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$).*");
    public static Pattern CSS_COMMENTS_MULTI_LINE = Pattern.compile("(?:/\\*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\*/");
    public static Pattern CSS_KEYWORDS_PATTERN = Pattern.compile("\\b(px|vh|flex|center|block|inline-block|bold|normal|default|both|none|absolute|fixed|relative|static|border-box|transform)\\b");
    public static Pattern CSS_STRING_PATTERN = Pattern.compile("\".*?\"");

    //JS
    public static Pattern JS_KEYWORDS_PATTERN = Pattern.compile("\\b(function|int|on|var|let|navigator|Activity|AppCompatActivity|Bundle|R|String|float)\\b");
    public static Pattern JS_KEYWORDS_PATTERN_TWO = Pattern.compile("\\b(if|return|boolean|new|for|window|else|document|switch|case|void|super|break|location|import|package|protected|public|private|static|class|this|extends)\\b");
    public static Pattern JS_KEYWORDS_PATTERN_THREE = Pattern.compile("\\b(@Override|@NanNull)\\b");
    public static Pattern JS_BUILTIN_PATTERN = Pattern.compile("\\b(true|false|null)\\b");
    public static Pattern JS_SYMBOL_PATTERN = Pattern.compile("\\b()\\b");
    public static Pattern JS_SYMBOL_PATTERN_TWO = Pattern.compile("\\b(\\.)\\b");
    public static Pattern JS_COMMENTS_SINGLE_LINE = Pattern.compile("(//)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$).*");
    public static Pattern JS_COMMENTS_MULTI_LINE = Pattern.compile("(?:/\\*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\*/");
    public static Pattern JS_STRING_PATTERN = Pattern.compile("\".*?\"");
    public static Pattern JS_STRING_PATTERN_TWO = Pattern.compile("\'.*?\'");
}