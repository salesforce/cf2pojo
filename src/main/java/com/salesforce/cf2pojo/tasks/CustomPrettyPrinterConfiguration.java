/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.tasks;

import static com.github.javaparser.utils.Utils.isNullOrEmpty;

import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.printer.PrettyPrintVisitor;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

import java.util.Iterator;
import java.util.function.Function;

class CustomPrettyPrinterConfiguration extends PrettyPrinterConfiguration {
    private boolean arrayLiteralMembersOnSeparateLines = false;

    CustomPrettyPrinterConfiguration setArrayLiteralMembersOnSeparateLines(boolean arrayLiteralMembersOnSeparateLines) {
        this.arrayLiteralMembersOnSeparateLines = arrayLiteralMembersOnSeparateLines;
        return this;
    }

    @Override
    public Function<PrettyPrinterConfiguration, PrettyPrintVisitor> getVisitorFactory() {
        return configuration -> new PrettyPrintVisitor(configuration) {
            @Override
            public void visit(ArrayInitializerExpr n, Void arg) {
                if (arrayLiteralMembersOnSeparateLines) {
                    if (configuration.isPrintJavaDoc()) {
                        n.getComment().ifPresent(c -> c.accept(this, arg));
                    }
                    printer.print("{");
                    if (!isNullOrEmpty(n.getValues())) {
                        printer.println();
                        printer.indent();
                        for (final Iterator<Expression> i = n.getValues().iterator(); i.hasNext(); ) {
                            final Expression expr = i.next();
                            expr.accept(this, arg);
                            if (i.hasNext()) {
                                printer.println(",");
                            }
                        }
                        printer.println();
                        printer.unindent();
                    }
                    printer.print("}");
                } else {
                    super.visit(n, arg);
                }
            }
        };
    }
}
