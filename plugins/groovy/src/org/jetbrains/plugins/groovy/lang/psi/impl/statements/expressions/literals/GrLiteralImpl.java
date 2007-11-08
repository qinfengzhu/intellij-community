/*
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveUtil;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import static org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.*;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementVisitor;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrExpressionImpl;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author ilyas
 */
public class GrLiteralImpl extends GrExpressionImpl implements GrLiteral {

  public GrLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public String toString() {
    return "Literal";
  }

  public PsiType getType() {
    IElementType elemType = getFirstChild().getNode().getElementType();
    if (elemType == mGSTRING_LITERAL || elemType == mSTRING_LITERAL || elemType == mREGEX_LITERAL) {
      return getTypeByFQName("java.lang.String");
    } else if (elemType == mNUM_INT) {
      return getTypeByFQName("java.lang.Integer");
    } else if (elemType == mNUM_LONG) {
      return getTypeByFQName("java.lang.Long");
    } else if (elemType == mNUM_FLOAT) {
      return getTypeByFQName("java.lang.Float");
    } else if (elemType == mNUM_DOUBLE) {
      return getTypeByFQName("java.lang.Double");
    } else if (elemType == mNUM_BIG_INT) {
      return getTypeByFQName("java.math.BigInteger");
    } else if (elemType == mNUM_BIG_DECIMAL) {
      return getTypeByFQName("java.math.BigDecimal");
    } else if (elemType == kFALSE || elemType == kTRUE) {
      return getTypeByFQName("java.lang.Boolean");
    }  else if (elemType == kNULL) {
      return PsiType.NULL;
    }

    return null;
  }

  public void accept(GroovyElementVisitor visitor) {
    visitor.visitLiteralExpression(this);
  }

  public Object getValue() {
    final PsiElement child = getFirstChild();
    IElementType elemType = child.getNode().getElementType();
    String text = child.getText();
    if (elemType == mNUM_INT) {
      try {
        return Integer.parseInt(text);
      } catch (NumberFormatException e) {}
    } else if (elemType == mNUM_LONG) {
      try {
        return Long.parseLong(text);
      } catch (NumberFormatException e) {}
    } else if (elemType == mNUM_FLOAT) {
      try {
        return Float.parseFloat(text);
      } catch (NumberFormatException e) {}
    } else if (elemType == mNUM_DOUBLE) {
      try {
        return Double.parseDouble(text);
      } catch (NumberFormatException e) {}
    } else if (elemType == mNUM_BIG_INT) {
      try {
        return new BigInteger(text);
      } catch (NumberFormatException e) {}
    } else if (elemType == mNUM_BIG_DECIMAL) {
      try {
        return new BigDecimal(text);
      } catch (NumberFormatException e) {}
    } else if (elemType == kFALSE) {
      return Boolean.FALSE;
    } else if (elemType == kTRUE) {
      return Boolean.TRUE;
    } else if (elemType == mSTRING_LITERAL) {
      if (!text.startsWith("'")) return null;
      text = text.substring(1);
      if (text.endsWith("'")) {
        text = text.substring(0, text.length() -1);
      }
      return StringUtil.unescapeStringCharacters(text);
    } else if (elemType == mGSTRING_LITERAL) {
      if (!text.startsWith("\"")) return null;
      text = text.substring(1);
      if (text.endsWith("\"")) {
        text = text.substring(0, text.length() -1);
      }
      return StringUtil.unescapeStringCharacters(text);
    }


    return null; //todo
  }

  @NotNull
  public PsiReference[] getReferences() {
    return ResolveUtil.getReferencesFromProviders(this, GrLiteral.class);
  }
}