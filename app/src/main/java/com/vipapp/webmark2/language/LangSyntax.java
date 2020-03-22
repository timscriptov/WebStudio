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
package com.vipapp.webmark2.language;

import com.vipapp.webmark2.EditorActivity;

import java.util.ArrayList;

public class LangSyntax {

    public static String HTML = "html";
    public static String HTM = "htm";
    public static String XML = "xml";
    public static String PHP = "php";
    public static String JAVASCRIPT = "js";
    public static String JAVASCRIPT2 = "javascript";
    public static String JAVA = "java";
    public static String CLASS = "class";
    public static String CSS = "css";
    public static String SCSS = "scss";
    public static String NONE = "";

    public static ArrayList<String> none() throws Exception {
        return new ArrayList<>();
    }

    public static String checkHTML_TAG(String text, boolean value) {
        String tagStart = "";
        if (value == true) {
            tagStart = "<";
        }

        try {
            if (LanguageHTML.htmlTag().contains(text.toString())) {
                if (text.toString().equals("audio")) {
                    text = tagStart + "audio src=\"\"></audio>";
                } else {
                    if (text.toString().equals("area")) {
                        text = tagStart + "area href=\"\" alt=\"Text\">";
                    } else {
                        if (text.toString().equals("applet")) {
                            text = tagStart + "applet code=\"\">";
                        } else {
                            if (text.toString().equals("base")) {
                                text = tagStart + "base target=\"_blank\">";
                            } else {
                                if (text.toString().equals("link")) {
                                    text = tagStart + "link rel=\"stylesheet\" href=\"\">";
                                } else {
                                    if (text.toString().equals("meta")) {
                                        text = tagStart + "meta charset=\"utf-8\">";
                                    } else {
                                        if (text.toString().equals("input")) {
                                            text = tagStart + "input type=\"text\" name=\"text\">";
                                        } else {
                                            if (text.toString().equals("img")) {
                                                text = tagStart + "img src=\"\">";
                                            } else {
                                                if (text.toString().equals("etc") || text.toString().equals("br") || text.toString().equals("hr")) {
                                                    text = tagStart + text + ">";
                                                } else {
                                                    if (text.toString().equals("script")) {
                                                        text = tagStart + "script type=\"text/javascript\"></script>";
                                                    } else {
                                                        if (text.toString().equals("a")) {
                                                            text = tagStart + "a href=\"\"></a>";
                                                        } else {
                                                            if (text.toString().equals("style")) {
                                                                text = tagStart + "style type=\"text/css\"></style>";
                                                            } else {
                                                                if (text.toString().equals("html") && !EditorActivity.mCodeEditor.getText().toString().contains("<html>")) {
                                                                    text = tagStart + "!DOCTYPE html>\n<html>\n" + EditorActivity.mCodeEditor.TAB_SIZE + "<head>\n\n" + EditorActivity.mCodeEditor.TAB_SIZE + "</head>\n" + EditorActivity.mCodeEditor.TAB_SIZE + "<body>\n\n" + EditorActivity.mCodeEditor.TAB_SIZE + "</body>\n</html>";
                                                                } else {
                                                                    if (!text.toString().equals("")) {
                                                                        text = tagStart + text + ">" + "</" + text + ">";
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
}