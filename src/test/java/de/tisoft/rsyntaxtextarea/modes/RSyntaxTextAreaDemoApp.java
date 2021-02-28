/*-
 * #%L
 * RSyntaxTextArea ANTLR 4 Extension
 * %%
 * Copyright (C) 2021 Markus Heberling
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.tisoft.rsyntaxtextarea.modes;

import de.tisoft.rsyntaxtextarea.modes.demo.AssemblerAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.AssemblerTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.CAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.CTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.ErlangAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.ErlangTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.GoAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.GoTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.JSONAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.JSONTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.Java9AntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.Java9TokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.MySqlAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.MySqlTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.Python3AntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.Python3TokenMaker;
import de.tisoft.rsyntaxtextarea.modes.demo.XMLAntlrParser;
import de.tisoft.rsyntaxtextarea.modes.demo.XMLTokenMaker;
import de.tisoft.rsyntaxtextarea.parser.antlr.DelegatingParser;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.WindowConstants;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.demo.DemoRootPane;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextScrollPane;

public final class RSyntaxTextAreaDemoApp extends JFrame {

  private static final Method addSyntaxItemMethod;

  static {
    try {
      addSyntaxItemMethod =
          DemoRootPane.class.getDeclaredMethod(
              "addSyntaxItem",
              String.class,
              String.class,
              String.class,
              ButtonGroup.class,
              JMenu.class);
      addSyntaxItemMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private RSyntaxTextAreaDemoApp() {
    try {
      Constructor<DemoRootPane> constructor = DemoRootPane.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      DemoRootPane demoRootPane = constructor.newInstance();
      setRootPane(demoRootPane);

      DelegatingParser delegatingParser = new DelegatingParser();
      ((RSyntaxTextArea)
              ((RTextScrollPane) demoRootPane.getContentPane().getComponent(0)).getTextArea())
          .addParser(delegatingParser);

      JMenu menu = demoRootPane.getJMenuBar().getMenu(0);
      ButtonGroup bg = ((DefaultButtonModel) menu.getItem(0).getModel()).getGroup();
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "Assembler (Antlr)",
          "Assembler6502.txt",
          "antlr/asm6502",
          bg,
          menu,
          AssemblerTokenMaker.class,
          AssemblerAntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "C (Antlr)",
          "CExample.txt",
          "antlr/c",
          bg,
          menu,
          CTokenMaker.class,
          CAntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "Erlang (Antlr)",
          "/ErlangExample.txt",
          "antlr/erlang",
          bg,
          menu,
          ErlangTokenMaker.class,
          ErlangAntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "Go (Antlr)",
          "GoExample.txt",
          "antlr/go",
          bg,
          menu,
          GoTokenMaker.class,
          GoAntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "Java (Antlr)",
          "JavaExample.txt",
          "antlr/java9",
          bg,
          menu,
          Java9TokenMaker.class,
          Java9AntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "JSON (Antlr)",
          "JsonExample.txt",
          "antlr/json",
          bg,
          menu,
          JSONTokenMaker.class,
          JSONAntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "Python (Antlr)",
          "PythonExample.txt",
          "antlr/python",
          bg,
          menu,
          Python3TokenMaker.class,
          Python3AntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "SQL (Antlr)",
          "SQLExample.txt",
          "antlr/mysql",
          bg,
          menu,
          MySqlTokenMaker.class,
          MySqlAntlrParser.class);
      addSyntaxItem(
          demoRootPane,
          delegatingParser,
          "XML (Antlr)",
          "XMLExample.txt",
          "antlr/xml",
          bg,
          menu,
          XMLTokenMaker.class,
          XMLAntlrParser.class);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      setTitle("RSyntaxTextArea Demo Application");
      pack();
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private void addSyntaxItem(
      DemoRootPane demoRootPane,
      DelegatingParser delegatingParser,
      String name,
      String res,
      String style,
      ButtonGroup bg,
      JMenu menu,
      Class<? extends TokenMaker> tokenMakerClass,
      Class<? extends Parser> parserBase)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {

    // register our token maker
    ((AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance())
        .putMapping(style, tokenMakerClass.getName(), tokenMakerClass.getClassLoader());

    // register our parser
    delegatingParser.addParser(style, parserBase.newInstance());

    // add the menu entry
    addSyntaxItemMethod.invoke(demoRootPane, name, res, style, bg, menu);
  }

  public static void main(String[] args) {
    new RSyntaxTextAreaDemoApp().setVisible(true);
  }
}
