/*
 * Copyright (c) 2008, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the Classpath exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.*/
 

package com.imooc.monitor_tuning.chapter4;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.Self;
import com.sun.btrace.annotations.TLS;

@BTrace 
public class PrintOnThrow {    
    // store current exception in a thread local
    // variable (@TLS annotation). Note that we can't
    // store it in a global variable!
    @TLS 
    static Throwable currentException;

    // introduce probe into every constructor of java.lang.Throwable
    // class and store "this" in the thread local variable.
    @OnMethod(
        clazz="java.lang.Throwable",
        method="<init>"
    )
    public static void onthrow(@Self Throwable self) {//new Throwable()
        currentException = self;
    }

    @OnMethod(
        clazz="java.lang.Throwable",
        method="<init>"
    )
    public static void onthrow1(@Self Throwable self, String s) {//new Throwable(String msg)
        currentException = self;
    }

    @OnMethod(
        clazz="java.lang.Throwable",
        method="<init>"
    )
    public static void onthrow1(@Self Throwable self, String s, Throwable cause) {//new Throwable(String msg, Throwable cause)
        currentException = self;
    }

    @OnMethod(
        clazz="java.lang.Throwable",
        method="<init>"
    )
    public static void onthrow2(@Self Throwable self, Throwable cause) {//new Throwable(Throwable cause)
        currentException = self;
    }

    // when any constructor of java.lang.Throwable returns
    // print the currentException's stack trace.
    @OnMethod(
        clazz="java.lang.Throwable",
        method="<init>",
        location=@Location(Kind.RETURN)
    )
    public static void onthrowreturn() {
        if (currentException != null) {
        	BTraceUtils.Threads.jstack(currentException);
        	BTraceUtils.println("=====================");
            currentException = null;
        }
    }
}
