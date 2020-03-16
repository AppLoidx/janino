
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2016 Arno Unkrig. All rights reserved.
 * Copyright (c) 2015-2016 TIBCO Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino.tests;

import java.util.EnumSet;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.IScriptEvaluator;
import org.codehaus.janino.JaninoOption;
import org.codehaus.janino.ScriptEvaluator;
import org.junit.Assert;
import org.junit.Test;

// SUPPRESS CHECKSTYLE JavadocMethod:9999

/**
 * Unit tests for the {@link ScriptEvaluator}.
 */
public
class ScriptEvaluatorTest {

    @Test public void
    testSimpleLocalMethod() throws Exception {
        Assert.assertEquals(7, new ScriptEvaluator((
            ""
            + "return meth();\n"
            + "static int meth() { return 7; }\n"
        ), int.class).evaluate(null));
    }

    @Test public void
    testOverlappingLocalMethods1() throws Exception {
        IScriptEvaluator se = new ScriptEvaluator();
        se.cook(new String[] {
            "void meth1() {}\n",
            "void meth2() {}\n"
        });
    }

    @Test public void
    testOverlappingLocalMethods2() throws Exception {
        IScriptEvaluator se = new ScriptEvaluator();
        try {
            se.cook(new String[] {
                "void meth() {}\n",
                "void meth() {}\n"
            });
            Assert.fail("Compilation exception expected");
        } catch (ClassFormatError cfe) {
            Assert.assertTrue(cfe.getMessage(), cfe.getMessage().contains("Duplicate method"));
        }
    }

    @Test public void
    testAccessibilityOfClassMembers1() throws Exception {

        // Without
        new ScriptEvaluator().cook(
            ""
            + "class MyClass {\n"
            + "    private int pri;\n"
            + "}\n"
            + "\n"
            + "new MyClass().pri = 7;\n"
        );
    }

    @Test public void
    testAccessibilityOfClassMembers2() throws Exception {
        try {
            new ScriptEvaluator().options(
                EnumSet.of(JaninoOption.PRIVATE_MEMBERS_OF_ENCLOSING_AND_ENCLOSED_TYPES_INACCESSIBLE)
            ).cook(
                ""
                + "class MyClass {\n"
                + "    private int pri;\n"
                + "}\n"
                + "\n"
                + "new MyClass().pri = 7;\n"
            );
            Assert.fail("CompileException expected");
        } catch (CompileException ce) {
            Assert.assertTrue(ce.getMessage().contains("Private member cannot be accessed"));
        }
    }
}
