package net.minecraftforge.common;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Property.Type;

public class Configuration {
    private static boolean[] configBlocks = new boolean[Block.BLOCKS.length];
    private static boolean[] configItems = new boolean[Item.ITEMS.length];
    private static final int ITEM_SHIFT = 256;
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_BLOCK = "block";
    public static final String CATEGORY_ITEM = "item";
    public static final String ALLOWED_CHARS = "._-";
    public static final String DEFAULT_ENCODING = "UTF-8";
    private static final CharMatcher allowedProperties = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.anyOf("._-"));
    File file;
    public Map<String, Map<String, Property>> categories = new TreeMap();
    private Map<String, String> customCategoryComments = Maps.newHashMap();
    private boolean caseSensitiveCustomCategories;
    public String defaultEncoding = "UTF-8";

    public Configuration(File file) {
        this.file = file;
    }

    public Configuration(File file, boolean caseSensitiveCustomCategories) {
        this(file);
        this.caseSensitiveCustomCategories = caseSensitiveCustomCategories;
    }

    public Property getBlock(String key, int defaultID) {
        return this.getBlock("block", key, defaultID);
    }

    public Property getBlock(String category, String key, int defaultID) {
        Property prop = this.get(category, key, -1);
        if (prop.getInt() != -1) {
            configBlocks[prop.getInt()] = true;
            return prop;
        } else if (Block.BLOCKS[defaultID] == null && !configBlocks[defaultID]) {
            prop.value = Integer.toString(defaultID);
            configBlocks[defaultID] = true;
            return prop;
        } else {
            for(int j = configBlocks.length - 1; j > 0; --j) {
                if (Block.BLOCKS[j] == null && !configBlocks[j]) {
                    prop.value = Integer.toString(j);
                    configBlocks[j] = true;
                    return prop;
                }
            }

            throw new RuntimeException("No more block ids available for " + key);
        }
    }

    public Property getItem(String key, int defaultID) {
        return this.getItem("item", key, defaultID);
    }

    public Property getItem(String category, String key, int defaultID) {
        Property prop = this.get(category, key, -1);
        int defaultShift = defaultID + 256;
        if (prop.getInt() != -1) {
            configItems[prop.getInt() + 256] = true;
            return prop;
        } else if (Item.ITEMS[defaultShift] == null && !configItems[defaultShift] && defaultShift > Block.BLOCKS.length) {
            prop.value = Integer.toString(defaultID);
            configItems[defaultShift] = true;
            return prop;
        } else {
            for(int x = configItems.length - 1; x >= 256; --x) {
                if (Item.ITEMS[x] == null && !configItems[x]) {
                    prop.value = Integer.toString(x - 256);
                    configItems[x] = true;
                    return prop;
                }
            }

            throw new RuntimeException("No more item ids available for " + key);
        }
    }

    public Property get(String category, String key, int defaultValue) {
        Property prop = this.get(category, key, Integer.toString(defaultValue), Type.INTEGER);
        if (!prop.isIntValue()) {
            prop.value = Integer.toString(defaultValue);
        }

        return prop;
    }

    public Property get(String category, String key, boolean defaultValue) {
        Property prop = this.get(category, key, Boolean.toString(defaultValue), Type.BOOLEAN);
        if (!prop.isBooleanValue()) {
            prop.value = Boolean.toString(defaultValue);
        }

        return prop;
    }

    public Property get(String category, String key, String defaultValue) {
        return this.get(category, key, defaultValue, Type.STRING);
    }

    public Property get(String category, String key, String defaultValue, Type type) {
        if (!this.caseSensitiveCustomCategories) {
            category = category.toLowerCase(Locale.ENGLISH);
        }

        Map<String, Property> source = (Map)this.categories.get(category);
        if (source == null) {
            source = new TreeMap();
            this.categories.put(category, source);
        }

        if (source.containsKey(key)) {
            return (Property)source.get(key);
        } else if (defaultValue != null) {
            Property prop = new Property(key, defaultValue, type);
            source.put(key, prop);
            return prop;
        } else {
            return null;
        }
    }

    public boolean hasCategory(String category) {
        return this.categories.get(category) != null;
    }

    public boolean hasKey(String category, String key) {
        Map<String, Property> cat = (Map)this.categories.get(category);
        return cat != null && cat.get(key) != null;
    }

    public void load() {
        BufferedReader buffer = null;

        try {
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }

            if (this.file.exists() || this.file.createNewFile()) {
                if (this.file.canRead()) {
                    Configuration.UnicodeInputStreamReader input = new Configuration.UnicodeInputStreamReader(
                            new FileInputStream(this.file), this.defaultEncoding
                    );
                    this.defaultEncoding = input.getEncoding();
                    buffer = new BufferedReader(input);
                    Map<String, Property> currentMap = null;

                    boolean quoted;
                    do {
                        String line = buffer.readLine();
                        if (line == null) {
                            return;
                        }

                        int nameStart = -1;
                        int nameEnd = -1;
                        boolean skip = false;
                        quoted = false;

                        for(int i = 0; i < line.length() && !skip; ++i) {
                            if (Character.isLetterOrDigit(line.charAt(i)) || "._-".indexOf(line.charAt(i)) != -1 || quoted && line.charAt(i) != '"') {
                                if (nameStart == -1) {
                                    nameStart = i;
                                }

                                nameEnd = i;
                            } else if (!Character.isWhitespace(line.charAt(i))) {
                                switch(line.charAt(i)) {
                                    case '"':
                                        if (quoted) {
                                            quoted = false;
                                        }

                                        if (!quoted && nameStart == -1) {
                                            quoted = true;
                                        }
                                        break;
                                    case '#':
                                        skip = true;
                                        break;
                                    case '=':
                                        String propertyName = line.substring(nameStart, nameEnd + 1);
                                        if (currentMap == null) {
                                            throw new RuntimeException("property " + propertyName + " has no scope");
                                        }

                                        Property prop = new Property();
                                        prop.setName(propertyName);
                                        prop.value = line.substring(i + 1);
                                        i = line.length();
                                        currentMap.put(propertyName, prop);
                                        break;
                                    case '{':
                                        String scopeName = line.substring(nameStart, nameEnd + 1);
                                        currentMap = (Map)this.categories.get(scopeName);
                                        if (currentMap == null) {
                                            currentMap = new TreeMap();
                                            this.categories.put(scopeName, currentMap);
                                        }
                                        break;
                                    case '}':
                                        currentMap = null;
                                        break;
                                    default:
                                        throw new RuntimeException("unknown character " + line.charAt(i));
                                }
                            }
                        }
                    } while(!quoted);

                    throw new RuntimeException("unmatched quote");
                }

                return;
            }
        } catch (IOException var22) {
            var22.printStackTrace();
            return;
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException var21) {
                }
            }

        }

    }

    public void save() {
        try {
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }

            if (!this.file.exists() && !this.file.createNewFile()) {
                return;
            }

            if (this.file.canWrite()) {
                FileOutputStream fos = new FileOutputStream(this.file);
                BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, this.defaultEncoding));
                buffer.write("# Configuration file\r\n");
                buffer.write("# Generated on " + DateFormat.getInstance().format(new Date()) + "\r\n");
                buffer.write("\r\n");

                for(Entry<String, Map<String, Property>> category : this.categories.entrySet()) {
                    buffer.write("####################\r\n");
                    buffer.write("# " + (String)category.getKey() + " \r\n");
                    if (this.customCategoryComments.containsKey(category.getKey())) {
                        buffer.write("#===================\r\n");
                        String comment = (String)this.customCategoryComments.get(category.getKey());
                        Splitter splitter = Splitter.onPattern("\r?\n");

                        for(String commentLine : splitter.split(comment)) {
                            buffer.write("# ");
                            buffer.write(commentLine + "\r\n");
                        }
                    }

                    buffer.write("####################\r\n\r\n");
                    String catKey = (String)category.getKey();
                    if (!allowedProperties.matchesAllOf(catKey)) {
                        catKey = '"' + catKey + '"';
                    }

                    buffer.write(catKey + " {\r\n");
                    this.writeProperties(buffer, ((Map)category.getValue()).values());
                    buffer.write("}\r\n\r\n");
                }

                buffer.close();
                fos.close();
            }
        } catch (IOException var9) {
            var9.printStackTrace();
        }

    }

    public void addCustomCategoryComment(String category, String comment) {
        if (!this.caseSensitiveCustomCategories) {
            category = category.toLowerCase(Locale.ENGLISH);
        }

        this.customCategoryComments.put(category, comment);
    }

    private void writeProperties(BufferedWriter buffer, Collection<Property> props) throws IOException {
        for(Property property : props) {
            if (property.comment != null) {
                Splitter splitter = Splitter.onPattern("\r?\n");

                for(String commentLine : splitter.split(property.comment)) {
                    buffer.write("   # " + commentLine + "\r\n");
                }
            }

            String propName = property.getName();
            if (!allowedProperties.matchesAllOf(propName)) {
                propName = '"' + propName + '"';
            }

            buffer.write("   " + propName + "=" + property.value);
            buffer.write("\r\n");
        }

    }

    static {
        Arrays.fill(configBlocks, false);
        Arrays.fill(configItems, false);
    }

    public static class UnicodeInputStreamReader extends Reader {
        private final InputStreamReader input;
        private final String defaultEnc;

        public UnicodeInputStreamReader(InputStream source, String encoding) throws IOException {
            this.defaultEnc = encoding;
            String enc = encoding;
            byte[] data = new byte[4];
            PushbackInputStream pbStream = new PushbackInputStream(source, data.length);
            int read = pbStream.read(data, 0, data.length);
            int size = 0;
            int bom16 = (data[0] & 255) << 8 | data[1] & 255;
            int bom24 = bom16 << 8 | data[2] & 255;
            int bom32 = bom24 << 8 | data[3] & 255;
            if (bom24 == 15711167) {
                enc = "UTF-8";
                size = 3;
            } else if (bom16 == 65279) {
                enc = "UTF-16BE";
                size = 2;
            } else if (bom16 == 65534) {
                enc = "UTF-16LE";
                size = 2;
            } else if (bom32 == 65279) {
                enc = "UTF-32BE";
                size = 4;
            } else if (bom32 == -131072) {
                enc = "UTF-32LE";
                size = 4;
            }

            if (size < read) {
                pbStream.unread(data, size, read - size);
            }

            this.input = new InputStreamReader(pbStream, enc);
        }

        public String getEncoding() {
            return this.input.getEncoding();
        }

        public int read(char[] cbuf, int off, int len) throws IOException {
            return this.input.read(cbuf, off, len);
        }

        public void close() throws IOException {
            this.input.close();
        }
    }
}
