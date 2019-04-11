package com.enhtmv.moli.subproject;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.util.HostUtil;
import com.enhtmv.sublib.work.BolanOrange;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testBolanOrangeCall() {

        SubCall call = new BolanOrange();

        call.setLog(true);
        call.setProxy(HostUtil.proxy());


        call.call();

    }

}