package cpw.mods.fml.common.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Language;

public class LanguageRegistry {
    private static final LanguageRegistry INSTANCE = new LanguageRegistry();
    private Map<String, Properties> modLanguageData = new HashMap();

    public LanguageRegistry() {
    }

    public static LanguageRegistry instance() {
        return INSTANCE;
    }

    public String getStringLocalization(String key) {
        return this.getStringLocalization(key, Language.getInstance().getCode());
    }

    public String getStringLocalization(String key, String lang) {
        String localizedString = "";
        Properties langPack = (Properties)this.modLanguageData.get(lang);
        if (langPack != null && langPack.getProperty(key) != null) {
            localizedString = langPack.getProperty(key);
        }

        return localizedString;
    }

    public void addStringLocalization(String key, String value) {
        this.addStringLocalization(key, "en_US", value);
    }

    public void addStringLocalization(String key, String lang, String value) {
        Properties langPack = (Properties)this.modLanguageData.get(lang);
        if (langPack == null) {
            langPack = new Properties();
            this.modLanguageData.put(lang, langPack);
        }

        langPack.put(key, value);
    }

    public void addStringLocalization(Properties langPackAdditions) {
        this.addStringLocalization(langPackAdditions, "en_US");
    }

    public void addStringLocalization(Properties langPackAdditions, String lang) {
        Properties langPack = (Properties)this.modLanguageData.get(lang);
        if (langPack == null) {
            langPack = new Properties();
            this.modLanguageData.put(lang, langPack);
        }

        if (langPackAdditions != null) {
            langPack.putAll(langPackAdditions);
        }

    }

    public static void reloadLanguageTable() {
        String lang = Language.getInstance().getCode();
        Language.getInstance().code = null;
        Language.getInstance().setCode(lang);
    }

    public void addNameForObject(Object objectToName, String lang, String name) {
        String objectName;
        if (objectToName instanceof Item) {
            objectName = ((Item)objectToName).getTranslationKey();
        } else if (objectToName instanceof Block) {
            objectName = ((Block)objectToName).getTranslationKey();
        } else {
            if (!(objectToName instanceof ItemStack)) {
                throw new IllegalArgumentException(String.format("Illegal object for naming %s", objectToName));
            }

            objectName = ((ItemStack)objectToName).getItem().getTranslationKey((ItemStack)objectToName);
        }

        objectName = objectName + ".name";
        this.addStringLocalization(objectName, lang, name);
    }

    public static void addName(Object objectToName, String name) {
        instance().addNameForObject(objectToName, "en_US", name);
    }

    public void loadLanguageTable(Properties languagePack, String lang) {
        Properties usPack = (Properties)this.modLanguageData.get("en_US");
        if (usPack != null) {
            languagePack.putAll(usPack);
        }

        Properties langPack = (Properties)this.modLanguageData.get(lang);
        if (langPack != null) {
            languagePack.putAll(langPack);
        }
    }

    public void loadLocalization(String localizationFile, String lang, boolean isXML) {
        this.loadLocalization(this.getClass().getResource(localizationFile), lang, isXML);
    }

    public void loadLocalization(URL localizationFile, String lang, boolean isXML) {
        InputStream langStream = null;
        Properties langPack = new Properties();

        try {
            langStream = localizationFile.openStream();
            if (isXML) {
                langPack.loadFromXML(langStream);
            } else {
                langPack.load(langStream);
            }

            this.addStringLocalization(langPack, lang);
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (langStream != null) {
                    langStream.close();
                }
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

    }
}
