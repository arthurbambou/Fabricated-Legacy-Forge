package cpw.mods.fml.common.versioning;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Stack;

public class ComparableVersion implements Comparable<ComparableVersion> {
    private String value;
    private String canonical;
    private ComparableVersion.ListItem items;

    public ComparableVersion(String version) {
        this.parseVersion(version);
    }

    public final void parseVersion(String version) {
        this.value = version;
        this.items = new ComparableVersion.ListItem();
        version = version.toLowerCase(Locale.ENGLISH);
        ComparableVersion.ListItem list = this.items;
        Stack<ComparableVersion.Item> stack = new Stack();
        stack.push(list);
        boolean isDigit = false;
        int startIndex = 0;

        for(int i = 0; i < version.length(); ++i) {
            char c = version.charAt(i);
            if (c == '.') {
                if (i == startIndex) {
                    list.add(ComparableVersion.IntegerItem.ZERO);
                } else {
                    list.add(parseItem(isDigit, version.substring(startIndex, i)));
                }

                startIndex = i + 1;
            } else if (c == '-') {
                if (i == startIndex) {
                    list.add(ComparableVersion.IntegerItem.ZERO);
                } else {
                    list.add(parseItem(isDigit, version.substring(startIndex, i)));
                }

                startIndex = i + 1;
                if (isDigit) {
                    list.normalize();
                    if (i + 1 < version.length() && Character.isDigit(version.charAt(i + 1))) {
                        list.add(list = new ComparableVersion.ListItem());
                        stack.push(list);
                    }
                }
            } else if (Character.isDigit(c)) {
                if (!isDigit && i > startIndex) {
                    list.add(new ComparableVersion.StringItem(version.substring(startIndex, i), true));
                    startIndex = i;
                }

                isDigit = true;
            } else {
                if (isDigit && i > startIndex) {
                    list.add(parseItem(true, version.substring(startIndex, i)));
                    startIndex = i;
                }

                isDigit = false;
            }
        }

        if (version.length() > startIndex) {
            list.add(parseItem(isDigit, version.substring(startIndex)));
        }

        while(!stack.isEmpty()) {
            list = (ComparableVersion.ListItem)stack.pop();
            list.normalize();
        }

        this.canonical = this.items.toString();
    }

    private static ComparableVersion.Item parseItem(boolean isDigit, String buf) {
        return (ComparableVersion.Item)(isDigit ? new ComparableVersion.IntegerItem(buf) : new ComparableVersion.StringItem(buf, false));
    }

    public int compareTo(ComparableVersion o) {
        return this.items.compareTo(o.items);
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(Object o) {
        return o instanceof ComparableVersion && this.canonical.equals(((ComparableVersion)o).canonical);
    }

    public int hashCode() {
        return this.canonical.hashCode();
    }

    private static class IntegerItem implements ComparableVersion.Item {
        private static final BigInteger BigInteger_ZERO = new BigInteger("0");
        private final BigInteger value;
        public static final ComparableVersion.IntegerItem ZERO = new ComparableVersion.IntegerItem();

        private IntegerItem() {
            this.value = BigInteger_ZERO;
        }

        public IntegerItem(String str) {
            this.value = new BigInteger(str);
        }

        @Override
        public int getType() {
            return 0;
        }

        @Override
        public boolean isNull() {
            return BigInteger_ZERO.equals(this.value);
        }

        @Override
        public int compareTo(ComparableVersion.Item item) {
            if (item == null) {
                return BigInteger_ZERO.equals(this.value) ? 0 : 1;
            } else {
                switch(item.getType()) {
                    case 0:
                        return this.value.compareTo(((ComparableVersion.IntegerItem)item).value);
                    case 1:
                        return 1;
                    case 2:
                        return 1;
                    default:
                        throw new RuntimeException("invalid item: " + item.getClass());
                }
            }
        }

        public String toString() {
            return this.value.toString();
        }
    }

    private interface Item {
        int INTEGER_ITEM = 0;
        int STRING_ITEM = 1;
        int LIST_ITEM = 2;

        int compareTo(ComparableVersion.Item item);

        int getType();

        boolean isNull();
    }

    private static class ListItem extends ArrayList<ComparableVersion.Item> implements ComparableVersion.Item {
        private ListItem() {
        }

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public boolean isNull() {
            return this.size() == 0;
        }

        void normalize() {
            ListIterator<ComparableVersion.Item> iterator = this.listIterator(this.size());

            while(iterator.hasPrevious()) {
                ComparableVersion.Item item = (ComparableVersion.Item)iterator.previous();
                if (!item.isNull()) {
                    break;
                }

                iterator.remove();
            }

        }

        @Override
        public int compareTo(ComparableVersion.Item item) {
            if (item == null) {
                if (this.size() == 0) {
                    return 0;
                } else {
                    ComparableVersion.Item first = (ComparableVersion.Item)this.get(0);
                    return first.compareTo(null);
                }
            } else {
                switch(item.getType()) {
                    case 0:
                        return -1;
                    case 1:
                        return 1;
                    case 2:
                        Iterator<ComparableVersion.Item> left = this.iterator();
                        Iterator<ComparableVersion.Item> right = ((ComparableVersion.ListItem)item).iterator();

                        while(left.hasNext() || right.hasNext()) {
                            ComparableVersion.Item l = left.hasNext() ? (ComparableVersion.Item)left.next() : null;
                            ComparableVersion.Item r = right.hasNext() ? (ComparableVersion.Item)right.next() : null;
                            int result = l == null ? -1 * r.compareTo(l) : l.compareTo(r);
                            if (result != 0) {
                                return result;
                            }
                        }

                        return 0;
                    default:
                        throw new RuntimeException("invalid item: " + item.getClass());
                }
            }
        }

        public String toString() {
            StringBuilder buffer = new StringBuilder("(");
            Iterator<ComparableVersion.Item> iter = this.iterator();

            while(iter.hasNext()) {
                buffer.append(iter.next());
                if (iter.hasNext()) {
                    buffer.append(',');
                }
            }

            buffer.append(')');
            return buffer.toString();
        }
    }

    private static class StringItem implements ComparableVersion.Item {
        private static final String[] QUALIFIERS = new String[]{"alpha", "beta", "milestone", "rc", "snapshot", "", "sp"};
        private static final List<String> _QUALIFIERS = Arrays.asList(QUALIFIERS);
        private static final Properties ALIASES = new Properties();
        private static final String RELEASE_VERSION_INDEX = String.valueOf(_QUALIFIERS.indexOf(""));
        private String value;

        public StringItem(String value, boolean followedByDigit) {
            if (followedByDigit && value.length() == 1) {
                switch(value.charAt(0)) {
                    case 'a':
                        value = "alpha";
                        break;
                    case 'b':
                        value = "beta";
                        break;
                    case 'm':
                        value = "milestone";
                }
            }

            this.value = ALIASES.getProperty(value, value);
        }

        @Override
        public int getType() {
            return 1;
        }

        @Override
        public boolean isNull() {
            return comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX) == 0;
        }

        public static String comparableQualifier(String qualifier) {
            int i = _QUALIFIERS.indexOf(qualifier);
            return i == -1 ? _QUALIFIERS.size() + "-" + qualifier : String.valueOf(i);
        }

        @Override
        public int compareTo(ComparableVersion.Item item) {
            if (item == null) {
                return comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX);
            } else {
                switch(item.getType()) {
                    case 0:
                        return -1;
                    case 1:
                        return comparableQualifier(this.value).compareTo(comparableQualifier(((ComparableVersion.StringItem)item).value));
                    case 2:
                        return -1;
                    default:
                        throw new RuntimeException("invalid item: " + item.getClass());
                }
            }
        }

        public String toString() {
            return this.value;
        }

        static {
            ALIASES.put("ga", "");
            ALIASES.put("final", "");
            ALIASES.put("cr", "rc");
        }
    }
}
