/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.jreadline.edit;

import org.jboss.jreadline.JReadlineTestCase;
import org.jboss.jreadline.TestBuffer;

import java.io.IOException;

/**
 * Test ViEditMode
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ViModeTest extends JReadlineTestCase {

    public ViModeTest(String test) {
        super(test);
    }

    public void testSimpleMovementAndEdit() throws Exception {
        TestBuffer b = new TestBuffer("1234");

        b.append(TestBuffer.ESCAPE) // esc
                .append("x") // x
                .append(TestBuffer.getNewLine()); // enter

        assertEqualsViMode("123", b);

        b = new TestBuffer("1234");
        b.append(TestBuffer.ESCAPE) // esc
                .append("h") // h
                .append("s") // s
                .append("5")
                .append(TestBuffer.getNewLine()); // enter
        assertEqualsViMode("1254", b);


        b = new TestBuffer("1234");
        b.append(TestBuffer.ESCAPE) // esc
                .append("0") // 0
                .append("x") // x
                .append(TestBuffer.getNewLine()); // enter

        assertEqualsViMode("234", b);

        b = new TestBuffer("1234");
        b.append(TestBuffer.ESCAPE) // esc
                .append("0")
                .append("x")
                .append("l")
                .append("a")
                .append("5")
                .append(TestBuffer.getNewLine());

        assertEqualsViMode("2354", b);
    }

    public void testWordMovementAndEdit() throws Exception {
        TestBuffer b = new TestBuffer("foo   bar...  Foo-Bar.");
        b.append(TestBuffer.ESCAPE)
                .append("B")
                .append("d").append("b") // db
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("foo   barFoo-Bar.", b);

        b = new TestBuffer("foo   bar...  Foo-Bar.");
        b.append(TestBuffer.ESCAPE)
                .append("0")
                .append("W")
                .append("W")
                .append("d").append("W")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("foo   bar...  ", b);

        b = new TestBuffer("foo   bar...   Foo-Bar.");
        b.append(TestBuffer.ESCAPE)
                .append("0")
                .append("w")
                .append("w")
                .append("d").append("W")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("foo   barFoo-Bar.", b);

        b = new TestBuffer("foo   bar...   Foo-Bar.");
        b.append(TestBuffer.ESCAPE)
                .append("B")
                .append("d").append("B")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("foo   Foo-Bar.", b);

        b = new TestBuffer("foo   bar...   Foo-Bar.");
        b.append(TestBuffer.ESCAPE)
                .append("0")
                .append("w").append("w")
                .append("i")
                .append("-bar")
                .append(TestBuffer.ESCAPE)
                .append("B")
                .append("d").append("w") //dw
                .append("x") // x
                .append("d").append("B") //dB
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("bar...   Foo-Bar.", b);
    }

    public void testRepeatAndEdit() throws IOException {
        TestBuffer b = new TestBuffer("/cd /home/foo; ls; cd Desktop; ls ../");
        b.append(TestBuffer.ESCAPE)
                .append("0")
                .append("w").append("w").append("w").append("w").append("w")
                .append("c").append("w")
                .append("bar")
                .append(TestBuffer.ESCAPE)
                .append("W")
                .append("d").append("w")
                .append(".")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("/cd /home/bar; cd Desktop; ls ../", b);

        b = new TestBuffer("/cd /home/foo; ls; cd Desktop; ls ../");
        b.append(TestBuffer.ESCAPE)
                .append("B")
                .append("D")
                .append("B")
                .append(".")
                .append("B")
                .append(".")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("/cd /home/foo; ls; cd ", b);
    }

    public void testTildeAndEdit() throws IOException {
        TestBuffer b = new TestBuffer("apt-get install vIM");
        b.append(TestBuffer.ESCAPE)
                .append("b")
                .append("~").append("~").append("~")
                .append("0")
                .append("w").append("w")
                .append("c").append("w")
                .append("cache")
                .append(TestBuffer.ESCAPE)
                .append("w")
                .append("c").append("w")
                .append("search")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("apt-cache search Vim", b);
    }

    public void testPasteAndEdit() throws IOException {
        TestBuffer b = new TestBuffer("apt-get install vIM");
        b.append(TestBuffer.ESCAPE)
                .append("0")
                .append("dW")
                .append("w")
                .append("P") //yank before
                .append("W")
                .append("yw") // add word to buffer
                .append("$")
                .append("p")
                .append(TestBuffer.getNewLine());
        assertEqualsViMode("install apt-get vIMvIM", b);

    }

    public void testSearch() throws IOException {
        TestBuffer b = new TestBuffer();
        b.append("asdf jkl").append(TestBuffer.getNewLine());
        b.append("footing").append(TestBuffer.getNewLine());
        int PREV_SEARCH = 18;
        b.append(PREV_SEARCH).append("a").append(TestBuffer.getNewLine());

        assertEqualsViMode("asdf jkl", b);

        b.append(PREV_SEARCH).append("ewsa").append(TestBuffer.getNewLine());
    }

}
