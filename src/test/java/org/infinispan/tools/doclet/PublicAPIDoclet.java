// Ripped from https://github.com/infinispan/infinispan/blob/master/tools/src/main/java/org/infinispan/tools/doclet/PublicAPI.java
// modified to emit a file containing all classes marked as public
// See PDFXS-582

package org.infinispan.tools.doclet;

import com.sun.javadoc.*;
import com.sun.tools.doclets.standard.Standard;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// derived from the class of the same name in https://github.com/infinispan/infinispan,
// taken under the Apache v2 license
public class PublicAPIDoclet {
    private static final String PUBLIC_TAG = "@publicapi";
    private static final String PRIVATE_TAG = "@nodoc";

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) throws java.io.IOException {
        return Standard.validOptions(options, reporter);
    }

    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    public static int optionLength(String option) {
        return Standard.optionLength(option);
    }

    public static boolean start(RootDoc root) throws java.io.IOException {
        // blank lines are paragraph breaks, please
        for ( ClassDoc classdoc : root.classes())
            classdoc.setRawCommentText(classdoc.getRawCommentText().replaceAll("\\n\\n", "\n<p>\n"));

        return Standard.start((RootDoc) filter(root, RootDoc.class));
    }

    private static boolean isPublicAPI(Doc doc) {
        if (doc.tags(PRIVATE_TAG).length > 0)
            return false;
        if (doc instanceof ProgramElementDoc) {
            ProgramElementDoc peDoc = (ProgramElementDoc) doc;
            if (peDoc.isPrivate() || peDoc.isPackagePrivate()) return false;
            if (peDoc.containingClass() != null && peDoc.containingClass().tags(PUBLIC_TAG).length > 0)
                return true;
        }
        return doc.tags(PUBLIC_TAG).length > 0;
    }

    private static Object filter(Object obj, Class expect) {
        if (obj == null)
            return null;
        Class cls = obj.getClass();
        if (cls.getName().startsWith("com.sun.")) {
            return Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new FilterHandler(obj));
        } else if (obj instanceof Object[]) {
            Class componentType = expect.getComponentType();
            Object[] array = (Object[]) obj;
            List list = new ArrayList(array.length);
            for (int i = 0; i < array.length; i++) {
                Object entry = array[i];

                if ((entry instanceof Doc) && !isPublicAPI((Doc) entry))
                    continue;
                list.add(filter(entry, componentType));
            }
            return list.toArray((Object[]) Array.newInstance(componentType, list.size()));
        } else {
            return obj;
        }
    }

    private static class FilterHandler implements InvocationHandler {
        private Object target;

        public FilterHandler(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            // eliminate "unknown tags" warnings in javadoc tool std output
            if (methodName.equals("tags") && (args == null || args.length == 0)) {
                ArrayList<Tag> s = new ArrayList<Tag>();
                for (Tag tag : ((Doc)target).tags()) {
                    if (!(tag.name().equals(PUBLIC_TAG) || tag.name().equals(PRIVATE_TAG))) {
                        s.add(tag);
                    }
                }
                return s.toArray(new Tag[s.size()]);
            }

            if (args != null) {
                if (methodName.equals("compareTo") || methodName.equals("equals") || methodName.equals("overrides")
                        || methodName.equals("subclassOf")) {
                    args[0] = unwrap(args[0]);
                }
            }
            try {
                return filter(method.invoke(target, args), method.getReturnType());
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        private Object unwrap(Object proxy) {
            if (proxy instanceof Proxy)
                return ((FilterHandler) Proxy.getInvocationHandler(proxy)).target;
            return proxy;
        }
    }

}
